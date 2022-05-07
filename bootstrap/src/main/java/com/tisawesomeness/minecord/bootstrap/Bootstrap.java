package com.tisawesomeness.minecord.bootstrap;

import com.tisawesomeness.minecord.Minecord;
import com.tisawesomeness.minecord.share.BootContext;
import com.tisawesomeness.minecord.share.Bot;
import com.tisawesomeness.minecord.share.HttpConfig;
import com.tisawesomeness.minecord.share.LoadingActivity;
import com.tisawesomeness.minecord.share.config.ConfigReader;
import com.tisawesomeness.minecord.share.config.InvalidConfigException;
import com.tisawesomeness.minecord.share.util.Either;
import com.tisawesomeness.minecord.share.util.IO;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.hooks.EventListener;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.EnumSet;
import java.util.Objects;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Starts the bot. See {@link Bot} for details on the boot process.
 */
@Slf4j
public final class Bootstrap {

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

    public static void main(String[] args) {
        log.debug("Program started");
        try {
            int exitCode = argsStage(args);
            if (exitCode != BootExitCodes.SUCCESS) {
                System.exit(exitCode);
            }
        } catch (Exception ex) {
            log.error("FATAL: A fatal exception occurred on startup", ex);
            System.exit(BootExitCodes.GENERAL_FAILURE);
        }
    }

    private static int argsStage(String[] args) {
        Instant startTime = Instant.now();
        log.debug("Parsing command line arguments");

        ArgsHandler handle = new ArgsHandler();
        int exitCode = new CommandLine(handle).execute(args);
        if (exitCode != BootExitCodes.SUCCESS) {
            return exitCode;
        }
        return configStage(Objects.requireNonNull(handle.getPath()), startTime);
    }

    private static int configStage(Path path, Instant startTime) {
        log.debug("Reading instance config");
        Path instancePath = path.resolve("instance.yml");
        if (!instancePath.toFile().exists()) {
            try {
                Files.copy(IO.openResource("instance.yml"), instancePath);
            } catch (IOException ex) {
                log.error("FATAL: Failed to create instance.yml", ex);
                return BootExitCodes.INSTANCE_CONFIG_CREATION_IOE;
            }
            log.info("The config file was created!");
            log.info("--> Put your bot token in instance.yml to run the bot.");
            return BootExitCodes.SUCCESS;
        }

        Either<Integer, InstanceConfig> instanceConfigOrError = loadConfig(instancePath);
        if (!instanceConfigOrError.isRight()) {
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

        Bot bot = new Minecord();
        int exitCode = bot.createConfigs(path);
        if (exitCode != BootExitCodes.SUCCESS) {
            return exitCode;
        }

        return preInitStage(startTime, bot, instanceConfig, token);
    }

    private static int preInitStage(Instant startTime, Bot bot, InstanceConfig instanceConfig, String token) {
        HttpConfig httpConfig = instanceConfig.getHttpConfig();
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequestsPerHost(httpConfig.getMaxRequestsPerHost());
        ConnectionPool connectionPool = new ConnectionPool(httpConfig.getMaxIdleConnections(),
                httpConfig.getKeepAlive(), TimeUnit.MILLISECONDS);
        Builder httpClientBuilder = new Builder()
                .connectionPool(connectionPool)
                .dispatcher(dispatcher);

        BootContext context = new BootContext(startTime, instanceConfig.getShardCount(), token,
                httpClientBuilder, dispatcher, connectionPool);
        int exitCode = bot.preInit(context);
        if (exitCode != BootExitCodes.SUCCESS) {
            return exitCode;
        }

        return initStage(bot, instanceConfig, token, httpClientBuilder);
    }

    private static int initStage(Bot bot, InstanceConfig instanceConfig, String token, Builder httpClientBuilder) {
        CountDownLatch readyLatch = new CountDownLatch(instanceConfig.getShardCount());
        EventListener readyListener = new ReadyListener(readyLatch);

        ShardManager shardManager;
        try {
            log.info("Logging in...");
            shardManager = DefaultShardManagerBuilder.create(GATEWAYS)
                    .setToken(token)
                    .setAutoReconnect(true)
                    .addEventListeners(readyListener)
                    .setShardsTotal(instanceConfig.getShardCount())
                    .setStatus(OnlineStatus.IDLE)
                    .setActivity(getActivity(instanceConfig))
                    .setMemberCachePolicy(MemberCachePolicy.NONE)
                    .disableCache(DISABLED_CACHE_FLAGS)
                    .setHttpClientBuilder(httpClientBuilder)
                    .build();

            readyLatch.await();
            log.info("All shards ready!");

        } catch (CompletionException | LoginException | InterruptedException ex) {
            log.error("FATAL: There was an error logging in, check if your token is correct.", ex);
            return getLoginFailureExitCode(ex);
        }

        MessageAction.setDefaultMentions(ALLOWED_MENTIONS);

        return bot.init(shardManager);
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

}
