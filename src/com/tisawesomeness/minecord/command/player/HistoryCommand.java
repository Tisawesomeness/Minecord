package com.tisawesomeness.minecord.command.player;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.database.Database;
import com.tisawesomeness.minecord.util.DateUtils;
import com.tisawesomeness.minecord.util.MessageUtils;
import com.tisawesomeness.minecord.util.NameUtils;
import com.tisawesomeness.minecord.util.RequestUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class HistoryCommand extends Command {
	
	public CommandInfo getInfo() {
		return new CommandInfo(
			"history",
			"Gets the name history of a player.",
			"<username|uuid> [date]",
			new String[]{"h"},
			2000,
			false,
			false,
			true
		);
	}
	
	public Result run(String[] args, MessageReceivedEvent e) {
		long id = e.getGuild().getIdLong();
		
		// No arguments message
		if (args.length == 0) {
			String m = ":warning: Incorrect arguments." +
				"\n" + Database.getPrefix(id) + "history <username|uuid> [date] " +
				"\n" + MessageUtils.dateHelp;
			return new Result(Outcome.WARNING, m, 5);
		}

		String player = args[0];	
		if (!player.matches(NameUtils.uuidRegex)) {
			String uuid = null;
			
			// Parse date argument
			if (args.length > 1) {
				long timestamp = DateUtils.getTimestamp(Arrays.copyOfRange(args, 1, args.length));
				if (timestamp == -1) {
					return new Result(Outcome.WARNING, MessageUtils.dateErrorString(id, "history"));
				}
				
			// Get the UUID
				uuid = NameUtils.getUUID(player, timestamp);
			} else {
				uuid = NameUtils.getUUID(player);
			}
			
			// Check for errors
			if (uuid == null) {
				String m = ":x: The Mojang API could not be reached." +
					"\n" + "Are you sure that username exists?" +
					"\n" + "Usernames are case-sensitive.";
				return new Result(Outcome.WARNING, m, 2);
			} else if (!uuid.matches(NameUtils.uuidRegex)) {
				String m = ":x: The API responded with an error:\n" + uuid;
				return new Result(Outcome.ERROR, m, 3);
			}
			
			player = uuid;
		}

		// Fetch name history
		String url = "https://api.mojang.com/user/profiles/" + player.replaceAll("-", "") + "/names";
		String request = RequestUtils.get(url);
		if (request == null) {
			return new Result(Outcome.ERROR, ":x: The Mojang API could not be reached.");
		}
		
		// Loop over each name change
		JSONArray names = new JSONArray(request);
		ArrayList<String> lines = new ArrayList<String>();
		for (int i = 0; i < names.length(); i++) {
			
			// Get info
			JSONObject change = names.getJSONObject(i);
			String name = change.getString("name");
			String date;
			if (change.has("changedToAt")) {
				date = DateUtils.getDateAgo(Instant.ofEpochMilli(change.getLong("changedToAt")).atOffset(ZoneOffset.UTC));
			} else {
				date = "Original";
			}
			
			// Add to lines in reverse
			lines.add(0, String.format("**%d.** `%s` | %s", i + 1, name, date));
		}

		// Get NameMC url
		player = names.getJSONObject(names.length() - 1).getString("name");
		url = "https://namemc.com/profile/" + player;
		
		// Proper apostrophe grammar
		if (player.endsWith("s")) {
			player += "' Name History";
		} else {
			player += "'s Name History";
		}

		EmbedBuilder eb = new EmbedBuilder()
			.setTitle(player, url)
			.setColor(Bot.color);

		// Truncate until 6000 char limit reached
		int chars = getTotalChars(lines);
		boolean truncated = false;
		while (chars > 6000 - 4) {
			truncated = true;
			lines.remove(lines.size() - 1);
			chars = getTotalChars(lines);
		}
		if (truncated) {
			lines.add("...");
		}
		// If over 2048, use fields, otherwise use description
		if (chars > 2048) {
			// Split into fields, avoiding 1024 field char limit
			ArrayList<String> fields = new ArrayList<String>();
			String fieldBuf = "";
			for (int i = 0; i < lines.size(); i++) {
				String fieldTemp = fieldBuf + lines.get(i) + "\n";
				if (fieldTemp.length() > 1024) {
					i -= 1; // The line goes over the char limit, don't include!
					fields.add(fieldBuf.substring(0, fieldBuf.length() - 1));
					fieldBuf = "";
				} else {
					fieldBuf = fieldTemp;
				}
				if (i == lines.size() - 1) {
					fields.add(fieldTemp);
				}
			}
			for (String field : fields) {
				eb.addField("Name History", field, false);
			}
		} else {
			eb.setDescription(String.join("\n", lines));
		}
		
		return new Result(Outcome.SUCCESS, MessageUtils.addFooter(eb).build());
	}

	private static int getTotalChars(ArrayList<String> lines) {
		int chars = 0;
		for (String line : lines) {
			chars += line.length() + 1; // +1 for newline
		}
		return chars;
	}
	
}
