package com.tisawesomeness.minecord;

import com.tisawesomeness.minecord.command.CommandRegistry;
import com.tisawesomeness.minecord.common.BootContext;
import com.tisawesomeness.minecord.common.Bot;
import com.tisawesomeness.minecord.common.config.ConfigReader;
import com.tisawesomeness.minecord.common.config.InvalidConfigException;
import com.tisawesomeness.minecord.common.util.Either;
import com.tisawesomeness.minecord.common.util.IO;
import com.tisawesomeness.minecord.config.branding.Branding;
import com.tisawesomeness.minecord.config.config.Config;
import com.tisawesomeness.minecord.database.Database;
import com.tisawesomeness.minecord.database.DatabaseCache;
import com.tisawesomeness.minecord.database.VoteHandler;
import com.tisawesomeness.minecord.lang.Lang;
import com.tisawesomeness.minecord.listen.CommandListener;
import com.tisawesomeness.minecord.listen.GuildCountListener;
import com.tisawesomeness.minecord.listen.ReactListener;
import com.tisawesomeness.minecord.mc.MCLibrary;
import com.tisawesomeness.minecord.mc.StandardMCLibrary;
import com.tisawesomeness.minecord.network.APIClient;
import com.tisawesomeness.minecord.network.OkAPIClient;
import com.tisawesomeness.minecord.service.*;
import com.tisawesomeness.minecord.setting.SettingRegistry;
import com.tisawesomeness.minecord.util.DateUtils;
import com.tisawesomeness.minecord.util.concurrent.ACExecutorService;
import com.tisawesomeness.minecord.util.concurrent.ShutdownBehavior;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.sharding.ShardManager;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * The entry point and central point for the Minecord bot.
 */
@Slf4j
@NoArgsConstructor
public class Minecord implements Bot {

    private PresenceService presenceService;
    private BotListService botListService;
    private Service menuService;
    private Service commandStatsService;

    private Config config;
    private EventListener guildCountListener;
    private Database database;
    private VoteHandler voteHandler;
    private MCLibrary mcLibrary;
    @Getter private APIClient apiClient;
    @Getter private ShardManager shardManager;
    @Getter private SettingRegistry settings;
    private @Nullable AnnounceRegistry announceRegistry;
    private @Nullable Branding brandingConfig;
    @Getter private BotBranding branding;
    @Getter private Secrets secrets;
    @Getter private long birth;
    @Getter private long bootTime;

    private ACExecutorService exe;
    private Future<Database> futureDB;

    /**
     * {@inheritDoc}
     * Exits unsuccessfully if there is an error creating or reading the config.
     */
    public int createConfigs(@NonNull Path path) {
        Either<Integer, Config> configOrError = initConfig(path);
        if (configOrError.isLeft()) {
            return configOrError.getLeft();
        }
        config = configOrError.getRight();

        if (!config.isSelfHosted()) {
            log.info("This bot is NOT self-hosted, support is unavailable");
        }

        Optional<Branding> brandingOpt = initBranding(path);
        if (brandingOpt.isPresent()) {
            brandingConfig = brandingOpt.get();
        }

        if (config.getFlagConfig().isLoadTranslationsFromFile()) {
            log.debug("Config enabled external translations, reading...");
            Lang.reloadFromFile(path.resolve("lang"));
        }

        return ExitCodes.SUCCESS;
    }

    /**
     * {@inheritDoc}
     */
    public int preInit(@NonNull BootContext context) {
        log.info("Bot starting...");
        birth = context.getStartTime().toEpochMilli();

        if (brandingConfig == null) {
            branding = new BotBranding();
        } else {
            branding = new BotBranding(config, brandingConfig);
            announceRegistry = new AnnounceRegistry(config, branding, brandingConfig.getAnnouncementConfig(), context.getShardCount());
        }

        secrets = new Secrets(config, context.getToken());

        apiClient = new OkAPIClient(context.getHttpClientBuilder(), context.getDispatcher(), context.getConnectionPool());
        mcLibrary = new StandardMCLibrary(apiClient, config);

        // Start db connection early
        exe = new ACExecutorService(Executors.newSingleThreadExecutor(), ShutdownBehavior.FORCE);
        futureDB = exe.submit(() -> new Database(config));

        return ExitCodes.SUCCESS;
    }

    /**
     * {@inheritDoc}
     * Exits unsuccessfully if the database failed to start, or the bot failed to promote the owner.
     */
    public int init(@NonNull ShardManager shardManager) {
        log.debug("Bot init stage reached");
        this.shardManager = shardManager;
        EventListener reactListener = new ReactListener();

        presenceService = new PresenceService(shardManager, brandingConfig);
        botListService = new BotListService(shardManager, config.getBotListConfig(), secrets);
        if (brandingConfig != null) {
            guildCountListener = new GuildCountListener(this, config.getBotListConfig(),
                    brandingConfig.getPresenceConfig(), presenceService, botListService);
        }

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
        CommandRegistry registry = new CommandRegistry();
        CommandListener commandListener = new CommandListener(this, config, registry, database.getCommandStats());
        commandStatsService = new CommandStatsService(commandListener.getCommandExecutor(), config.getCommandConfig());
        settings = new SettingRegistry(config.getSettingsConfig());

        // Bot has started, start accepting messages
        shardManager.addEventListener(commandListener, reactListener);
        if (guildCountListener != null) {
            shardManager.addEventListener(guildCountListener);
        }

        bootTime = System.currentTimeMillis() - birth;
        log.info("Bot ready!");

        return postInit();
    }

    /**
     * {@inheritDoc}
     * Exits unsuccessfully if the vote handler failed to start.
     */
    private int postInit() {
        // Start web server
        Future<VoteHandler> futureVH = null;
        if (config.getBotListConfig().isReceiveVotes()) {
            log.debug("Config enabled vote handler, starting...");
            futureVH = exe.submit(() -> new VoteHandler(this, secrets));
        }

        // Post-init
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
        throw new UnsupportedOperationException("Not yet implemented"); // TODO reimplement

        /*
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
        */
    }

    /**
     * Gracefully shuts down the bot.
     * <br>Use this method instead of {@link System#exit(int)} except for emergencies.
     */
    public void shutdown() {
        log.info("Shutting down...");
        System.exit(0); // TODO reimplement
        /*
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
        */
    }

    private static Either<Integer, Config> initConfig(Path path) {
        try {
            Path configPath = getOrCreateFile(path, "config.yml");
            return readConfig(configPath);
        } catch (IOException ex) {
            log.error("FATAL: There was an error creating the config", ex);
            return Either.left(ExitCodes.COULD_NOT_CREATE_CONFIG);
        }
    }
    private static Either<Integer, Config> readConfig(Path configPath) {
        log.debug("Config file detected, reading...");
        try {
            return Either.right(ConfigReader.read(configPath, Config.class));
        } catch (IOException ex) {
            log.error("FATAL: There was an error reading the config", ex);
            return Either.left(ExitCodes.CONFIG_IOE);
        } catch (InvalidConfigException ex) {
            log.error("FATAL: The config was invalid", ex);
            return Either.left(ExitCodes.INVALID_CONFIG);
        }
    }

    private static Optional<Branding> initBranding(Path path) {
        try {
            Path brandingPath = getOrCreateFile(path, "branding.yml");
            return readBranding(brandingPath);
        } catch (IOException ex) {
            log.warn("Could not load branding file, continuing anyway...", ex);
            return Optional.empty();
        }
    }
    private static Optional<Branding> readBranding(Path brandingPath) {
        log.debug("Branding file detected, reading...");
        try {
            return Optional.of(ConfigReader.read(brandingPath, Branding.class));
        } catch (IOException ex) {
            log.error("There was an error reading the branding config, continuing anyway...", ex);
        } catch (InvalidConfigException ex) {
            log.error("The branding config was invalid, continuing anyway...", ex);
        }
        return Optional.empty();
    }

    private static Path getOrCreateFile(Path path, String fileName) throws IOException {
        Path filePath = path.resolve(fileName);
        if (!filePath.toFile().exists()) {
            log.debug("{} does not exist, creating...", fileName);
            IO.write(filePath, IO.loadResource(fileName));
        }
        return filePath;
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
