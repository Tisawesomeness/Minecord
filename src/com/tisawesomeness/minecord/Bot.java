package com.tisawesomeness.minecord;

import com.tisawesomeness.minecord.command.CommandListener;
import com.tisawesomeness.minecord.command.Registry;
import com.tisawesomeness.minecord.database.Database;
import com.tisawesomeness.minecord.database.VoteHandler;
import com.tisawesomeness.minecord.mc.MCLibrary;
import com.tisawesomeness.minecord.mc.StandardMCLibrary;
import com.tisawesomeness.minecord.mc.item.Item;
import com.tisawesomeness.minecord.mc.item.Recipe;
import com.tisawesomeness.minecord.network.APIClient;
import com.tisawesomeness.minecord.network.OkAPIClient;
import com.tisawesomeness.minecord.util.*;

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

import java.awt.Color;
import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

public class Bot {

    private static final String mainClass = "com.tisawesomeness.minecord.Main";
    public static final String author = "Tis_awesomeness";
    public static final String authorTag = "@Tis_awesomeness#8617";
    public static final String invite = "https://minecord.github.io/invite";
    public static final String helpServer = "https://minecord.github.io/support";
    public static final String website = "https://minecord.github.io";
    public static final String github = "https://github.com/Tisawesomeness/Minecord";
    private static final String version = "0.16.3";
    public static final String javaVersion = "1.8";
    public static final String jdaVersion = "5.0.0-alpha.18";
    public static final Color color = Color.GREEN;

    public static ShardManager shardManager;
    private static APIClient apiClient;
    public static MCLibrary mcLibrary;
    private static Listener listener;
    private static CommandListener commandListener;
    private static ReactListener reactListener;
    public static long birth;
    public static long bootTime;
    public static String[] args;

    public static Thread thread;
    public static volatile int readyShards = 0;
    private static final List<GatewayIntent> gateways = Arrays.asList(
            GatewayIntent.DIRECT_MESSAGES, GatewayIntent.DIRECT_MESSAGE_REACTIONS,
            GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS
    );

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

        //Pre-init
        thread = Thread.currentThread();
        listener = new Listener();
        commandListener = new CommandListener();
        reactListener = new ReactListener();
        apiClient = new OkAPIClient();
        mcLibrary = new StandardMCLibrary(apiClient);
        try {
            Announcement.init(Config.getPath());
            ColorUtils.init(Config.getPath());
            Item.init(Config.getPath());
            Recipe.init(Config.getPath());
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
                    jda.addEventListener(listener, commandListener, reactListener);
                }
                m.editMessage(":white_check_mark: **Bot reloaded!**").queue();
                MessageUtils.log(":arrows_counterclockwise: **Bot reloaded by " + u.getName() + "**");

                //If this is the first run
            } else {

                //Initialize JDA
                shardManager = DefaultShardManagerBuilder.createLight(Config.getClientToken(), gateways)
                        .setAutoReconnect(true)
                        .addEventListeners(listener, commandListener, reactListener)
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
                while (readyShards < shardManager.getShardsTotal()) {
                    //System.out.println("Ready shards: " + readyShards + " / " + shardManager.getShardsTotal());
                    Thread.sleep(100);
                }
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
            if (ws != null) ws.join();
            System.out.println("Bot ready!");
        } catch (InterruptedException ignored) {}

        //Post-init
        bootTime = System.currentTimeMillis() - birth;
        System.out.println("Boot Time: " + DateUtils.getBootTime());
        MessageUtils.log(":white_check_mark: **Bot started!**");
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
            jda.removeEventListener(listener, commandListener, reactListener);
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
