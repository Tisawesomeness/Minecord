package com.tisawesomeness.minecord.command.player;

import java.io.IOException;
import java.util.Arrays;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.database.Database;
import com.tisawesomeness.minecord.util.DateUtils;
import com.tisawesomeness.minecord.util.MessageUtils;
import com.tisawesomeness.minecord.util.NameUtils;
import com.tisawesomeness.minecord.util.RequestUtils;

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
		long id = e.getGuild().getIdLong();
		
		//No arguments message
		if (args.length == 0) {
			String m = ":warning: Incorrect arguments." +
				"\n" + Database.getPrefix(id) + "cape <username|uuid> [date]" +
				"\n" + MessageUtils.dateHelp;
			return new Result(Outcome.WARNING, m, 5);
		}

		//Get playername
		String player = args[0];
		String uuid = player;
		if (player.matches(NameUtils.uuidRegex)) {
			player = NameUtils.getName(player);
			
			//Check for errors
			if (player == null) {
				String m = ":x: The Mojang API could not be reached." +
					"\n" + "Are you sure that UUID exists?";
				return new Result(Outcome.WARNING, m, 1.5);
			} else if (!player.matches(NameUtils.playerRegex)) {
				String m = ":x: The API responded with an error:\n" + player;
				return new Result(Outcome.ERROR, m, 3);
			}
		} else {
			//Parse date argument
			if (args.length > 1) {
				long timestamp = DateUtils.getTimestamp(Arrays.copyOfRange(args, 1, args.length));
				if (timestamp == -1) {
					return new Result(Outcome.WARNING, MessageUtils.dateErrorString(id, "skin"));
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
		}

		//Fetch capes
		boolean mc = false;
		boolean of = false;
		try {
			e.getTextChannel().sendFile(RequestUtils.downloadImage("https://crafatar.com/capes/" + uuid), "cape.png").queue();
		} catch (IOException ex) {
			mc = true;
		}
		try {
			e.getTextChannel().sendFile(RequestUtils.downloadImage("http://s.optifine.net/capes/" + player + ".png"), "cape.png").queue();
		} catch (IOException ex) {
			of = true;
		}
		
		if (mc && of) return new Result(Outcome.WARNING, ":warning: " + player + " does not have a cape!");
		return new Result(Outcome.SUCCESS);
	}
	
}
