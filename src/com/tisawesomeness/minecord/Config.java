package com.tisawesomeness.minecord;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tisawesomeness.minecord.util.DiscordUtils;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Game.GameType;

public class Config {

	private static String clientToken;
	private static int shardCount;
	private static String owner;
	private static boolean devMode;
	private static boolean debugMode;
	private static String logChannel;
	private static boolean sendServerCount;
	private static String pwToken;
	private static String netToken;
	private static String orgToken;
	private static String game;
	private static String name;
	private static String prefix;
	private static boolean respondToMentions;
	private static boolean deleteCommands;
	private static boolean sendTyping;
	private static String invite;
	private static boolean showMemory;
	private static boolean elevatedSkipCooldown;
	
	private static ArrayList<String> elevatedUsers = new ArrayList<String>();

	public static void read(File file) {
		
		//Look for client token
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
		
		try {
			//Parse config JSON
			JSONObject config = new JSONObject(FileUtils.readFileToString(file, "UTF-8"));
			if (clientToken == null) {
				clientToken = config.getString("clientToken");
			}
			shardCount = config.getInt("shardCount");
			if (shardCount < 1) shardCount = 1;
			
			owner = config.getString("owner");
			devMode = config.getBoolean("devMode");
			debugMode = config.getBoolean("debugMode");
			logChannel = config.getString("logChannel");
			sendServerCount = config.getBoolean("sendServerCount");
			pwToken = config.getString("pwToken");
			netToken = config.getString("netToken");
			orgToken = config.getString("orgToken");
			game = config.getString("game");
			name = config.getString("name");
			prefix = config.getString("prefix");
			respondToMentions = config.getBoolean("respondToMentions");
			deleteCommands = config.getBoolean("deleteCommands");
			sendTyping = config.getBoolean("sendTyping");
			invite = config.getString("invite");
			showMemory = config.getBoolean("showMemory");
			elevatedSkipCooldown = config.getBoolean("elevatedSkipCooldown");
			
			JSONArray eu = config.getJSONArray("elevatedUsers");
			for (int i = 0; i < eu.length(); i++) {
				elevatedUsers.add(eu.getString(i));
			}
			
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void update() {
		for (JDA jda : Bot.shards) {
			jda.getPresence().setGame(Game.of(GameType.DEFAULT, game
				.replaceAll("\\{prefix\\}", prefix)
				.replaceAll("\\{guilds\\}", String.valueOf(DiscordUtils.getGuilds().size()))
				.replaceAll("\\{users\\}", String.valueOf(DiscordUtils.getUsers().size()))
				.replaceAll("\\{channels\\}", String.valueOf(DiscordUtils.getTextChannels().size()))
			));
			if (!"".equals(name)) {
				jda.getSelfUser().getManager().setName(name).queue();
			}
		}
	}

	protected static String getClientToken() {return clientToken;}
	public static int getShardCount() {return shardCount;}
	public static String getOwner() {return owner;}
	public static boolean getDevMode() {return devMode;}
	public static boolean getDebugMode() {return debugMode;}
	public static String getLogChannel() {return logChannel;}
	public static boolean getSendServerCount() {return sendServerCount;}
	public static String getPwToken() {return pwToken;}
	public static String getNetToken() {return netToken;}
	public static String getOrgToken() {return orgToken;}
	public static String getGame() {return game;}
	public static String getName() {return name;}
	public static String getPrefix() {return prefix;}
	public static boolean getRespondToMentions() {return respondToMentions;}
	public static boolean getDeleteCommands() {return deleteCommands;}
	public static boolean getSendTyping() {return sendTyping;}
	public static String getInvite() {return invite;}
	public static boolean getShowMemory() {return showMemory;}
	public static boolean getElevatedSkipCooldown() {return elevatedSkipCooldown;}
	
	public static ArrayList<String> getElevatedUsers() {return elevatedUsers;}
	
}
