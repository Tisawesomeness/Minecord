package com.tisawesomeness.minecord;

import com.tisawesomeness.minecord.util.ArrayUtils;
import com.tisawesomeness.minecord.util.RequestUtils;
import lombok.Getter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Config {

    @Getter private static String clientToken;
    @Getter private static int shardCount;
    @Getter private static String owner;
    @Getter private static List<String> testServers;

    @Getter private static String logChannel;
    @Getter private static String joinLogChannel;
    @Getter private static String logWebhook;
    @Getter private static String statusWebhook;
    private static boolean includeSpamStatuses;
    @Getter private static String supportedMCVersion;
    private static boolean isSelfHosted;
    @Getter private static String author;
    @Getter private static String authorTag;
    @Getter private static String invite;
    @Getter private static String helpServer;
    @Getter private static String website;
    @Getter private static String github;
    @Getter private static String prefix;
    @Getter private static String game;
    private static boolean evil;
    private static boolean devMode;
    private static boolean debugMode;
    private static boolean deleteCommands;
    private static boolean useMenus;
    private static boolean showMemory;
    private static boolean elevatedSkipCooldown;
    @Getter private static int serverTimeout;
    @Getter private static int serverReadTimeout;
    private static boolean useElectroidAPI;
    private static boolean useGappleAPI;
    private static boolean recordCacheStats;
    @Getter private static String itemImageHost;
    @Getter private static String recipeImageHost;
    @Getter private static String crafatarHost;
    private static boolean reuploadCrafatarImages;

    private static boolean sendServerCount;
    @Getter private static String pwToken;
    @Getter private static String orgToken;
    private static boolean receiveVotes;
    @Getter private static String webhookURL;
    @Getter private static int webhookPort;
    @Getter private static String webhookAuth;

    @Getter private static String type;
    @Getter private static String host;
    @Getter private static int port;
    @Getter private static String dbName;
    @Getter private static String user;
    @Getter private static String pass;

    @Getter private static String path;

    public static void read(boolean reload) {

        //Look for client token
        if (!reload) {
            String[] args = Bot.args;
            if (args.length > 1 && ArrayUtils.contains(args, "-t")) {
                int index = ArrayUtils.indexOf(args, "-t");
                if (index + 1 < args.length) {
                    args = ArrayUtils.remove(args, index);
                    String token = args[index];
                    if (token.length() >= 32) {
                        System.out.println("Found custom client token (hash): " + token.hashCode());
                        clientToken = token;
                        args = ArrayUtils.remove(args, index);
                    }
                    Bot.args = args;
                }
            }
        }

        //Parse config path
        path = ".";
        if (Bot.args.length > 1 && ArrayUtils.contains(Bot.args, "-c")) {
            int index = ArrayUtils.indexOf(Bot.args, "-c");
            if (index + 1 < Bot.args.length) {
                path = Bot.args[index + 1];
                System.out.println("Found custom config path: " + path);
            }
        }

        //Parse config JSON
        try {
            JSONObject config = RequestUtils.loadJSON(path + "/config.json");
            if (clientToken == null) clientToken = config.getString("clientToken");
            shardCount = config.getInt("shardCount");
            if (shardCount < -1) {
                shardCount = -1;
            } else if (shardCount == 0) {
                shardCount = 1;
            }
            owner = config.getString("owner");

            testServers = new ArrayList<>();
            JSONArray arr = config.optJSONArray("testServers");
            if (arr != null) {
                arr.forEach(id -> testServers.add((String) id));
            }

            JSONObject settings = config.getJSONObject("settings");
            logChannel = settings.getString("logChannel");
            joinLogChannel = settings.optString("joinLogChannel", "0");
            logWebhook = settings.optString("logWebhook", "");
            statusWebhook = settings.optString("statusWebhook", "");
            includeSpamStatuses = settings.optBoolean("includeSpamStatuses", false);
            supportedMCVersion = settings.optString("supportedMCVersion", "1.21.5");
            isSelfHosted = settings.optBoolean("isSelfHosted", true);
            if (isSelfHosted) {
                author = settings.getString("author");
                authorTag = settings.getString("authorTag");
                invite = settings.getString("invite");
                helpServer = settings.getString("helpServer");
                website = settings.getString("website");
                github = settings.getString("github");
            } else {
                author = Bot.author;
                authorTag = Bot.authorTag;
                invite = Bot.invite;
                helpServer = Bot.helpServer;
                website = Bot.website;
                github = Bot.github;
            }
            prefix = settings.getString("prefix");
            game = settings.getString("game");
            evil = settings.optBoolean("evil", false);
            if (evil) {
                System.err.println("WARNING: Eval is enabled!");
            }
            devMode = settings.getBoolean("devMode");
            debugMode = settings.getBoolean("debugMode");
            deleteCommands = settings.getBoolean("deleteCommands");
            useMenus = settings.getBoolean("useMenus");
            showMemory = settings.getBoolean("showMemory");
            elevatedSkipCooldown = settings.getBoolean("elevatedSkipCooldown");
            serverTimeout = settings.optInt("serverTimeout", 5000);
            serverReadTimeout = settings.optInt("serverReadTimeout", 5000);
            useElectroidAPI = settings.optBoolean("useElectroidAPI", true);
            useGappleAPI = settings.optBoolean("useGappleAPI", true);
            recordCacheStats = settings.optBoolean("recordCacheStats", false);
            itemImageHost = settings.optString("itemImageHost", "https://minecord.github.io/item/");
            recipeImageHost = settings.optString("recipeImageHost", "https://minecord.github.io/recipe/");
            crafatarHost = settings.optString("crafatarHost", "https://crafatar.com/");
            reuploadCrafatarImages = settings.optBoolean("reuploadCrafatarImages", false);

            JSONObject botLists = config.getJSONObject("botLists");
            if (isSelfHosted) {
                sendServerCount = false;
            } else {
                sendServerCount = botLists.getBoolean("sendServerCount");
            }
            pwToken = botLists.getString("pwToken");
            orgToken = botLists.getString("orgToken");
            receiveVotes = botLists.getBoolean("receiveVotes");
            webhookURL = botLists.getString("webhookURL");
            webhookPort = botLists.getInt("webhookPort");
            webhookAuth = botLists.getString("webhookAuth");

            JSONObject database = config.getJSONObject("database");
            type = database.getString("type");
            host = database.getString("host");
            port = database.getInt("port");
            dbName = database.getString("name");
            user = database.getString("user");
            pass = database.getString("pass");

        } catch (JSONException | IOException ex) {
            ex.printStackTrace();
        }

    }

    public static boolean getIncludeSpamStatuses() { return includeSpamStatuses; }
    public static boolean isSelfHosted() { return isSelfHosted; }
    public static boolean getEvil() { return evil; }
    public static boolean getDevMode() { return devMode; }
    public static boolean getDebugMode() { return debugMode; }
    public static boolean getDeleteCommands() { return deleteCommands; }
    public static boolean getUseMenus() { return useMenus; }
    public static boolean getShowMemory() { return showMemory; }
    public static boolean getElevatedSkipCooldown() { return elevatedSkipCooldown; }
    public static boolean getUseElectroidAPI() { return useElectroidAPI; }
    public static boolean getUseGappleAPI() { return useGappleAPI; }
    public static boolean getRecordCacheStats() { return recordCacheStats; }
    public static boolean getReuploadCrafatarImages() { return reuploadCrafatarImages; }
    public static boolean getSendServerCount() { return sendServerCount; }
    public static boolean getReceiveVotes() { return receiveVotes; }

}
