package com.tisawesomeness.minecord;

import com.tisawesomeness.minecord.command.CommandRegistry;
import com.tisawesomeness.minecord.config.ConfigReader;
import com.tisawesomeness.minecord.config.InvalidConfigException;
import com.tisawesomeness.minecord.config.branding.Branding;
import com.tisawesomeness.minecord.config.branding.LoadingActivity;
import com.tisawesomeness.minecord.config.config.BotListConfig;
import com.tisawesomeness.minecord.config.config.Config;
import com.tisawesomeness.minecord.database.Database;
import com.tisawesomeness.minecord.database.DatabaseCache;
import com.tisawesomeness.minecord.database.VoteHandler;
import com.tisawesomeness.minecord.lang.Lang;
import com.tisawesomeness.minecord.listen.CommandListener;
import com.tisawesomeness.minecord.listen.GuildCountListener;
import com.tisawesomeness.minecord.listen.ReactListener;
import com.tisawesomeness.minecord.listen.ReadyListener;
import com.tisawesomeness.minecord.mc.MCLibrary;
import com.tisawesomeness.minecord.mc.StandardMCLibrary;
import com.tisawesomeness.minecord.network.APIClient;
import com.tisawesomeness.minecord.service.*;
import com.tisawesomeness.minecord.setting.SettingRegistry;
import com.tisawesomeness.minecord.util.DateUtils;
import com.tisawesomeness.minecord.util.Lists;
import com.tisawesomeness.minecord.util.concurrent.ACExecutorService;
import com.tisawesomeness.minecord.util.concurrent.ShutdownBehavior;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import lombok.Cleanup;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

/**
 * <p>The entry point and central point for Minecord.</p>
 * To start the bot, call {@link #setup(ArgsHandler args)}.
 */
@Slf4j
@NoArgsConstructor
public class Bot {

    // Only use what's necessary
    private static final List<GatewayIntent> gateways = Lists.of(
            GatewayIntent.DIRECT_MESSAGES, GatewayIntent.DIRECT_MESSAGE_REACTIONS,
            GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS);
    private static final EnumSet<CacheFlag> disabledCacheFlags = EnumSet.of(
            CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS, CacheFlag.EMOTE,
            CacheFlag.ONLINE_STATUS, CacheFlag.VOICE_STATE);
    private static final EnumSet<Message.MentionType> ALLOWED_MENTIONS = EnumSet.complementOf(EnumSet.of(
            Message.MentionType.EVERYONE, Message.MentionType.HERE,
            Message.MentionType.USER, Message.MentionType.ROLE));

    private PresenceService presenceService;
    private BotListService botListService;
    private Service menuService;
    private Service commandStatsService;

    private Config config;
    private CommandRegistry registry;
    private CommandListener commandListener;
    private EventListener guildCountListener;
    private Database database;
    private MCLibrary mcLibrary;
    @Getter private APIClient apiClient;
    @Getter private ArgsHandler args;
    @Getter private ShardManager shardManager;
    @Getter private SettingRegistry settings;
    private @Nullable AnnounceRegistry announceRegistry;
    @Getter private VoteHandler voteHandler;
    private @Nullable Branding brandingConfig;
    @Getter private BotBranding branding;
    @Getter private Secrets secrets;
    @Getter private long birth;
    @Getter private long bootTime;

    /**
     * Starts the bot. Will return early if one of the following happens:
     * <ul>
     *     <li>The config or announcement files couldn't load.</li>
     *     <li>An internal resource file fails to load.</li>
     *     <li>JDA had an error logging in, usually invalid token.</li>
     *     <li>The database fails to connect.</li>
     * </ul>
     * <b>This method is blocking!</b>
     * @param args The parsed command-line arguments
     * @return The error code to send on failure, or 0 on success
     */
    public int setup(ArgsHandler args) {
        birth = System.currentTimeMillis();
        log.info("Bot starting...");
        this.args = args;

        // Pre-init
        try {
            config = ConfigReader.read(args.getConfigPath(), Config.class);
        } catch (IOException ex) {
            log.error("FATAL: There was an error reading the config", ex);
            return ExitCodes.CONFIG_IOE;
        } catch (InvalidConfigException ex) {
            log.error("FATAL: The config was invalid", ex);
            return ExitCodes.INVALID_CONFIG;
        }

        // only logs after this line can be changed :(
        Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        Level logLevel = config.getLogLevel();
        log.info("Setting log level to " + logLevel);
        logger.setLevel(logLevel);

        if (!config.isSelfHosted()) {
            log.info("This bot is NOT self-hosted, support is unavailable");
        }
        if ("your token here".equals(config.getToken())) {
            log.info("--> Put your bot token in config.yml to run the bot.");
            return ExitCodes.SUCCESS;
        }

        secrets = new Secrets(config);

        Optional<Path> brandingPathOpt = args.getBrandingPath();
        LoadingActivity loadingActivity = null;
        if (brandingPathOpt.isPresent()) {
            log.debug("Branding file detected, reading...");
            try {
                brandingConfig = ConfigReader.read(brandingPathOpt.get(), Branding.class);
                loadingActivity = brandingConfig.getPresenceConfig().getLoadingActivity();
                branding = new BotBranding(config, brandingConfig);
                announceRegistry = new AnnounceRegistry(config, branding, brandingConfig.getAnnouncementConfig());
            } catch (IOException ex) {
                log.error("There was an error reading the branding config", ex);
            } catch (InvalidConfigException ex) {
                log.error("The branding config was invalid", ex);
            }
        }
        if (branding == null) {
            branding = new BotBranding();
            loadingActivity = LoadingActivity.getDefault();
        }
        Activity activity = loadingActivity == null ? null : loadingActivity.asActivity(branding, config);

        if (config.getFlagConfig().isLoadTranslationsFromFile()) {
            log.debug("Config enabled external translations, reading...");
            Lang.reloadFromFile(args.getPath().resolve("lang"));
        }

        apiClient = new APIClient(config.getAdvancedConfig().getHttpConfig());
        mcLibrary = new StandardMCLibrary(apiClient, config);

        CountDownLatch readyLatch = new CountDownLatch(config.getShardCount());
        EventListener readyListener = new ReadyListener(readyLatch);
        EventListener reactListener = new ReactListener();

        // Connect to database
        @Cleanup ACExecutorService exe = new ACExecutorService(
                Executors.newSingleThreadExecutor(), ShutdownBehavior.FORCE);
        Future<Database> futureDB = exe.submit(() -> new Database(config));

        try {
            // Initialize JDA
            log.info("Logging in...");
            shardManager = DefaultShardManagerBuilder.create(gateways)
                    .setToken(secrets.getToken())
                    .setAutoReconnect(true)
                    .addEventListeners(readyListener)
                    .setShardsTotal(config.getShardCount())
                    .setStatus(OnlineStatus.IDLE)
                    .setActivity(activity)
                    .setMemberCachePolicy(MemberCachePolicy.NONE)
                    .disableCache(disabledCacheFlags)
                    .setHttpClientBuilder(apiClient.getHttpClientBuilder())
                    .build();

            // Wait for shards to ready
            readyLatch.await();
            log.info("All shards ready!");

        } catch (CompletionException | LoginException | InterruptedException ex) {
            log.error("FATAL: There was an error logging in, check if your token is correct.", ex);
            return ExitCodes.LOGIN_ERROR;
        }

        // These depend on ShardManager
        presenceService = new PresenceService(shardManager, brandingConfig);
        botListService = new BotListService(shardManager, config.getBotListConfig(), secrets);
        if (brandingConfig != null) {
            guildCountListener = new GuildCountListener(this, config.getBotListConfig(),
                    brandingConfig.getPresenceConfig(), presenceService, botListService);
        }

        // Wait for database
        try {
            database = futureDB.get();
        } catch (ExecutionException ex) {
            log.error("FATAL: There was an error connecting to the database", ex);
            return ExitCodes.DATABASE_ERROR;
        } catch (InterruptedException ex) {
            throw new AssertionError("It should not be possible to interrupt the main thread" +
                    " before the bot can accept commands.");
        }
        for (long owner : config.getOwners()) {
            try {
                database.getCache().getUser(owner).withElevated(true).update();
            } catch (SQLException ex) {
                log.error("FATAL: Owner " + owner + " could not be elevated", ex);
                return ExitCodes.FAILED_TO_SET_OWNER;
            }
        }

        // These depend on database
        registry = new CommandRegistry();
        commandListener = new CommandListener(this, config, registry, database.getCommandStats());
        commandStatsService = new CommandStatsService(commandListener.getCommandExecutor(), config.getCommandConfig());
        settings = new SettingRegistry(config.getSettingsConfig());

        // Bot has started, start accepting messages
        MessageAction.setDefaultMentions(ALLOWED_MENTIONS);
        shardManager.addEventListener(commandListener, reactListener);
        if (guildCountListener != null) {
            shardManager.addEventListener(guildCountListener);
        }
        log.info("Bot ready!");

        // Start web server
        Future<VoteHandler> futureVH = null;
        if (config.getBotListConfig().isReceiveVotes()) {
            log.debug("Config enabled vote handler, starting...");
            futureVH = exe.submit(() -> new VoteHandler(this, secrets));
        }

        // Post-init
        bootTime = System.currentTimeMillis() - birth;
        log.info("Boot Time: " + DateUtils.getBootTime(bootTime));
        log(":white_check_mark: **Bot started!**");
        shardManager.setStatus(OnlineStatus.ONLINE);
        presenceService.start();
        botListService.start();
        menuService = new MenuService();
        menuService.start();
        commandStatsService.start();

        // Make sure vote handler finishes
        if (futureVH != null) {
            try {
                voteHandler = futureVH.get();
            } catch (ExecutionException ex) {
                log.error("FATAL: There was an error starting the vote handler", ex);
                return ExitCodes.VOTE_HANDLER_ERROR;
            } catch (InterruptedException ignore) {
                // It's possible to be interrupted if the shutdown command executes
                // after the bot starts but before the vote handler starts
                log.warn("Vote handler startup was interrupted. Did you use the shutdown command?");
                return ExitCodes.SUCCESS;
            }
        }

        log.info("Post-init finished.");
        return ExitCodes.SUCCESS;

    }

    /**
     * Reloads the bot. The parts that get reloaded are:
     * <ul>
     *     <li>The config and announce files.</li>
     *     <li>The database.</li>
     *     <li>The vote server.</li>
     *     <li>The internal item and recipe resource files (open the JAR as archive and replace them to reload these).</li>
     * </ul>
     * The config is loaded before everything else, so options like login details can change.
     * @throws IOException If a file wasn't found, or there was an error starting the vote server.
     * @throws ExecutionException If the database couldn't open, the initial read failed, or creating a missing table failed.
     * If there is an exception, shut down the bot with &shutdown or do a hard reset.
     */
    public void reload() throws IOException, ExecutionException, InterruptedException {
        log.info("Reloading...");

        // Closing everything down
        if (config.getBotListConfig() != null && config.getBotListConfig().isReceiveVotes()) {
            voteHandler.close();
        }
        presenceService.shutdown();
        menuService.shutdown();
        commandStatsService.shutdown();
        shardManager.removeEventListener(commandListener, guildCountListener);

        config = ConfigReader.read(args.getConfigPath(), Config.class);

        Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        Level logLevel = config.getLogLevel();
        log.info("Setting log level to " + logLevel);
        logger.setLevel(logLevel);

        // Database and vote handler need separate threads
        ExecutorService exe = Executors.newSingleThreadExecutor();
        Future<Database> futureDB = exe.submit(() -> new Database(config));
        Future<VoteHandler> futureVH = null;
        BotListConfig blc = config.getBotListConfig();
        secrets = new Secrets(config);
        if (blc != null && blc.isReceiveVotes()) {
            futureVH = exe.submit(() -> new VoteHandler(this, secrets));
        }

        // These can be started before the database
        if (!config.isSelfHosted()) {
            log.info("This bot is NOT self-hosted, support is unavailable");
        }
        Optional<Path> brandingPathOpt = args.getBrandingPath();
        if (brandingPathOpt.isPresent()) {
            log.debug("Branding file detected, reading...");
            brandingConfig = ConfigReader.read(brandingPathOpt.get(), Branding.class);
            branding = new BotBranding(config, brandingConfig);
            announceRegistry = new AnnounceRegistry(config, branding, brandingConfig.getAnnouncementConfig());
        }
        if (branding == null) {
            branding = new BotBranding();
            announceRegistry = null;
        }
        if (config.getFlagConfig().isLoadTranslationsFromFile()) {
            log.debug("Config enabled external translations, reading...");
            Lang.reloadFromFile(args.getPath().resolve("lang"));
        } else {
            log.debug("Config disabled external translations, reloading from resources...");
            Lang.reloadFromResources();
        }
        settings = new SettingRegistry(config.getSettingsConfig());
        presenceService = new PresenceService(shardManager, brandingConfig);
        presenceService.start();
        botListService = new BotListService(shardManager, blc, secrets);
        botListService.start();
        if (brandingConfig != null) {
            guildCountListener = new GuildCountListener(this ,config.getBotListConfig(),
                    brandingConfig.getPresenceConfig(), presenceService, botListService);
        }
        menuService = new MenuService();
        menuService.start();

        // Start everything up again
        database = futureDB.get();
        registry = new CommandRegistry();
        commandListener = new CommandListener(this, config, registry, database.getCommandStats());
        commandStatsService = new CommandStatsService(commandListener.getCommandExecutor(), config.getCommandConfig());
        commandStatsService.start();
        shardManager.addEventListener(commandListener);
        if (guildCountListener != null) {
            shardManager.addEventListener(guildCountListener);
        }
        if (futureVH != null) {
            voteHandler = futureVH.get();
        }
        exe.shutdown();

    }

    /**
     * Gracefully shuts down the bot.
     * <br>Use this method instead of {@link System#exit(int)} except for emergencies.
     */
    public void shutdown() {
        log.info("Shutting down...");
        presenceService.shutdown();
        menuService.shutdown();
        botListService.shutdown();
        commandStatsService.shutdown();
        shardManager.shutdown();
        if (config.getBotListConfig().isReceiveVotes()) {
            voteHandler.close();
        }
        apiClient.close();
        for (JDA jda : shardManager.getShards()) {
            jda.shutdown();
        }
        log.debug("Shutdown queued");
    }

    public Optional<AnnounceRegistry> getAnnounceRegistry() {
        return Optional.ofNullable(announceRegistry);
    }
    public Optional<Branding> getBrandingConfig() {
        return Optional.ofNullable(brandingConfig);
    }

    /**
     * The access point for stored guilds, channels, and users.
     * @return The database cache
     */
    public DatabaseCache getDatabaseCache() {
        return database.getCache();
    }

    /**
     * @return The Minecraft library object
     */
    public MCLibrary getMCLibrary() {
        return mcLibrary;
    }

    /**
     * Logs a message to the logging channel.
     */
    public void log(CharSequence m) {
        log.info(m.toString());
        long logChannel = config.getLogChannelId();
        if (logChannel == 0) {
            return;
        }
        TextChannel c = shardManager.getTextChannelById(logChannel);
        if (c == null) {
            return;
        }
        c.sendMessage(m).queue();
    }
    /**
     * Logs a message to the logging channel.
     */
    public void log(Message m) {
        User author = m.getAuthor();
        String logMessage = String.format("Logged message %d sent by %#s, ID %d: %#s",
                m.getIdLong(), author, author.getIdLong(), m);
        log.info(logMessage);
        long logChannel = config.getLogChannelId();
        if (logChannel == 0) {
            return;
        }
        TextChannel c = shardManager.getTextChannelById(logChannel);
        if (c == null) {
            return;
        }
        c.sendMessage(m).queue();
    }
    /**
     * Logs a message to the logging channel.
     */
    public void log(MessageEmbed m) {
        log.info(m.toData().toString());
        long logChannel = config.getLogChannelId();
        if (logChannel == 0) {
            return;
        }
        TextChannel c = shardManager.getTextChannelById(logChannel);
        if (c == null) {
            return;
        }
        EmbedBuilder eb = new EmbedBuilder(m).setTimestamp(OffsetDateTime.now());
        c.sendMessage(eb.build()).queue();
    }

}
