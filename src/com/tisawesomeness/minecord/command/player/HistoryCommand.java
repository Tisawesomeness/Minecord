package com.tisawesomeness.minecord.command.player;

import java.awt.Color;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONObject;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.database.Database;
import com.tisawesomeness.minecord.util.DateUtils;
import com.tisawesomeness.minecord.util.MessageUtils;
import com.tisawesomeness.minecord.util.NameUtils;
import com.tisawesomeness.minecord.util.RequestUtils;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class HistoryCommand extends Command {
	
	public CommandInfo getInfo() {
		return new CommandInfo(
			"history",
			"Gets the name history of a player.",
			"<username|uuid> [date]",
			null,
			2000,
			false,
			false,
			true
		);
	}
	
	public Result run(String[] args, MessageReceivedEvent e) {
		long id = e.getGuild().getIdLong();
		
		//No arguments message
		if (args.length == 0) {
			String m = ":warning: Incorrect arguments." +
				"\n" + Database.getPrefix(id) + "history <username|uuid> [date] " +
				"\n" + MessageUtils.dateHelp;
			return new Result(Outcome.WARNING, m, 5);
		}

		String player = args[0];	
		if (!player.matches(NameUtils.uuidRegex)) {
			String uuid = null;
			
			//Parse date argument
			if (args.length > 1) {
				long timestamp = DateUtils.getTimestamp(Arrays.copyOfRange(args, 1, args.length));
				if (timestamp == -1) {
					return new Result(Outcome.WARNING, MessageUtils.dateErrorString(id, "history"));
				}
				
			//Get the UUID
				uuid = NameUtils.getUUID(player, timestamp);
			} else {
				uuid = NameUtils.getUUID(player);
			}
			
			//Check for errors
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

		//Fetch name history
		String url = "https://api.mojang.com/user/profiles/" + player.replaceAll("-", "") + "/names";
		String request = RequestUtils.get(url);
		if (request == null) {
			return new Result(Outcome.ERROR, ":x: The Mojang API could not be reached.");
		}
		
		//Loop over each name change
		String m = "";
		JSONArray names = new JSONArray(request);
		for (int i = 0; i < names.length(); i++) {
			
			//Get info
			JSONObject change = names.getJSONObject(i);
			String name = change.getString("name");
			String date;
			if (change.has("changedToAt")) {
				date = DateUtils.getString(change.getLong("changedToAt"));
			} else {
				date = "Original";
			}
			
			//Add to lines
			m = name + " | " + date + "\n" + m;
		}
		m = m.substring(0, m.length() - 1);
		
		player = names.getJSONObject(names.length() - 1).getString("name");
		
		//Get NameMC url
		url = "https://namemc.com/profile/" + player;
		
		//PROPER APOSTROPHE GRAMMAR THANK THE LORD
		if (player.endsWith("s")) {
			player = player + "' Name History";
		} else {
			player = player + "'s Name History";
		}
		
		MessageEmbed me = MessageUtils.embedMessage(player, url, m, Color.GREEN);
		
		return new Result(Outcome.SUCCESS, new EmbedBuilder(me).build());
	}
	
}
