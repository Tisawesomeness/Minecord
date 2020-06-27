package com.tisawesomeness.minecord;

import lombok.Getter;
import org.json.JSONObject;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Loads the config options from config.json.
 */
public class Config {

	// Some @Getters are omitted since {@code isDeleteCommandsDefault()} doesn't make sense.
	@Getter private final String clientToken;
	@Getter private final int shardCount;
	@Getter private final String owner;

	@Getter private final String prefixDefault;
	private final boolean deleteCommandsDefault;
	public boolean shouldDeleteCommandsDefault() { return deleteCommandsDefault; }
	private final boolean useMenusDefault;
	public boolean shouldUseMenusDefault() { return useMenusDefault; }

	@Getter private final String logChannel;
	@Getter private final String invite;
	@Getter private final String game;
	@Getter private final boolean debugMode;
	private final boolean respondToMentions;
	public boolean shouldRespondToMentions() { return respondToMentions; }
	private final boolean sendTyping;
	public boolean shouldSendTyping() { return sendTyping; }
	private final boolean showMemory;
	public boolean shouldShowMemory() { return showMemory; }
	private final boolean elevatedSkipCooldown;
	public boolean shouldElevatedSkipCooldown() { return elevatedSkipCooldown; }

	private final boolean sendServerCount;
	public boolean shouldSendServerCount() { return sendServerCount; }
	@Getter private final String pwToken;
	@Getter private final String orgToken;
	private final boolean receiveVotes;
	public boolean shouldReceiveVotes() { return receiveVotes; }
	@Getter private final String webhookURL;
	@Getter private final int webhookPort;
	@Getter private final String webhookAuth;

	@Getter private final String dbPath;

	/**
	 * Reads data from the config file.
	 * @param configPath The path to the config.
	 * @param tokenOverride The token used to override the config, or {@code null} for no override.
	 * @throws IOException When the config file couldn't be found
	 */
	public Config(Path configPath, @Nullable String tokenOverride) throws IOException {
		this(new JSONObject(new String(Files.readAllBytes(configPath))), tokenOverride);
	}

	/**
	 * Reads data from the config file.
	 * @param config The config JSON.
	 * @param tokenOverride The token used to override the config, or {@code null} for no override.
	 */
	public Config(JSONObject config, @Nullable String tokenOverride) {

		clientToken = parseToken(config, tokenOverride);
		shardCount = parseShards(config);
		owner = config.getString("owner");

		JSONObject settings = config.getJSONObject("settings");
		prefixDefault = settings.getString("prefix");
		deleteCommandsDefault = settings.getBoolean("deleteCommands");
		useMenusDefault = settings.getBoolean("useMenus");

		logChannel = settings.getString("logChannel");
		invite = settings.getString("invite");
		game = settings.getString("game");
		debugMode = settings.getBoolean("debugMode");
		respondToMentions = settings.getBoolean("respondToMentions");
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
		dbPath = database.getString("path");

	}

	private static String parseToken(JSONObject config, @Nullable String tokenOverride) {
		if (tokenOverride != null) {
			return tokenOverride;
		}
		return config.getString("clientToken");
	}

	private static int parseShards(JSONObject config) {
		return Math.max(1, config.getInt("shardCount"));
	}

}
