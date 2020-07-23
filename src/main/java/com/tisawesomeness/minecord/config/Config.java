package com.tisawesomeness.minecord.config;

import com.tisawesomeness.minecord.Lang;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Loads the config options from config.json.
 */
public class Config {

	public final String clientToken;
	public final int shardCount;
	public final List<Long> owners;

	public final int presenceChangeInterval;
	public final PresenceBehavior presenceBehavior;
	public final List<BotPresence> presences;

	public final String prefixDefault;
	public final Lang langDefault;
	public final boolean useMenusDefault;
	public final long logChannel;
	public final String invite;
	public final boolean useAnnouncements;
	public final boolean debugMode;
	public final boolean respondToMentions;
	public final boolean sendTyping;
	public final boolean showMemory;
	public final boolean elevatedSkipCooldown;

	public final boolean sendServerCount;
	public final int sendGuildsInterval;
	public final String pwToken;
	public final String orgToken;
	public final boolean receiveVotes;
	public final String webhookURL;
	public final int webhookPort;
	public final String webhookAuth;

	public final String dbPath;

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
		owners = parseOwners(config);

		JSONObject presence = config.getJSONObject("presence");
		presenceChangeInterval = presence.getInt("presenceChangeInterval");
		presenceBehavior = PresenceBehavior.fromKey(presence.getString("presenceBehavior"));

		JSONObject settings = config.getJSONObject("settings");
		prefixDefault = settings.getString("prefix");
		langDefault = Lang.from(settings.getString("lang")).orElseThrow(IllegalArgumentException::new);
		useMenusDefault = settings.getBoolean("useMenus");
		logChannel = settings.getLong("logChannel");
		invite = settings.getString("invite");
		useAnnouncements = settings.getBoolean("useAnnouncements");
		debugMode = settings.getBoolean("debugMode");
		respondToMentions = settings.getBoolean("respondToMentions");
		sendTyping = settings.getBoolean("sendTyping");
		showMemory = settings.getBoolean("showMemory");
		elevatedSkipCooldown = settings.getBoolean("elevatedSkipCooldown");

		JSONObject botLists = config.getJSONObject("botLists");
		sendServerCount = botLists.getBoolean("sendServerCount");
		sendGuildsInterval = botLists.getInt("sendGuildsInterval");
		pwToken = botLists.getString("pwToken");
		orgToken = botLists.getString("orgToken");
		receiveVotes = botLists.getBoolean("receiveVotes");
		webhookURL = botLists.getString("webhookURL");
		webhookPort = botLists.getInt("webhookPort");
		webhookAuth = botLists.getString("webhookAuth");

		JSONObject database = config.getJSONObject("database");
		dbPath = database.getString("path");

		// Processed last since it depends on some config variables
		presences = parsePresences(presence.getJSONArray("presences"));

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

	private static List<Long> parseOwners(JSONObject config) {
		JSONArray arr = config.getJSONArray("owners");
		List<Long> list = new ArrayList<>();
		for (int i = 0; i < arr.length(); i++) {
			list.add(arr.getLong(i));
		}
		return Collections.unmodifiableList(list);
	}

	private List<BotPresence> parsePresences(JSONArray activities) {
		if (activities.length() == 0) {
			return Collections.singletonList(BotPresence.defaultPresence());
		}
		List<BotPresence> list = new ArrayList<>();
		for (int i = 0; i < activities.length(); i++) {
			list.add(new BotPresence(activities.getJSONObject(i), this));
		}
		return Collections.unmodifiableList(list);
	}

	/**
	 * Determines if the given ID is listed in the config as an owner.
	 * <br>The config is not guarenteed to have any owners.
	 * @param id The 17-20 digit ID, though invalid IDs return false
	 */
	public boolean isOwner(long id) {
		return owners.contains(id);
	}

	/**
	 * Determines if the given ID is listed in the config as an owner.
	 * <br>The config is not guarenteed to have any owners.
	 * @param id The 17-20 digit string ID (this method is safe for any input)
	 */
	public boolean isOwner(@Nullable String id) {
		if (id == null) {
			return false;
		}
		return owners.stream()
				.map(Object::toString)
				.anyMatch(s -> s.equals(id));
	}

}
