package com.tisawesomeness.minecord.bootstrap;

import com.tisawesomeness.minecord.common.*;
import com.tisawesomeness.minecord.common.config.ConfigReader;
import com.tisawesomeness.minecord.common.config.InvalidConfigException;
import com.tisawesomeness.minecord.common.util.Either;
import com.tisawesomeness.minecord.common.util.IO;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient.Builder;
import org.jetbrains.annotations.Nullable;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.EnumSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Starts the bot. See {@link Bot} for details on the boot process.
 */
@Slf4j
@RequiredArgsConstructor
public final class Bootstrap implements BootstrapHook {

    private static final String TOKEN_ENV_VAR = "MINECORD_TOKEN";

    private static final EnumSet<GatewayIntent> GATEWAYS = EnumSet.of(
            GatewayIntent.DIRECT_MESSAGES, GatewayIntent.DIRECT_MESSAGE_REACTIONS,
            GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS);
    private static final EnumSet<CacheFlag> DISABLED_CACHE_FLAGS = EnumSet.of(
            CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS, CacheFlag.EMOTE,
            CacheFlag.ONLINE_STATUS, CacheFlag.VOICE_STATE);
    private static final EnumSet<Message.MentionType> ALLOWED_MENTIONS = EnumSet.complementOf(EnumSet.of(
            Message.MentionType.EVERYONE, Message.MentionType.HERE,
            Message.MentionType.USER, Message.MentionType.ROLE));

    private final Function<Bootstrap, Bot> botLoader;

    private Bot bot;

    // Saved for reload/shutdown
    private Path path;
    private BootContext context;
    private ShardManager shardManager;

    // Must queue and promote during reload
    private SwapEventManagerProvider swapEventManagers;

    public void start(String[] args) {
        log.debug("Program started");
        try {
            int exitCode = argsStage(args);
            if (exitCode != BootExitCodes.SUCCESS) {
                System.exit(exitCode);
            }
        } catch (Throwable ex) {
            log.error("FATAL: A fatal exception occurred on startup", ex);
            System.exit(BootExitCodes.GENERAL_FAILURE);
        }
    }

    private int argsStage(String[] args) {
        Instant startTime = Instant.now();
        log.debug("Parsing command line arguments");

        ArgsHandler handle = new ArgsHandler();
        int exitCode = new CommandLine(handle).execute(args);
        if (handle.requestedHelp() || exitCode != BootExitCodes.SUCCESS) {
            return exitCode;
        }
        path = handle.getPath();
        assert path != null;
        return configStage(startTime);
    }

    private int configStage(Instant startTime) {
        log.debug("Reading instance config");
        Path instancePath = path.resolve("instance.yml");
        if (!instancePath.toFile().exists()) {
            try {
                IO.copyResource("instance.yml", instancePath, Bootstrap.class);
            } catch (IOException ex) {
                log.error("FATAL: Failed to create instance.yml", ex);
                return BootExitCodes.INSTANCE_CONFIG_CREATION_IOE;
            }
            log.info("The config file was created!");
            log.info("--> Put your bot token in instance.yml to run the bot.");
            return BootExitCodes.SUCCESS;
        }

        Either<Integer, InstanceConfig> instanceConfigOrError = loadConfig(instancePath);
        if (instanceConfigOrError.isLeft()) {
            return instanceConfigOrError.getLeft();
        }
        InstanceConfig instanceConfig = instanceConfigOrError.getRight();

        // only logs after this line can be changed :(
        setLogLevel(instanceConfig);

        if (instanceConfig.isTokenDefault()) {
            log.info("--> Put your bot token in instance.yml to run the bot.");
            return BootExitCodes.SUCCESS;
        }
        String token = checkTokenOverride(instanceConfig.getToken());

        bot = botLoader.apply(this);
        int exitCode = bot.createConfigs(path);
        if (exitCode != BootExitCodes.SUCCESS) {
            return exitCode;
        }

        return preInitStage(startTime, instanceConfig, token);
    }

    private int preInitStage(Instant startTime, InstanceConfig instanceConfig, String token) {
        log.debug("Creating connection builder");
        HttpConfig httpConfig = instanceConfig.getHttpConfig();
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequestsPerHost(httpConfig.getMaxRequestsPerHost());
        ConnectionPool connectionPool = new ConnectionPool(httpConfig.getMaxIdleConnections(),
                httpConfig.getKeepAlive(), TimeUnit.MILLISECONDS);
        Builder httpClientBuilder = new Builder()
                .connectionPool(connectionPool)
                .dispatcher(dispatcher);
        OkHttpConnection connection = new OkHttpConnection(httpClientBuilder, dispatcher, connectionPool);

        context = new BootContext(startTime, instanceConfig.getShardCount(), token, connection);
        int exitCode = bot.preInit(context);
        if (exitCode != BootExitCodes.SUCCESS) {
            return exitCode;
        }

        return initStage(instanceConfig, token);
    }

    private int initStage(InstanceConfig instanceConfig, String token) {
        int shardCount = instanceConfig.getShardCount();
        ReadyListener readyListener = new ReadyListener(shardCount);
        swapEventManagers = new SwapEventManagerProvider(shardCount);

        try {
            log.info("Logging in...");
            shardManager = DefaultShardManagerBuilder.create(GATEWAYS)
                    .setToken(token)
                    .setAutoReconnect(true)
                    .setEventManagerProvider(swapEventManagers)
                    .addEventListeners(readyListener)
                    .setShardsTotal(shardCount)
                    .setStatus(OnlineStatus.IDLE)
                    .setActivity(getActivity(instanceConfig))
                    .setMemberCachePolicy(MemberCachePolicy.NONE)
                    .disableCache(DISABLED_CACHE_FLAGS)
                    .setHttpClientBuilder(context.getConnection().getHttpClientBuilder())
                    .build();
        } catch (CompletionException | LoginException ex) {
            log.error("FATAL: There was an error logging in, check if your token is correct.", ex);
            return getLoginFailureExitCode(ex);
        }
        try {
            readyListener.await();
        } catch (InterruptedException ex) {
            log.error("FATAL: Interrupted while waiting for shards to ready.", ex);
            return BootExitCodes.INTERRUPTED;
        }
        log.info("All shards ready!");
        shardManager.removeEventListener(readyListener);

        MessageAction.setDefaultMentions(ALLOWED_MENTIONS);

        int exitCode = bot.init(shardManager);
        if (exitCode != BootExitCodes.SUCCESS) {
            return exitCode;
        }

        postInitStage();
        return BootExitCodes.SUCCESS;
    }

    private void postInitStage() {
        bot.postInit();
    }

    private static Either<Integer, InstanceConfig> loadConfig(Path path) {
        try {
            return Either.right(ConfigReader.read(path, InstanceConfig.class));
        } catch (IOException ex) {
            log.error("FATAL: There was an error reading the config", ex);
            return Either.left(BootExitCodes.INSTANCE_CONFIG_IOE);
        } catch (InvalidConfigException ex) {
            log.error("FATAL: The config was invalid", ex);
            return Either.left(BootExitCodes.INSTANCE_CONFIG_INVALID);
        }
    }

    private static void setLogLevel(InstanceConfig instanceConfig) {
        Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        Level logLevel = instanceConfig.getLogLevel();
        log.info("Setting log level to " + logLevel);
        logger.setLevel(logLevel);
    }

    private static String checkTokenOverride(String fallback) {
        String env = System.getenv(TOKEN_ENV_VAR);
        if (env != null) {
            log.info("Found env var override for " + TOKEN_ENV_VAR);
            return env;
        }
        return fallback;
    }

    private static @Nullable Activity getActivity(InstanceConfig instanceConfig) {
        LoadingActivity loadingActivity = instanceConfig.getLoadingActivity();
        return loadingActivity == null ? null : loadingActivity.asActivity();
    }

    private static int getLoginFailureExitCode(Exception ex) {
        if (ex instanceof LoginException) {
            return BootExitCodes.LOGIN_FAILURE;
        }
        return BootExitCodes.LOGIN_IOE;
    }

    public int reload() {
        log.debug("Reload requested");
        Bot newBot = botLoader.apply(this);
        int exitCode = reload(newBot);
        if (exitCode == BootExitCodes.SUCCESS) {
            bot = newBot;
            log.debug("Reload complete");
        } else {
            try {
                newBot.onShutdown();
            } catch (Exception ex) {
                log.error("There was an exception shutting down the new bot on failed boot after reload, ignoring", ex);
            }
        }
        return exitCode;
    }
    private int reload(Bot newBot) {
        int configExitCode = newBot.createConfigs(path);
        if (configExitCode != BootExitCodes.SUCCESS) {
            log.error("Failed to reload during config stage with exit code {}", configExitCode);
            return configExitCode;
        }
        int preInitExitCode = newBot.preInit(context);
        if (preInitExitCode != BootExitCodes.SUCCESS) {
            log.error("Failed to reload during pre-init stage with exit code {}", preInitExitCode);
            return preInitExitCode;
        }

        log.debug("Queue staging event manager");
        swapEventManagers.queueStaging();
        // queueStaging() must be undone if there is an error to avoid ghost listeners leaking memory
        try {
            int initExitCode = newBot.init(shardManager);
            if (initExitCode != BootExitCodes.SUCCESS) {
                log.error("Failed to reload during init stage with exit code {}", initExitCode);
                log.debug("Unqueue staging event manager");
                swapEventManagers.unqueueStaging();
                return initExitCode;
            }
        } catch (Throwable ex) {
            log.error("Failed to reload during init stage due to exception");
            log.debug("Unqueue staging event manager");
            swapEventManagers.unqueueStaging();
            throw ex; // Let reloader handle it
        }
        log.debug("Promote staging event manager");
        swapEventManagers.promoteStaging();

        log.debug("Starting old bot shutdown");
        CompletableFuture<Void> future = CompletableFuture.runAsync(bot::onShutdown);
        newBot.postInit();
        log.debug("Waiting for onShutdown to complete");
        future.join();
        return BootExitCodes.SUCCESS;
    }

    public void shutdown() {
        log.debug("Shutdown requested");
        context.getConnection().close();
        shardManager.shutdown();
        log.debug("Shutdown queued");
    }

}
