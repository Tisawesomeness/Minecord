package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.util.MessageUtils;
import com.tisawesomeness.minecord.util.NameUtils;
import com.tisawesomeness.minecord.util.RequestUtils;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CapeCommand extends Command {

	public CommandInfo getInfo() {
		return new CommandInfo(
			"cape",
			"Gets the cape of a player.",
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
				"\n" + Config.getPrefix() + "cape <username|uuid> [date]" +
				"\n" + "In [date], you may define a date, time, and timezone." +
				"\n" + "Date Examples:" +
				"\n" + "`9/25`" +
				" | " + "`2/29/2012`" +
				" | " + "`5/15 8:30`" +
				" | " + "`3/2/06 2:47:32`" +
				" | " + "`9:00 PM`" +
				" | " + "`12/25/12 12:00 AM EST`" +
				" | " + "`5:22 CST`";
			return new Result(Outcome.WARNING, m, 5);
		}

		//Get playername
		String player = args[0];	
		if (player.matches(NameUtils.uuidRegex)) {
			player = NameUtils.getName(player);
			
			//Check for errors
			if (player == null) {
				String m = ":x: The Mojang API could not be reached." +
					"\n" + "Are you sure that UUID exists?";
				return new Result(Outcome.ERROR, m, 1.5);
			} else if (!player.matches(NameUtils.playerRegex)) {
				String m = ":x: The API responded with an error:\n" + player;
				return new Result(Outcome.ERROR, m, 3);
			}
		}
		String user = player;

		//Fetch cape
		String minecraftUrl = "https://crafatar.com/capes/" + player;
		boolean minecraftCape = RequestUtils.checkURL(minecraftUrl);
		String optifineUrl = "http://s.optifine.net/capes/" + player + ".png";
		boolean optifineCape = RequestUtils.checkURL(optifineUrl);
		
		//PROPER APOSTROPHE GRAMMAR THANK THE LORD
		player = args[0];
		if (player.endsWith("s")) {
			player = player + "' Cape";
		} else {
			player = player + "'s Cape";
		}
		
		//Logic to decide message
		String url;
		if (minecraftCape && optifineCape) {
			e.getTextChannel().sendMessage(MessageUtils.wrapImage(
				player, minecraftUrl, MessageUtils.randomColor())).queue();
			url = optifineUrl;
		} else if (minecraftCape) {
			url = minecraftUrl;
		} else if (optifineCape) {
			url = optifineUrl;
		} else {
			return new Result(Outcome.WARNING, ":warning: " + user + " does not have a cape!");
		}
		
		MessageEmbed me = MessageUtils.wrapImage(player, url, MessageUtils.randomColor());
		
		return new Result(Outcome.SUCCESS, new EmbedBuilder(me).build());
	}
	
}
