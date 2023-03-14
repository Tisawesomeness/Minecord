package com.tisawesomeness.minecord;

import com.tisawesomeness.minecord.util.ArrayUtils;
import com.tisawesomeness.minecord.util.RequestUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Config {

    private static String clientToken;
    private static int shardCount;
    private static String owner;
    private static List<String> testServers;

    private static String logChannel;
    private static String joinLogChannel;
    private static String logWebhook;
    private static String statusWebhook;
    private static boolean isSelfHosted;
    private static String author;
    private static String authorTag;
    private static String invite;
    private static String helpServer;
    private static String website;
    private static String github;
    private static String prefix;
    private static String game;
    private static boolean evil;
    private static boolean devMode;
    private static boolean debugMode;
    private static boolean deleteCommands;
    private static boolean useMenus;
    private static boolean showMemory;
    private static boolean elevatedSkipCooldown;
    private static boolean useElectroidAPI;
    private static boolean useGappleAPI;
    private static boolean recordCacheStats;
    private static String itemImageHost;
    private static String recipeImageHost;

    private static boolean sendServerCount;
    private static String pwToken;
    private static String orgToken;
    private static boolean receiveVotes;
    private static String webhookURL;
    private static int webhookPort;
    private static String webhookAuth;

    private static String type;
    private static String host;
    private static int port;
    private static String dbName;
    private static String user;
    private static String pass;

    private static String path;

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
            isSelfHosted = settings.getBoolean("isSelfHosted");
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
            useElectroidAPI = settings.optBoolean("useElectroidAPI", true);
            useGappleAPI = settings.optBoolean("useGappleAPI", true);
            recordCacheStats = settings.optBoolean("recordCacheStats", false);
            itemImageHost = settings.optString("itemImageHost", "https://minecord.github.io/item/");
            recipeImageHost = settings.optString("recipeImageHost", "https://minecord.github.io/recipe/");

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

    public static String getClientToken() { return clientToken; }
    public static int getShardCount() { return shardCount; }
    public static String getOwner() { return owner; }
    public static List<String> getTestServers() { return testServers; }

    public static String getLogChannel() { return logChannel; }
    public static String getJoinLogChannel() { return joinLogChannel; }
    public static String getLogWebhook() { return logWebhook; }
    public static String getStatusWebhook() { return statusWebhook; }
    public static boolean isSelfHosted() { return isSelfHosted; }
    public static String getAuthor() { return author; }
    public static String getAuthorTag() { return authorTag; }
    public static String getInvite() { return invite; }
    public static String getHelpServer() { return helpServer; }
    public static String getWebsite() { return website; }
    public static String getGithub() { return github; }
    public static String getPrefix() { return prefix; }
    public static String getGame() { return game; }
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
    public static String getItemImageHost() { return itemImageHost; }
    public static String getRecipeImageHost() { return recipeImageHost; }

    public static boolean getSendServerCount() { return sendServerCount; }
    public static String getPwToken() { return pwToken; }
    public static String getOrgToken() { return orgToken; }
    public static boolean getReceiveVotes() { return receiveVotes; }
    public static String getWebhookURL() { return webhookURL; }
    public static int getWebhookPort() { return webhookPort; }
    public static String getWebhookAuth() { return webhookAuth; }

    public static String getType() { return type; }
    public static String getHost() { return host; }
    public static int getPort() { return port; }
    public static String getDbName() { return dbName; }
    public static String getUser() { return user; }
    public static String getPass() { return pass; }

    public static String getPath() { return path; }

}
