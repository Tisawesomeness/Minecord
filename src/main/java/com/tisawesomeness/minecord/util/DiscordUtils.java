package com.tisawesomeness.minecord.util;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.config.Config;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DiscordUtils {

	public static final String idRegex = "[0-9]{9,20}";

	public static final Pattern ANY_MENTION = Pattern.compile("<(@(!?|&)|#|:(.{2,32}):)\\d{17,20}>");

	/**
	 * Replaces constants in the input string with their values
	 * @param input A string with {constants}
	 * @param config The config file to get the invite and default prefix from
	 * @return The string with resolved constants, though variables such as {guilds} are unresolved
	 */
	public static String parseConstants(String input, Config config) {
		return input
			.replace("{author}", Bot.author)
			.replace("{author_tag}", Bot.authorTag)
			.replace("{help_server}", Bot.helpServer)
			.replace("{website}", Bot.website)
			.replace("{github}", Bot.github)
			.replace("{jda_ver}", Bot.jdaVersion)
			.replace("{version}", Bot.version)
			.replace("{invite}", config.invite)
			.replace("{prefix}", config.prefixDefault);
	}

	/**
	 * Replaces variables in the input string with their values
	 * @param input A string with {variables}
	 * @return The string with resolved variables, though constants such as {version} are unresolved
	 */
	public static String parseVariables(String input, ShardManager sm) {
		return input.replace("{guilds}", String.valueOf(sm.getGuildCache().size()));
	}
	
	public static User findUser(String search, ShardManager sm) {
		Matcher ma = Pattern.compile("(<@!?)?([0-9]{9,20})>?").matcher(search);
		return ma.matches() ? sm.getUserById(ma.group(2)) : null;
	}
	
	public static TextChannel findChannel(String search, ShardManager sm) {
		Matcher ma = Pattern.compile("(<#)?([0-9]{9,20})>?").matcher(search);
		return ma.matches() ? sm.getTextChannelById(ma.group(2)) : null;
	}

}
