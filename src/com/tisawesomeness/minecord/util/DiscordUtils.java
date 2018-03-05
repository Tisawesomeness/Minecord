package com.tisawesomeness.minecord.util;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Config;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.Game.GameType;

public class DiscordUtils {
	
	public static List<User> getUsers() {
		Set<User> users = new LinkedHashSet<User>();
		for (JDA jda : Bot.shards) {
			users.addAll(jda.getUsers());
		}
		return new ArrayList<User>(users);
	}
	
	public static User getUserById(String id) {
		for (JDA jda : Bot.shards) {
			User user = jda.getUserById(id);
			if (user != null) return user;
		}
		return null;
	}
	
	public static List<TextChannel> getTextChannels() {
		Set<TextChannel> channels = new LinkedHashSet<TextChannel>();
		for (JDA jda : Bot.shards) {
			channels.addAll(jda.getTextChannels());
		}
		return new ArrayList<TextChannel>(channels);
	}
	
	public static TextChannel getTextChannelById(String id) {
		for (JDA jda : Bot.shards) {
			TextChannel channel = jda.getTextChannelById(id);
			if (channel != null) return channel;
		}
		return null;
	}
	
	public static List<Guild> getGuilds() {
		Set<Guild> guilds = new LinkedHashSet<Guild>();
		for (JDA jda : Bot.shards) {
			guilds.addAll(jda.getGuilds());
		}
		return new ArrayList<Guild>(guilds);
	}
	
	public static Guild getGuildById(String id) {
		for (JDA jda : Bot.shards) {
			Guild guild = jda.getGuildById(id);
			if (guild != null) return guild;
		}
		return null;
	}
	
	public static void update() {
		for (JDA jda : Bot.shards) {
			jda.getPresence().setGame(Game.of(GameType.DEFAULT, Config.getGame()
				.replaceAll("\\{prefix\\}", Config.getPrefix())
				.replaceAll("\\{guilds\\}", String.valueOf(getGuilds().size()))
				.replaceAll("\\{users\\}", String.valueOf(getUsers().size()))
				.replaceAll("\\{channels\\}", String.valueOf(getTextChannels().size()))
			));
			if (!"".equals(Config.getName())) {
				jda.getSelfUser().getManager().setName(Config.getName()).queue();
			}
		}
	}
	
	public static User findUser(String search) {
		Matcher ma = Pattern.compile("(<@!?)?([0-9]{18})>?").matcher(search);
		return ma.matches() ? getUserById(ma.group(2)) : null;
	}

}
