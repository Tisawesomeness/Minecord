package com.tisawesomeness.minecord;

import com.tisawesomeness.minecord.command.CommandRegistry;
import com.tisawesomeness.minecord.config.AnnounceRegistry;
import com.tisawesomeness.minecord.config.ConfigReader;
import com.tisawesomeness.minecord.config.serial.BotListConfig;
import com.tisawesomeness.minecord.config.serial.Config;
import com.tisawesomeness.minecord.database.Database;
import com.tisawesomeness.minecord.database.DatabaseCache;
import com.tisawesomeness.minecord.database.VoteHandler;
import com.tisawesomeness.minecord.listen.CommandListener;
import com.tisawesomeness.minecord.listen.GuildCountListener;
import com.tisawesomeness.minecord.listen.ReactListener;
import com.tisawesomeness.minecord.listen.ReadyListener;
import com.tisawesomeness.minecord.service.BotListService;
import com.tisawesomeness.minecord.service.CommandStatsService;
import com.tisawesomeness.minecord.service.MenuService;
import com.tisawesomeness.minecord.service.PresenceService;
import com.tisawesomeness.minecord.service.Service;
import com.tisawesomeness.minecord.setting.SettingRegistry;
import com.tisawesomeness.minecord.util.DateUtils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import okhttp3.OkHttpClient;

import javax.security.auth.login.LoginException;
import java.awt.Color;
import java.io.IOException;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * <p>The entry point and central point for Minecord.</p>
 * To start the bot, call {@link #setup(ArgsHandler args)}.
 */
@NoArgsConstructor
public class Bot {

    // Bot constants, all others defined in config
    public static final String author = "Tis_awesomeness";
    public static final String authorTag = "@Tis_awesomeness#8617";
    public static final String helpServer = "https://minecord.github.io/support";
    public static final String website = "https://minecord.github.io";
    public static final String github = "https://github.com/Tisawesomeness/Minecord";
    public static final String jdaVersion = "4.2.0_168";
    public static final Color color = Color.GREEN;

    // Only use what's necessary
    private static final List<GatewayIntent> gateways = Arrays.asList(
            GatewayIntent.DIRECT_MESSAGES, GatewayIntent.DIRECT_MESSAGE_REACTIONS,
            GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS);
    private static final EnumSet<CacheFlag> disabledCacheFlags = EnumSet.of(
            CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS, CacheFlag.EMOTE, CacheFlag.VOICE_STATE);

    private PresenceService presenceService;
    private BotListService botListService;
    private Service menuService;
    private Service commandStatsService;

    private Config config;
    private CommandRegistry registry;
    private CommandListener commandListener;
    private EventListener guildCountListener;
    private Database database;
    @Getter private ArgsHandler args;
    @Getter private ShardManager shardManager;
    @Getter private SettingRegistry settings;
    @Getter private AnnounceRegistry announceRegistry;
    @Getter private VoteHandler voteHandler;
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
        System.out.println("Bot starting...");
        this.args = args;

        // Pre-init
        config = ConfigReader.read(args.getConfigPath());
        try {
            if (config.getFlagConfig().isUseAnnouncements()) {
                announceRegistry = new AnnounceRegistry(args.getAnnouncePath(), config);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return 10;
        }

        String tokenOverride = args.getTokenOverride();
        String token = tokenOverride == null ? config.getToken() : tokenOverride;

        CountDownLatch readyLatch = new CountDownLatch(config.getShardCount());
        EventListener readyListener = new ReadyListener(readyLatch);
        EventListener reactListener = new ReactListener();

        // Connect to database
        ExecutorService exe = Executors.newSingleThreadExecutor();
        Future<Database> futureDB = exe.submit(() -> new Database(config));

        try {
            // Initialize JDA
            shardManager = DefaultShardManagerBuilder.create(gateways)
                    .setToken(token)
                    .setAutoReconnect(true)
                    .addEventListeners(readyListener)
                    .setShardsTotal(config.getShardCount())
                    .setActivity(Activity.playing("Loading..."))
                    .setMemberCachePolicy(MemberCachePolicy.NONE)
                    .disableCache(disabledCacheFlags)
                    .build();

            // Wait for shards to ready
            readyLatch.await();
            System.out.println("All shards ready!");

        } catch (LoginException | InterruptedException ex) {
            ex.printStackTrace();
            exe.shutdownNow();
            return 11;
        }

        // These depend on ShardManager
        presenceService = new PresenceService(shardManager, config.getPresenceConfig());
        botListService = new BotListService(shardManager, config.getBotListConfig());
        guildCountListener = new GuildCountListener(this, config, presenceService, botListService);

        // Wait for database
        try {
            database = futureDB.get();
        } catch (ExecutionException ex) {
            ex.printStackTrace();
            return 12;
        } catch (InterruptedException ex) {
            throw new AssertionError(
                    "It should not be possible to interrupt the main thread before the bot can accept commands.");
        }
        try {
            for (long owner : config.getOwners()) {
                database.getCache().getUser(owner).withElevated(true).update();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return 13;
        }

        // These depend on database
        registry = new CommandRegistry(config.getCommandConfig());
        commandListener = new CommandListener(this, config, registry, database.getCommandStats());
        commandStatsService = new CommandStatsService(commandListener.getCommandExecutor(), config.getCommandConfig());
        settings = new SettingRegistry(config.getSettingsConfig());

        // Bot has started, start accepting messages
        shardManager.addEventListener(commandListener, reactListener, guildCountListener);
        System.out.println("Bot ready!");

        // Start web server
        Future<VoteHandler> futureVH = null;
        if (config.getBotListConfig().isReceiveVotes()) {
            futureVH = exe.submit(() -> new VoteHandler(this, config.getBotListConfig()));
        }

        // Post-init
        bootTime = System.currentTimeMillis() - birth;
        System.out.println("Boot Time: " + DateUtils.getBootTime(bootTime));
        log(":white_check_mark: **Bot started!**");
        presenceService.start();
        botListService.start();
        menuService = new MenuService();
        menuService.start();
        commandStatsService.start();

        // Make sure vote handler finishes
        if (futureVH != null) {
            try {
                voteHandler = futureVH.get();
            } catch (ExecutionException | InterruptedException ex) {
                // It's possible to be interrupted if the shutdown command
                // after the bot starts but before the vote handler starts
                ex.printStackTrace();
                return 14;
            }
        }

        exe.shutdown();
        System.out.println("Post-init finished.");
        return 0;

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
        System.out.println("Reloading...");

        // Closing everything down
        if (config.getBotListConfig().isReceiveVotes()) {
            voteHandler.close();
        }
        presenceService.shutdown();
        menuService.shutdown();
        commandStatsService.shutdown();
        shardManager.removeEventListener(commandListener, guildCountListener);
        config = ConfigReader.read(args.getConfigPath());

        // Database and vote handler need separate threads
        ExecutorService exe = Executors.newSingleThreadExecutor();
        Future<Database> futureDB = exe.submit(() -> new Database(config));
        Future<VoteHandler> futureVH = null;
        BotListConfig blc = config.getBotListConfig();
        if (blc.isReceiveVotes()) {
            futureVH = exe.submit(() -> new VoteHandler(this, blc));
        }

        // These can be started before the database
        if (config.getFlagConfig().isUseAnnouncements()) {
            announceRegistry = new AnnounceRegistry(args.getAnnouncePath(), config);
        }
        settings = new SettingRegistry(config.getSettingsConfig());
        presenceService = new PresenceService(shardManager, config.getPresenceConfig());
        presenceService.start();
        botListService = new BotListService(shardManager, blc);
        botListService.start();
        guildCountListener = new GuildCountListener(this ,config, presenceService, botListService);
        menuService = new MenuService();
        menuService.start();

        // Start everything up again
        database = futureDB.get();
        registry = new CommandRegistry(config.getCommandConfig());
        commandListener = new CommandListener(this, config, registry, database.getCommandStats());
        commandStatsService = new CommandStatsService(commandListener.getCommandExecutor(), config.getCommandConfig());
        commandStatsService.start();
        shardManager.addEventListener(commandListener, guildCountListener);
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
        System.out.println("Shutting down...");
        presenceService.shutdown();
        menuService.shutdown();
        botListService.shutdown();
        commandStatsService.shutdown();
        shardManager.shutdown();
        if (config.getBotListConfig().isReceiveVotes()) {
            voteHandler.close();
        }
        for (JDA jda : shardManager.getShards()) {
            OkHttpClient client = jda.getHttpClient();
            client.connectionPool().evictAll();
            client.dispatcher().executorService().shutdown();
            jda.shutdown();
        }
    }

    /**
     * The access point for stored guilds, channels, and users.
     * @return The database cache
     */
    public DatabaseCache getDatabaseCache() {
        return database.getCache();
    }

    /**
     * Logs a message to the logging channel.
     */
    public void log(CharSequence m) {
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
