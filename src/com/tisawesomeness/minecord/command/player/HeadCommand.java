package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.util.ArrayUtils;
import com.tisawesomeness.minecord.util.MessageUtils;
import com.tisawesomeness.minecord.util.NameUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class HeadCommand extends Command {
	
	public CommandInfo getInfo() {
		return new CommandInfo(
			"head",
			"Gets the head of a player.",
			"<username|uuid> [overlay?]",
			null,
			2000,
			false,
			false,
			true
		);
	}

	public String getHelp() {
		return "`{&}head <player> [overlay?]` - Gets an image of the player's head.\n" +
			"- `<player>` can be a username or a UUID.\n" +
			"- `[overlay?]` whether to include the second skin layer.\n" +
			"\n" +
			"Examples:\n" +
			"`{&}head Tis_awesomeness`\n" +
			"`{&}head jeb_`\n" +
			"`{&}head f6489b797a9f49e2980e265a05dbc3af`\n" +
			"`{&}head 069a79f4-44e9-4726-a5be-fca90e38aaf5 overlay`\n";
	}
	
	public Result run(String[] argsOrig, MessageReceivedEvent e) {
		//No arguments message
		if (argsOrig.length == 0) {
			return new Result(Outcome.WARNING, ":warning: You must specify a player.");
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
		if (!NameUtils.isUuid(player)) {
			String uuid = NameUtils.getUUID(player);
			
			//Check for errors
			if (uuid == null) {
				String m = ":x: The Mojang API could not be reached." +
					"\n" + "Are you sure that username exists?" +
					"\n" + "Usernames are case-sensitive.";
				return new Result(Outcome.WARNING, m);
			} else if (!NameUtils.isUuid(player)) {
				String m = ":x: The API responded with an error:\n" + uuid;
				return new Result(Outcome.ERROR, m);
			}
			
			param = uuid;
		}

		//Fetch head
		String url = "https://crafatar.com/renders/head/" + param.replace("-", "") + ".png";
		if (overlay) url += "?overlay";
		return new Result(Outcome.SUCCESS, new EmbedBuilder().setImage(url).setColor(Bot.color).build());
	}
	
}
