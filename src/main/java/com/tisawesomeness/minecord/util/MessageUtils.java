package com.tisawesomeness.minecord.util;

import com.tisawesomeness.minecord.Config;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.SelfUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class MessageUtils {
	
	/**
	 * Parses boolean arguments.
	 * @param search The string array to search through.
	 * @param include A string that also means true.
	 * @return The first index of the boolean argument. Returns -1 if not found.
	 */
	public static int parseBoolean(String[] search, String include) {

		List<String> searchList = Arrays.asList(search);
		String[] words = new String[]{"true", "yes", "allow", include};
		for (String word : words) {
			int index = searchList.indexOf(word);
			if (index > -1) {
				return index;
			}
		}
		return -1;
	}
	
	/**
	 * Gets the command-useful content of a message, keeping the name and arguments and purging the prefix and mention.
	 */
	public static String[] getContent(Message m, String prefix, SelfUser su, Config config) {
		String content = m.getContentRaw();
		if (m.getContentRaw().startsWith(prefix)) {
			return content.replaceFirst(Pattern.quote(prefix), "").split(" ");
		} else if (config.respondToMentions && content.replaceFirst("@!", "@").startsWith(su.getAsMention())) {
			String[] args = content.split(" ");
			return Arrays.copyOfRange(args, 1, args.length);
		}
		return null;
	}
	
	public static String dateErrorString(String prefix, String cmd) {
		return ":x: Improperly formatted date. " +
			"At least a date or time is required. " +
			"Do `" + prefix + cmd + "` for more info.";
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
		ArrayList<String> split = new ArrayList<String>();
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
