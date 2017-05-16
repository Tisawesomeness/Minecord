package com.tisawesomeness.minecord.command.player;

import java.util.Arrays;

import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.util.DateUtils;
import com.tisawesomeness.minecord.util.MessageUtils;
import com.tisawesomeness.minecord.util.NameUtils;
import com.tisawesomeness.minecord.util.RequestUtils;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class SkinCommand extends Command {
	
	public CommandInfo getInfo() {
		return new CommandInfo(
			"skin",
			"Gets the skin of a player.",
			"<username|uuid> [date]",
			null,
			2000,
			false,
			false,
			true
		);
	}
	
	public Result run(String[] args, MessageReceivedEvent e) {
		
		//No arguments message
		if (args.length == 0) {
			String m = ":warning: Incorrect arguments." +
				"\n" + Config.getPrefix() + "skin <username|uuid> [date]" +
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
					String m = ":x: Improperly formatted date. " +
						"At least a date or time is required. " +
						"Do `" + Config.getPrefix() + "skin` for more info.";
					return new Result(Outcome.WARNING, m);
				}
				
			//Get the UUID
				uuid = NameUtils.getUUID(player, timestamp);
			} else {
				uuid = NameUtils.getUUID(player);
			}
			
			//Check for errors
			if (uuid == null) {
				String m = ":x: The Mojang API could not be reached." +
					"\n" + "Are you sure that username exists?";
				return new Result(Outcome.WARNING, m, 1.5);
			} else if (!uuid.matches(NameUtils.uuidRegex)) {
				String m = ":x: The API responded with an error:\n" + uuid;
				return new Result(Outcome.ERROR, m, 3);
			}
			
			player = uuid;
		}

		//Fetch skin
		String url = "https://crafatar.com/skins/" + player + ".png";
		url = RequestUtils.checkPngExtension(url);
		if (url == null) {
			MessageUtils.log("Error embedding image." +
				"\n" + "Command: `" + Config.getPrefix() + "skin`" +
				"\n" + "UUID: `" + player + "`"
			);
			return new Result(Outcome.ERROR, ":x: There was an error embedding the image.");
		}
		
		//PROPER APOSTROPHE GRAMMAR THANK THE LORD
		player = args[0];
		if (player.endsWith("s")) {
			player = player + "' Skin";
		} else {
			player = player + "'s Skin";
		}
		
		MessageEmbed me = MessageUtils.embedImage(player, url, MessageUtils.randomColor());
		
		return new Result(Outcome.SUCCESS, new EmbedBuilder(me).build());
	}
	
}
