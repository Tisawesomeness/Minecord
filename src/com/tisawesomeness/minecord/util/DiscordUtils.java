package com.tisawesomeness.minecord.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Config;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class DiscordUtils {

	public static final String idRegex = "[0-9]{18}";
	
	public static void update() {
		Bot.shardManager.setActivity(Activity.playing(Config.getGame()
			.replaceAll("\\{prefix\\}", Config.getPrefix())
			.replaceAll("\\{guilds\\}", String.valueOf(Bot.shardManager.getGuilds().size()))
		));
	}
	
	public static User findUser(String search) {
		Matcher ma = Pattern.compile("(<@!?)?([0-9]{18})>?").matcher(search);
		return ma.matches() ? Bot.shardManager.getUserById(ma.group(2)) : null;
	}
	
	public static TextChannel findChannel(String search) {
		Matcher ma = Pattern.compile("(<#)?([0-9]{18})>?").matcher(search);
		return ma.matches() ? Bot.shardManager.getTextChannelById(ma.group(2)) : null;
	}

}
