package com.tisawesomeness.minecord;

import com.tisawesomeness.minecord.command.CommandListener;
import com.tisawesomeness.minecord.command.Registry;
import com.tisawesomeness.minecord.database.Database;
import com.tisawesomeness.minecord.database.VoteHandler;
import com.tisawesomeness.minecord.mc.MCLibrary;
import com.tisawesomeness.minecord.mc.StandardMCLibrary;
import com.tisawesomeness.minecord.mc.item.ItemRegistry;
import com.tisawesomeness.minecord.mc.recipe.RecipeRegistry;
import com.tisawesomeness.minecord.network.APIClient;
import com.tisawesomeness.minecord.network.OkAPIClient;
import com.tisawesomeness.minecord.util.*;
import com.tisawesomeness.minecord.util.type.DelayedCountDownLatch;
import com.tisawesomeness.minecord.util.type.Switch;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.messages.MessageRequest;
import org.discordbots.api.client.DiscordBotListAPI;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Bot {

    private static final String mainClass = "com.tisawesomeness.minecord.Main";
    public static final String author = "Tis_awesomeness";
    public static final String authorTag = "@tis_awesomeness";
    public static final String invite = "https://minecord.github.io/invite";
    public static final String helpServer = "https://minecord.github.io/support";
    public static final String website = "https://minecord.github.io";
    public static final String github = "https://github.com/Tisawesomeness/Minecord";
    public static final String donate = "https://ko-fi.com/tis_awesomeness";
    public static final String terms = "https://minecord.github.io/terms";
    public static final String privacy = "https://minecord.github.io/privacy";
    private static final String version = "0.17.17";
    public static final String jdaVersion = "5.6.1";
    public static final Color color = Color.GREEN;

    public static ShardManager shardManager;
    public static APIClient apiClient;
    public static DiscordLogger logger;
    public static MCLibrary mcLibrary;
    private static StatusListener statusListener;
    private static GuildListener guildListener;
    private static CommandListener commandListener;
    private static ReactListener reactListener;
    public static String ownerAvatarUrl;
    public static long birth;
    public static long bootTime;
    public static String[] args;

    public static Thread thread;
    private static final List<GatewayIntent> gateways = Arrays.asList(
            GatewayIntent.DIRECT_MESSAGES, GatewayIntent.DIRECT_MESSAGE_REACTIONS,
            GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS
    );

    private static final Switch readySwitch = new Switch();
    public static void setReady() {
        readySwitch.enable();
    }
    public static void setNotReady() {
        readySwitch.disable();
    }
    public static boolean waitForReady(long l, TimeUnit timeUnit) throws InterruptedException {
        return readySwitch.waitForEnable(l, timeUnit);
    }

    private static final DelayedCountDownLatch shardReadySwitch = new DelayedCountDownLatch();
    private static void initShardLatch(int shardCount) {
        shardReadySwitch.startCountDown(shardCount);
    }
    public static void readyShard() {
        shardReadySwitch.countDown();
    }
    private static void waitForShards() throws InterruptedException {
        shardReadySwitch.await();
    }

    public static boolean setup(String[] args, boolean devMode) {
        long startTime = System.currentTimeMillis();
        if (!devMode) {
            System.out.println("Bot starting...");
        }

        //Parse arguments
        Bot.args = args;
        Config.read(false);
        if (Config.getDevMode() && !devMode) return false;
        boolean reload = args.length > 0 && ArrayUtils.contains(args, "-r");

        if (!reload && Config.getClientToken().equals("your token here")) {
            System.err.println("Enter your Discord bot token in config.json, then start the bot again.");
            System.exit(0);
        }

        //Pre-init
        thread = Thread.currentThread();
        statusListener = new StatusListener();
        guildListener = new GuildListener();
        commandListener = new CommandListener();
        reactListener = new ReactListener();
        apiClient = new OkAPIClient();
        logger = new DiscordLogger(apiClient.getHttpClientBuilder().build());
        mcLibrary = new StandardMCLibrary(apiClient);
        try {
            Announcement.init(Config.getPath());
            ColorUtils.init(Config.getPath());
            ItemRegistry.init(Config.getPath());
            RecipeRegistry.init(Config.getPath());
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        ReactMenu.startPurgeThread();
        Registry.init();

        //Connect to database
        Thread db = new Thread(() -> {
            try {
                Database.init();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
        db.start();

        //Start web server
        Thread ws = null;
        if (Config.getReceiveVotes()) {
            ws = new Thread(() -> {
                try {
                    VoteHandler.init();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
            ws.start();
        }

        //Fetch main class
        try {
            if (Config.getDevMode()) {
                @SuppressWarnings("static-access")
                Class<?> clazz = Thread.currentThread().getContextClassLoader().getSystemClassLoader()
                        .loadClass(mainClass);
                MethodName.clazz = clazz;
            }

            //If this is a reload
            if (reload && Config.getDevMode()) {

                //Get main class info
                Message m = (Message) MethodName.GET_MESSAGE.method().invoke(null, "ignore");
                User u = (User) MethodName.GET_USER.method().invoke(null, "ignore");
                shardManager = (ShardManager) MethodName.GET_SHARDS.method().invoke(null, "ignore");
                birth = (long) MethodName.GET_BIRTH.method().invoke(null, "ignore");
                //Prepare commands
                for (JDA jda : shardManager.getShards()) {
                    jda.addEventListener(statusListener, guildListener, commandListener, reactListener);
                }
                m.editMessage(":white_check_mark: **Bot reloaded!**").queue();
                logger.log(":arrows_counterclockwise: **Bot reloaded by " + DiscordUtils.tagAndId(u) + "**");
                System.out.println("Bot reloaded by " + DiscordUtils.tagAndId(u));

                //If this is the first run
            } else {

                //Initialize JDA
                shardManager = DefaultShardManagerBuilder.createLight(Config.getClientToken(), gateways)
                        .setAutoReconnect(true)
                        .addEventListeners(statusListener, guildListener, commandListener, reactListener)
                        .setShardsTotal(Config.getShardCount())
                        .setActivity(Activity.playing("Loading..."))
                        .setHttpClientBuilder(apiClient.getHttpClientBuilder())
                        .build();

                //Update main class
                birth = startTime;
                if (Config.getDevMode()) {
                    MethodName.SET_SHARDS.method().invoke(null, shardManager);
                    MethodName.SET_BIRTH.method().invoke(null, birth);
                }

                // Wait for shards to ready
                int shardCount = shardManager.getShardsTotal();
                initShardLatch(shardCount);
                if (Config.getShardCount() == -1) {
                    System.out.println("Shard count: " + shardCount);
                }
                waitForShards();
                System.out.println("Shards ready");

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

        Message.suppressContentIntentWarning(); // Still process legacy commands when possible to ease transition
        MessageRequest.setDefaultMentions(EnumSet.noneOf(Message.MentionType.class));

        // The big one
        List<CommandData> slashCommands = Registry.getSlashCommands();
        for (String testServerId : Config.getTestServers()) {
            shardManager.getGuildById(testServerId).updateCommands()
                    .addCommands(slashCommands)
                    .queue();
        }

        //Start discordbots.org API
        String id = shardManager.getShardById(0).getSelfUser().getId();
        if (Config.getSendServerCount() || Config.getReceiveVotes()) {
            RequestUtils.api = new DiscordBotListAPI.Builder().token(Config.getOrgToken()).botId(id).build();
        }

        //Wait for database and web server
        try {
            db.join();
        } catch (InterruptedException ignored) {}
        setReady();
        System.out.println("Bot ready!");
        try {
            if (ws != null) ws.join();
            System.out.println("Web server started");
        } catch (InterruptedException ignored) {}

        //Post-init
        if (!Config.getOwner().equals("0")) {
            shardManager.retrieveUserById(Config.getOwner()).queue(u -> ownerAvatarUrl = u.getAvatarUrl());
        }
        bootTime = System.currentTimeMillis() - birth;
        System.out.println("Boot Time: " + DateUtils.getBootTime());
        logger.log(":white_check_mark: **Bot started!**");
        DiscordUtils.update();
        RequestUtils.sendGuilds();

        return true;

    }

    public static void reloadMCLibrary() {
        mcLibrary = new StandardMCLibrary(apiClient);
    }

    public static void shutdown(Message m, User u) {

        //Disable JDA
        for (JDA jda : shardManager.getShards()) {
            jda.setAutoReconnect(false);
            jda.removeEventListener(statusListener, guildListener, commandListener, reactListener);
        }
        try {
            //Reload this class using reflection
            String[] args = ArrayUtils.addAll(new String[]{"-r"}, Bot.args);
            MethodName.SET_MESSAGE.method().invoke(null, m);
            MethodName.SET_USER.method().invoke(null, u);
            MethodName.LOAD.method().invoke(null, (Object) args);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        //Stop the thread
        thread.interrupt();

    }

    public static double getPing() {
        // yes, getAverageGatewayPing() really can return negative
        return Math.max(0, Bot.shardManager.getAverageGatewayPing());
    }

    public static String getVersion() {
        return version;
    }

    //Helps with reflection
    private enum MethodName {
        MAIN("main"),
        LOAD("load"),
        GET_MESSAGE("getMessage"),
        SET_MESSAGE("setMessage"),
        GET_USER("getUser"),
        SET_USER("setUser"),
        GET_SHARDS("getShards"),
        SET_SHARDS("setShards"),
        GET_BIRTH("getBirth"),
        SET_BIRTH("setBirth");

        private final String name;
        MethodName(String name) {
            this.name = name;
        }

        public static Class<?> clazz;
        public Method method() {
            for (Method m : clazz.getDeclaredMethods()) {
                if (m.getName().equals(name)) return m;
            }
            return null;
        }
    }

}
