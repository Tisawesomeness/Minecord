package com.tisawesomeness.minecord.command.player;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.util.DateUtils;
import com.tisawesomeness.minecord.util.MessageUtils;
import com.tisawesomeness.minecord.util.NameUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class HeadCommand extends Command {
	
	public CommandInfo getInfo() {
		return new CommandInfo(
			"head",
			"Gets the head render of a player.",
			"<username|uuid> [date] [overlay?]",
			null,
			2000,
			false,
			false,
			true
		);
	}
	
	public Result run(String[] argsOrig, MessageReceivedEvent e) {
		String prefix = MessageUtils.getPrefix(e);
		
		//No arguments message
		if (argsOrig.length == 0) {
			String m = ":warning: Incorrect arguments." +
				"\n" + prefix + "head <username|uuid> [date] [overlay?]" +
				"\n" + MessageUtils.dateHelp;
			return new Result(Outcome.WARNING, m, 5);
		}
		String[] args = argsOrig;
		
		//Check for overlay argument
		boolean overlay = false;
		int index = MessageUtils.parseBoolean(args, "overlay");
		if (index != -1) {
			overlay = true;
			args = ArrayUtils.remove(args, index);
		}

		String player = args[0];	
		String param = player;
		if (!player.matches(NameUtils.uuidRegex)) {
			String uuid = null;
			
			//Parse date argument
			if (args.length > 1) {
				long timestamp = DateUtils.getTimestamp(Arrays.copyOfRange(args, 1, args.length));
				if (timestamp == -1) {
					return new Result(Outcome.WARNING, MessageUtils.dateErrorString(prefix, "head"));
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
			
			param = uuid;
		}

		//Fetch head
		String url = "https://crafatar.com/renders/head/" + param.replaceAll("-", "") + ".png";
		if (overlay) url += "?overlay";
		return new Result(Outcome.SUCCESS, new EmbedBuilder().setImage(url).setColor(Bot.color).build());
	}
	
}
