package com.tisawesomeness.minecord.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Config;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class DiscordUtils {
	
	public static void update() {
		Bot.shardManager.setGame(Game.playing(Config.getGame()
			.replaceAll("\\{prefix\\}", Config.getPrefix())
			.replaceAll("\\{guilds\\}", String.valueOf(Bot.shardManager.getGuilds().size()))
			.replaceAll("\\{users\\}", String.valueOf(Bot.shardManager.getUsers().size()))
			.replaceAll("\\{channels\\}", String.valueOf(Bot.shardManager.getTextChannels().size()))
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
