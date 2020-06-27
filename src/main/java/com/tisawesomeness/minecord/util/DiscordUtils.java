package com.tisawesomeness.minecord.util;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Config;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiscordUtils {

	public static final String idRegex = "[0-9]{2,32}";
	
	public static void update(ShardManager sm, Config config) {
		sm.setActivity(Activity.playing(parseAll(config.game, config)));
	}

	/**
	 * Replaces constants in the input string with their values
	 * This can be called during init, as long as Config is initialized
	 * @param input A string with {constants}
	 * @param config
	 * @return The string with resolved constants, though variables such as {guilds} are unresolved
	 */
	public static String parseConstants(String input, Config config) {
		return input
			.replace("{author}", Bot.author)
			.replace("{author_tag}", Bot.authorTag)
			.replace("{help_server}", Bot.helpServer)
			.replace("{website}", Bot.website)
			.replace("{github}", Bot.github)
			.replace("{java_ver}", Bot.javaVersion)
			.replace("{jda_ver}", Bot.jdaVersion)
			.replace("{version}", Bot.version)
			.replace("{invite}", config.invite)
			.replace("{prefix}", config.prefixDefault);
	}

	/**
	 * Replaces variables in the input string with their values
	 * This must be called after init
	 * @param input A string with {variables}
	 * @return The string with resolved variables, though constants such as {version} are unresolved
	 */
	public static String parseVariables(String input) {
		// TODO currently not used, temporarily disabled
//		return input.replace("{guilds}", String.valueOf(Bot.shardManager.getGuilds().size()));
		return input;
	}

	/**
	 * Replaces variables and constants in the input string with their values
	 * This must be called after init
	 * @param input A string with {variables}
	 * @return The string with resolved variables
	 */
	public static String parseAll(String input, Config config) {
		return parseVariables(parseConstants(input, config));
	}
	
	public static User findUser(String search, ShardManager sm) {
		Matcher ma = Pattern.compile("(<@!?)?([0-9]{18})>?").matcher(search);
		return ma.matches() ? sm.getUserById(ma.group(2)) : null;
	}
	
	public static TextChannel findChannel(String search, ShardManager sm) {
		Matcher ma = Pattern.compile("(<#)?([0-9]{18})>?").matcher(search);
		return ma.matches() ? sm.getTextChannelById(ma.group(2)) : null;
	}

}
