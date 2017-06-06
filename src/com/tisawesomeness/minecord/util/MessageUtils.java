package com.tisawesomeness.minecord.util;

import java.awt.Color;
import java.time.OffsetDateTime;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Config;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class MessageUtils {
	
	public static final String mentionRegex = "<@!?[0-9]+>";
	public static final String channelRegex = "<#[0-9]+>";
	public static final String idRegex = "[0-9]{18}";
	public static final String messageRegex = "[^ ]+ [^ ]+ ";
	
	public static final String dateHelp = 
		"In [date], you may define a date, time, and timezone." +
		"\n" + "Dates are `mm/dd` or `mm/dd/yyyy`" +
		"\n" + "Date Examples:" +
		"\n" + "`9/25`" +
		" | " + "`2/29/2012`" +
		" | " + "`5/15 8:30`" +
		" | " + "`3/2/06 2:47:32`" +
		" | " + "`9:00 PM`" +
		" | " + "`12/25/12 12:00 AM EST`" +
		" | " + "`5:22 CST`";
	
	/**
	 * Sends a notification message deleted after the amount of time set in the config.
	 * @param m Message to send
	 * @param c Channel to send message in
	 */
	public static void notify(String m, TextChannel c) {
		Message msg = c.sendMessage(m).complete();
		notifyInternal(msg, 1);
	}
	
	/**
	 * Sends a notification message deleted after the amount of time set in the config.
	 * @param m Message to send
	 * @param c Channel to send message in
	 */
	public static void notify(Message m, TextChannel c) {
		Message msg = c.sendMessage(m).complete();
		notifyInternal(msg, 1);
	}
	
	/**
	 * Sends a notification message deleted after the amount of time set in the config.
	 * @param m Message to send
	 * @param c Channel to send message in
	 */
	public static void notify(String m, TextChannel c, double multiplier) {
		Message msg = c.sendMessage(m).complete();
		notifyInternal(msg, multiplier);
	}
	
	/**
	 * Sends a notification message deleted after the amount of time set in the config.
	 * @param m Message to send
	 * @param c Channel to send message in
	 */
	public static void notify(Message m, TextChannel c, double multiplier) {
		Message msg = c.sendMessage(m).complete();
		notifyInternal(msg, multiplier);
	}
	
	private static void notifyInternal(Message m, double multiplier) {
		if (Config.getNotificationTime() >= 0) {
			class Clean extends TimerTask {
				@Override
				public void run() {
					m.delete().queue();
				}
			}
		
			Timer timer = new Timer();
			timer.schedule(new Clean(), (long) (Config.getNotificationTime()*multiplier));
		}
	}
	
	/**
	 * Formats a message to look more fancy using an embed. Pass null in any argument (except color) to remove that aspect of the message.
	 * @param title The title or header of the message.
	 * @param url A URL that the title goes to when clicked. Only works if title is not null.
	 * @param body The main body of the message.
	 * @param color The color of the embed. Discord markdown formatting and newline are supported.
	 * @param thumb The URL of the thumbnail.
	 * @return A MessageEmbed representing the message. You can add additional info (e.g. fields) by passing this variable into a new EmbedBuilder.
	 */
	public static MessageEmbed embedMessage(String title, String url, String body, Color color) {
		EmbedBuilder eb = new EmbedBuilder();
		if (title != null) {eb.setAuthor(title, url, null);}
		eb.setDescription(body);
		eb.setColor(color);
		eb = addFooter(eb);
		return eb.build();
	}
	
	/**
	 * Formats an image to look more fancy using an embed.
	 * @param title The title or header.
	 * @param url The URL of the image.
	 * @param color The color of the embed. Discord markdown formatting and newline are supported.
	 * @return A MessageEmbed representing the message. You can add additional info (e.g. fields) by passing this variable into a new EmbedBuilder.
	 */
	public static MessageEmbed embedImage(String title, String url, Color color) {
		EmbedBuilder eb = new EmbedBuilder();
		if (title != null) {eb.setAuthor(title, null, null);}
		eb.setImage(url);
		eb.setColor(color);
		eb = addFooter(eb);
		return eb.build();
	}
	
	public static EmbedBuilder addFooter(EmbedBuilder eb) {
		User user = Bot.jda.getUserById(Config.getOwner());
		return eb.setFooter("Minecord " + Bot.getVersion() + " | Made with \u2764 by " + user.getName(),
			user.getAvatarUrl());
	}
	
	/**
	 * Returns one of 16 random colors
	 */
	public static Color randomColor() {
		final Color[] colors = new Color[]{
			new Color(0, 0, 0),
			new Color(0, 0, 170),
			new Color(0, 170, 0),
			new Color(0, 170, 170),
			new Color(170, 0, 0),
			new Color(170, 0, 170),
			new Color(255, 170, 0),
			new Color(170, 170, 170),
			new Color(85, 85, 85),
			new Color(85, 85, 255),
			new Color(85, 255, 85),
			new Color(85, 255, 255),
			new Color(255, 85, 85),
			new Color(255, 85, 255),
			new Color(255, 255, 85),
			new Color(255, 255, 255)
		};
		return colors[new Random().nextInt(colors.length)];
	}
	
	/**
	 * Parses boolean arguments.
	 * @param search The string array to search through.
	 * @param include A string that also means true.
	 * @return The first index of the boolean argument. Returns -1 if not found.
	 */
	public static int parseBoolean(String[] search, String include) {
		
		String[] words = new String[]{"true", "yes", "allow", include};
		for (String word : words) {
			int index = ArrayUtils.indexOf(search, word);
			if (index > -1) {
				return index;
			}
		}
		return -1;
	}
	
	/**
	 * Logs a message to the logging channel.
	 */
	public static void log(String m) {
		Bot.jda.getTextChannelById(Config.getLogChannel()).sendMessage(m).queue();
	}
	/**
	 * Logs a message to the logging channel.
	 */
	public static void log(Message m) {
		Bot.jda.getTextChannelById(Config.getLogChannel()).sendMessage(m).queue();
	}
	/**
	 * Logs a message to the logging channel.
	 */
	public static void log(MessageEmbed m) {
		EmbedBuilder eb = new EmbedBuilder(m);
		eb.setTimestamp(OffsetDateTime.now());
		Bot.jda.getTextChannelById(Config.getLogChannel()).sendMessage(eb.build()).queue();
	}
	
	/**
	 * Gets the command-useful content of a message, keeping the name and arguments and purging the prefix and mention.
	 */
	public static String[] getContent(Message m, boolean raw) {
		if (m.getContent().startsWith(Config.getPrefix())) {
			String content = null;
			if (raw) {
				content = m.getRawContent();
			} else {
				content = m.getContent();
			}
			return content.replaceFirst(Pattern.quote(Config.getPrefix()), "").split(" ");
		} else if (m.getRawContent().replaceFirst("@!", "@").startsWith(Bot.jda.getSelfUser().getAsMention())) {
			if (raw) {
				String[] args = m.getRawContent().split(" ");
				return ArrayUtils.removeElement(args, args[0]);
			} else {
				String replace = "@" + m.getMentionedUsers().get(0).getName();
				String content = StringUtils.removeStart(m.getContent(), replace);
				if (content.length() >= 5 && content.substring(0, 5).matches("^#[0-9]{4}")) {
					content = content.replaceFirst("#[0-9]{4} ?", "");
				} else {
					content = content.substring(1);
				}
				return content.split(" ");
			}
		} else {
			return null;
		}
	}

}
