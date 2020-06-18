package com.tisawesomeness.minecord;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.json.JSONObject;

import javax.annotation.Nullable;

public class Config {

	private static String clientToken;
	private static int shardCount;
	private static String owner;

	private static String logChannel;
	private static String invite;
	private static String prefix;
	private static String game;
	private static boolean debugMode;
	private static boolean respondToMentions;
	private static boolean deleteCommands;
	private static boolean useMenus;
	private static boolean sendTyping;
	private static boolean showMemory;
	private static boolean elevatedSkipCooldown;

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

	public static void read(Path configPath, @Nullable String tokenOverride) throws IOException {

		// Parse config JSON
		JSONObject config = new JSONObject(new String(Files.readAllBytes(configPath)));

		clientToken = parseToken(config, tokenOverride);
		shardCount = config.getInt("shardCount");
		if (shardCount < 1) shardCount = 1;
		owner = config.getString("owner");

		JSONObject settings = config.getJSONObject("settings");
		logChannel = settings.getString("logChannel");
		invite = settings.getString("invite");
		prefix = settings.getString("prefix");
		game = settings.getString("game");
		debugMode = settings.getBoolean("debugMode");
		respondToMentions = settings.getBoolean("respondToMentions");
		deleteCommands = settings.getBoolean("deleteCommands");
		useMenus = settings.getBoolean("useMenus");
		sendTyping = settings.getBoolean("sendTyping");
		showMemory = settings.getBoolean("showMemory");
		elevatedSkipCooldown = settings.getBoolean("elevatedSkipCooldown");

		JSONObject botLists = config.getJSONObject("botLists");
		sendServerCount = botLists.getBoolean("sendServerCount");
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

	}

	public static String parseToken(JSONObject config, @Nullable String tokenOverride) {
		if (tokenOverride != null) {
			return tokenOverride;
		}
		return config.getString("clientToken");
	}

	public static String getClientToken() {return clientToken;}
	public static int getShardCount() {return shardCount;}
	public static String getOwner() {return owner;}

	public static String getLogChannel() {return logChannel;}
	public static String getInvite() {return invite;}
	public static String getPrefix() {return prefix;}
	public static String getGame() {return game;}
	public static boolean getDebugMode() {return debugMode;}
	public static boolean getRespondToMentions() {return respondToMentions;}
	public static boolean getDeleteCommands() {return deleteCommands;}
	public static boolean getUseMenus() {return useMenus;}
	public static boolean getSendTyping() {return sendTyping;}
	public static boolean getShowMemory() {return showMemory;}
	public static boolean getElevatedSkipCooldown() {return elevatedSkipCooldown;}

	public static boolean getSendServerCount() {return sendServerCount;}
	public static String getPwToken() {return pwToken;}
	public static String getOrgToken() {return orgToken;}
	public static boolean getReceiveVotes() {return receiveVotes;}
	public static String getWebhookURL() {return webhookURL;}
	public static int getWebhookPort() {return webhookPort;}
	public static String getWebhookAuth() {return webhookAuth;}

	public static String getType() {return type;}
	public static String getHost() {return host;}
	public static int getPort() {return port;}
	public static String getDbName() {return dbName;}
	public static String getUser() {return user;}
	public static String getPass() {return pass;}

}
