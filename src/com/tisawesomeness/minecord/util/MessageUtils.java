package com.tisawesomeness.minecord.util;

import com.tisawesomeness.minecord.Announcement;
import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.database.Database;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.Color;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

public class MessageUtils {
	
	/**
	 * Formats a message to look more fancy using an embed. Pass null in any argument (except color) to remove that aspect of the message.
	 * @param title The title or header of the message.
	 * @param url A URL that the title goes to when clicked. Only works if title is not null.
	 * @param body The main body of the message.
	 * @param color The color of the embed. Discord markdown formatting and newline are supported.
	 * @return A MessageEmbed representing the message. You can add additional info (e.g. fields) by passing this variable into a new EmbedBuilder.
	 */
	public static MessageEmbed embedMessage(String title, String url, String body, Color color) {
		EmbedBuilder eb = new EmbedBuilder();
		if (title != null) eb.setTitle(title, url);
		eb.setDescription(body);
		eb.setColor(color);
		eb = addFooter(eb);
		return eb.build();
	}
	
	public static EmbedBuilder addFooter(EmbedBuilder eb) {
		String announcement = Announcement.rollAnnouncement();
		if (Config.getOwner().equals("0")) {
			return eb.setFooter(announcement);
		}
		User owner = Bot.shardManager.retrieveUserById(Config.getOwner()).complete();
		return eb.setFooter(announcement, owner.getAvatarUrl());
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
		if (!Config.getLogChannel().equals("0")) {
			TextChannel tc = Bot.shardManager.getTextChannelById(Config.getLogChannel());
			if (tc == null) {
				System.err.println("Logging channel not found!");
			} else {
				tc.sendMessage(m).queue();
			}
		}
	}
	/**
	 * Logs a message to the logging channel.
	 */
	public static void log(MessageEmbed m) {
		if (!Config.getLogChannel().equals("0")) {
			EmbedBuilder eb = new EmbedBuilder(m);
			eb.setTimestamp(OffsetDateTime.now());
			TextChannel tc = Bot.shardManager.getTextChannelById(Config.getLogChannel());
			if (tc == null) {
				System.err.println("Logging channel not found!");
			} else {
				tc.sendMessageEmbeds(eb.build()).queue();
			}
		}
	}
	/**
	 * Logs a message to the join log channel.
	 */
	public static void logJoin(MessageEmbed m) {
		if (!Config.getJoinLogChannel().equals("0")) {
			EmbedBuilder eb = new EmbedBuilder(m);
			eb.setTimestamp(OffsetDateTime.now());
			TextChannel tc = Bot.shardManager.getTextChannelById(Config.getJoinLogChannel());
			if (tc == null) {
				System.err.println("Join log channel not found!");
			} else {
				tc.sendMessageEmbeds(eb.build()).queue();
			}
		}
	}
	
	/**
	 * Gets the command-useful content of a message, keeping the name and arguments and purging the prefix and mention.
	 */
	public static String[] getContent(Message m, String prefix, SelfUser su) {
		String content = m.getContentRaw();
		if (m.getContentRaw().startsWith(prefix)) {
			return content.replaceFirst(Pattern.quote(prefix), "").split(" ");
		} else if (content.replace("@!", "@").startsWith(su.getAsMention())) {
			String[] args = content.split(" ");
			return ArrayUtils.remove(args, 0);
		} else {
			return null;
		}
	}

	/**
	 * Gets the prefix the bot should use in a text or private channel
	 * @param e The event corresponding to a command
	 * @return The configured prefix if e is for a text channel, or the default otherwise
	 */
	public static String getPrefix(MessageReceivedEvent e) {
		return e.isFromGuild() ? Database.getPrefix(e.getGuild().getIdLong()) : Config.getPrefix();
	}

	/**
	 * Splits a string with newlines into groups, making sure that every group is a whole number of lines and at most the max length.
	 * The provided String is split into lines, then stored in an ArrayList that follows the other methods specifications.
	 * If a line is over the max length, it is split into multiple lines.
	 * Useful for getting past Discord's char limits.
	 * @param str An ArrayList of String lines without newline characters.
	 * @param maxLength The maximum length allowed for a string in the returned list.
	 * @return A list of strings where every string length < maxLength - 1
	 */
	public static ArrayList<String> splitLinesByLength(String str, int maxLength) {
		return splitLinesByLength(new ArrayList<>(Arrays.asList(str.split("\n"))), maxLength);
	}

	/**
	 * Splits a list of lines into groups, making sure that none of them are over the max length.
	 * This takes into account the additional newline character.
	 * If a line is over the max length, it is split into multiple lines.
	 * Useful for getting past Discord's char limits.
	 * @param lines An ArrayList of String lines without newline characters.
	 * @param maxLength The maximum length allowed for a string in the returned list.
	 * @return A list of strings where every string length < maxLength - 1
	 */
	public static ArrayList<String> splitLinesByLength(ArrayList<String> lines, int maxLength) {
		ArrayList<String> split = new ArrayList<>();
		String splitBuf = "";
		for (int i = 0; i < lines.size(); i++) {
			// Max line length check
			if (lines.get(i).length() > maxLength - 1) {
				lines.add(i + 1, lines.get(i).substring(maxLength - 1));
				lines.set(i, lines.get(i).substring(0, maxLength - 1));
			}
			String fieldTemp = splitBuf + lines.get(i) + "\n";
			if (fieldTemp.length() > maxLength) {
				i -= 1; // The line goes over the char limit, don't include!
				split.add(splitBuf.substring(0, splitBuf.length() - 1));
				splitBuf = "";
			} else {
				splitBuf = fieldTemp;
			}
			// Last line check
			if (i == lines.size() - 1) {
				split.add(fieldTemp);
			}
		}
		return split;
	}

	/**
	 * Gets the total number of characters in a list of lines, adding 1 for each newline
	 * @param lines An ArrayList of String lines, without newline characters
	 * @return The total number of characters
	 */
	public static int getTotalChars(ArrayList<String> lines) {
		int chars = 0;
		for (String line : lines) {
			chars += line.length() + 1; // +1 for newline
		}
		return chars;
	}

}
