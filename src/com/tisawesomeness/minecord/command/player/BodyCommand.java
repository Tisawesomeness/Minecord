package com.tisawesomeness.minecord.command.player;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;

import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.util.DateUtils;
import com.tisawesomeness.minecord.util.MessageUtils;
import com.tisawesomeness.minecord.util.NameUtils;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class BodyCommand extends Command {
	
	public CommandInfo getInfo() {
		return new CommandInfo(
			"body",
			"Gets the body render of a player.",
			"<username|uuid> [date] [overlay?]",
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
				"\n" + Config.getPrefix() + "body <username|uuid> [date]" +
				"\n" + MessageUtils.dateHelp;
			return new Result(Outcome.WARNING, m, 5);
		}
		
		//Check for overlay argument
		boolean overlay = false;
		int index = MessageUtils.parseBoolean(args, "overlay");
		if (index != -1) {
			overlay = true;
			args = ArrayUtils.remove(args, index);
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
						"Do `" + Config.getPrefix() + "body` for more info.";
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

		//Fetch body
		String url = "https://crafatar.com/renders/body/" + player + ".png";
		if (overlay) {url = url + "?overlay";}
		
		//PROPER APOSTROPHE GRAMMAR THANK THE LORD
		player = args[0];
		if (player.endsWith("s")) {
			player = player + "' Body";
		} else {
			player = player + "'s Body";
		}
		
		MessageEmbed me = MessageUtils.embedImage(player, url, MessageUtils.randomColor());
		
		return new Result(Outcome.SUCCESS, new EmbedBuilder(me).build());
	}
	
}
