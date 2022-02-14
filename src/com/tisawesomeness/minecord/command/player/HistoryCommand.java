package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.util.MessageUtils;
import com.tisawesomeness.minecord.util.NameUtils;
import com.tisawesomeness.minecord.util.RequestUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.TimeFormat;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class HistoryCommand extends Command {
	
	public CommandInfo getInfo() {
		return new CommandInfo(
			"history",
			"Gets the name history of a player.",
			"<username|uuid>",
			new String[]{"h"},
			2000,
			false,
			false,
			true
		);
	}

	public String getHelp() {
		return "`{&}history <player>` - Gets a player's name history.\n" +
			"- `<player>` can be a username or a UUID.\n" +
			"\n" +
			"Examples:\n" +
			"`{&}history Tis_awesomeness`\n" +
			"`{&}history jeb_`\n" +
			"`{&}history f6489b797a9f49e2980e265a05dbc3af`\n" +
			"`{&}history 069a79f4-44e9-4726-a5be-fca90e38aaf5`\n";
	}
	
	public Result run(String[] args, MessageReceivedEvent e) {

		// No arguments message
		if (args.length == 0) {
			return new Result(Outcome.WARNING, ":warning: You must specify a player.");
		}

		String player = args[0];	
		if (!NameUtils.isUuid(player)) {
			if (!NameUtils.isUsername(player)) {
				return new Result(Outcome.WARNING, ":warning: That username is invalid.");
			}

			String uuid = NameUtils.getUUID(player);
			
			// Check for errors
			if (uuid == null) {
				String m = ":x: The Mojang API could not be reached." +
					"\n" + "Are you sure that username exists?" +
					"\n" + "Usernames are case-sensitive.";
				return new Result(Outcome.WARNING, m);
			} else if (!NameUtils.isUuid(uuid)) {
				String m = ":x: The API responded with an error:\n" + uuid;
				return new Result(Outcome.ERROR, m);
			}
			
			player = uuid;
		}

		// Fetch name history
		String url = "https://api.mojang.com/user/profiles/" + player.replace("-", "") + "/names";
		String request = RequestUtils.get(url);
		if (request == null) {
			return new Result(Outcome.ERROR, ":x: The Mojang API could not be reached.");
		}
		
		// Loop over each name change
		JSONArray names = new JSONArray(request);
		ArrayList<String> lines = new ArrayList<>();
		for (int i = 0; i < names.length(); i++) {
			
			// Get info
			JSONObject change = names.getJSONObject(i);
			String name = change.getString("name");
			String date;
			if (change.has("changedToAt")) {
				date = TimeFormat.RELATIVE.format(change.getLong("changedToAt"));
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
		int chars = MessageUtils.getTotalChars(lines);
		boolean truncated = false;
		while (chars > 6000 - 4) {
			truncated = true;
			lines.remove(lines.size() - 1);
			chars = MessageUtils.getTotalChars(lines);
		}
		if (truncated) {
			lines.add("...");
		}
		// If over 2048, use fields, otherwise use description
		if (chars > 2048) {
			// Split into fields, avoiding 1024 field char limit
			for (String field : MessageUtils.splitLinesByLength(lines, 1024)) {
				eb.addField("Name History", field, false);
			}
		} else {
			eb.setDescription(String.join("\n", lines));
		}
		
		return new Result(Outcome.SUCCESS, MessageUtils.addFooter(eb).build());
	}
	
}
