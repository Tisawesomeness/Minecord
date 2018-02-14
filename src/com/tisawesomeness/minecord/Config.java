package com.tisawesomeness.minecord;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class Config {

	private static String clientToken;
	private static int shardCount;
	private static String owner;
	
	private static String logChannel;
	private static String invite;
	private static String prefix;
	private static String game;
	private static String name;
	private static boolean devMode;
	private static boolean debugMode;
	private static boolean respondToMentions;
	private static boolean deleteCommands;
	private static boolean sendTyping;
	private static boolean showMemory;
	private static boolean elevatedSkipCooldown;
	
	private static boolean sendServerCount;
	private static String pwToken;
	private static String netToken;
	private static String orgToken;
	private static boolean fetchVotes;
	
	private static String type;
	private static String host;
	private static int port;
	private static String dbName;
	private static String user;
	private static String pass;

	public static void read(boolean reload) {
		
		//Look for client token
		if (!reload) {
			String[] args = Bot.args;
			if (args.length > 1 && ArrayUtils.contains(args, "-t")) {
				int index = ArrayUtils.indexOf(args, "-t");
				if (index + 1 < args.length) {
					args = ArrayUtils.remove(args, index);
					String token = args[index];
					if (token.matches("{32,}")) {
						System.out.println("Found custom client token: " + token);
						clientToken = token;
						args = ArrayUtils.remove(args, index);
					}
					Bot.args = args;
				}
			}
		}
		
		//Parse config path
		String path = "./config.json";
		if (Bot.args.length > 1 && ArrayUtils.contains(Bot.args, "-c")) {
			int index = ArrayUtils.indexOf(Bot.args, "-c");
			if (index + 1 < Bot.args.length) {
				path = Bot.args[index + 1];
				System.out.println("Found custom config path: " + path);
			}
		}
		
		try {
			//Parse config JSON
			JSONObject config = new JSONObject(FileUtils.readFileToString(new File(path), "UTF-8"));
			if (clientToken == null) clientToken = config.getString("clientToken");
			shardCount = config.getInt("shardCount");
			if (shardCount < 1) shardCount = 1;
			owner = config.getString("owner");
			
			JSONObject settings = config.getJSONObject("settings");
			logChannel = settings.getString("logChannel");
			invite = settings.getString("invite");
			prefix = settings.getString("prefix");
			game = settings.getString("game");
			name = settings.getString("name");
			devMode = settings.getBoolean("devMode");
			debugMode = settings.getBoolean("debugMode");
			respondToMentions = settings.getBoolean("respondToMentions");
			deleteCommands = settings.getBoolean("deleteCommands");
			sendTyping = settings.getBoolean("sendTyping");
			showMemory = settings.getBoolean("showMemory");
			elevatedSkipCooldown = settings.getBoolean("elevatedSkipCooldown");
			
			JSONObject botLists = config.getJSONObject("botLists");
			sendServerCount = botLists.getBoolean("sendServerCount");
			pwToken = botLists.getString("pwToken");
			netToken = botLists.getString("netToken");
			orgToken = botLists.getString("orgToken");
			fetchVotes = botLists.getBoolean("fetchVotes");
			
			JSONObject database = config.getJSONObject("database");
			type = database.getString("type");
			host = database.getString("host");
			port = database.getInt("port");
			dbName = database.getString("name");
			user = database.getString("user");
			pass = database.getString("pass");
			
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}
	}

	protected static String getClientToken() {return clientToken;}
	public static int getShardCount() {return shardCount;}
	public static String getOwner() {return owner;}

	public static String getLogChannel() {return logChannel;}
	public static String getInvite() {return invite;}
	public static String getPrefix() {return prefix;}
	public static String getGame() {return game;}
	public static String getName() {return name;}
	public static boolean getDevMode() {return devMode;}
	public static boolean getDebugMode() {return debugMode;}
	public static boolean getRespondToMentions() {return respondToMentions;}
	public static boolean getDeleteCommands() {return deleteCommands;}
	public static boolean getSendTyping() {return sendTyping;}
	public static boolean getShowMemory() {return showMemory;}
	public static boolean getElevatedSkipCooldown() {return elevatedSkipCooldown;}
	
	public static boolean getSendServerCount() {return sendServerCount;}
	public static String getPwToken() {return pwToken;}
	public static String getNetToken() {return netToken;}
	public static String getOrgToken() {return orgToken;}
	public static boolean getFetchVotes() {return fetchVotes;}
	
	public static String getType() {return type;}
	public static String getHost() {return host;}
	public static int getPort() {return port;}
	public static String getDbName() {return dbName;}
	public static String getUser() {return user;}
	public static String getPass() {return pass;}
	
}
