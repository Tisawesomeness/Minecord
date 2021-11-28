package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.util.NameUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SkinCommand extends Command {
	
	public CommandInfo getInfo() {
		return new CommandInfo(
			"skin",
			"Gets the skin of a player.",
			"<username|uuid>",
			null,
			2000,
			false,
			false,
			true
		);
	}

	public String getHelp() {
		return "`{&}skin <player> [overlay?]` - Gets an image of the player's skin.\n" +
			"- `<player>` can be a username or a UUID.\n" +
			"\n" +
			"Examples:\n" +
			"`{&}skin Tis_awesomeness`\n" +
			"`{&}skin jeb_`\n" +
			"`{&}skin f6489b797a9f49e2980e265a05dbc3af`\n" +
			"`{&}skin 069a79f4-44e9-4726-a5be-fca90e38aaf5`\n";
	}
	
	public Result run(String[] args, MessageReceivedEvent e) {
		//No arguments message
		if (args.length == 0) {
			return new Result(Outcome.WARNING, ":warning: You must specify a player.");
		}

		String player = args[0];
		String param = player;
		if (!player.matches(NameUtils.uuidRegex)) {
			String uuid = NameUtils.getUUID(player);
			
			//Check for errors
			if (uuid == null) {
				String m = ":x: The Mojang API could not be reached." +
					"\n" + "Are you sure that username exists?" +
					"\n" + "Usernames are case-sensitive.";
				return new Result(Outcome.WARNING, m);
			} else if (!uuid.matches(NameUtils.uuidRegex)) {
				String m = ":x: The API responded with an error:\n" + uuid;
				return new Result(Outcome.ERROR, m);
			}
			
			param = uuid;
		}

		//Fetch skin
		String url = "https://crafatar.com/skins/" + param.replaceAll("-", "");
		return new Result(Outcome.SUCCESS, new EmbedBuilder().setImage(url).setColor(Bot.color).build());
	}
	
}
