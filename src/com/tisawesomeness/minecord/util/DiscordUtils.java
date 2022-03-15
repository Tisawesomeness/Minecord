package com.tisawesomeness.minecord.util;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Config;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiscordUtils {

	private static final Pattern ID_PATTERN = Pattern.compile("[0-9]{2,32}");
	private static final Pattern USER_MENTION_PATTERN = Pattern.compile("(<@!?)?([0-9]{2,32})>?");
	private static final Pattern CHANNEL_MENTION_PATTERN = Pattern.compile("(<#)?([0-9]{2,32})>?");

	public static boolean isDiscordId(String str) {
		return ID_PATTERN.matcher(str).matches();
	}
	
	public static void update() {
		Bot.shardManager.setActivity(Activity.playing(parseAll(Config.getGame())));
	}

	/**
	 * Replaces constants in the input string with their values
	 * This can be called during init, as long as Config is initialized
	 * @param input A string with {constants}
	 * @return The string with resolved constants, though variables such as {guilds} are unresolved
	 */
	public static String parseConstants(String input) {
		return input
			.replace("{author}", Config.getAuthor())
			.replace("{author_tag}", Config.getAuthorTag())
			.replace("{help_server}", Config.getHelpServer())
			.replace("{website}", Config.getWebsite())
			.replace("{github}", Config.getGithub())
			.replace("{java_ver}", Bot.javaVersion)
			.replace("{jda_ver}", Bot.jdaVersion)
			.replace("{version}", Bot.getVersion())
			.replace("{invite}", Config.getInvite())
			.replace("{prefix}", Config.getPrefix());
	}

	/**
	 * Replaces variables in the input string with their values
	 * This must be called after init
	 * @param input A string with {variables}
	 * @return The string with resolved variables, though constants such as {version} are unresolved
	 */
	public static String parseVariables(String input) {
		return input.replace("{guilds}", String.valueOf(Bot.shardManager.getGuilds().size()));
	}

	/**
	 * Replaces variables and constants in the input string with their values
	 * This must be called after init
	 * @param input A string with {variables}
	 * @return The string with resolved variables
	 */
	public static String parseAll(String input) {
		return parseVariables(parseConstants(input));
	}
	
	public static User findUser(String search) {
		if (isDiscordId(search)) {
			return Bot.shardManager.retrieveUserById(search).complete();
		}
		Matcher ma = USER_MENTION_PATTERN.matcher(search);
		return ma.matches() ? Bot.shardManager.retrieveUserById(ma.group(2)).complete() : null;
	}
	
	public static TextChannel findChannel(String search) {
		if (isDiscordId(search)) {
			return Bot.shardManager.getTextChannelById(search);
		}
		Matcher ma = CHANNEL_MENTION_PATTERN.matcher(search);
		return ma.matches() ? Bot.shardManager.getTextChannelById(ma.group(2)) : null;
	}

    /**
     * Gets the emote text associated with true or false.
     */
    public static String getBoolEmote(boolean bool) {
        return bool ? ":white_check_mark:" : ":x:";
    }

}
