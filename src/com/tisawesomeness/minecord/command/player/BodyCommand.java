package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.util.ArrayUtils;
import com.tisawesomeness.minecord.util.MessageUtils;
import com.tisawesomeness.minecord.util.NameUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class BodyCommand extends Command {
	
	public CommandInfo getInfo() {
		return new CommandInfo(
			"body",
			"Gets the body of a player.",
			"<username|uuid> [overlay?]",
			new String[]{
				"nude",
				"nudes"},
			2000,
			false,
			false,
			true
		);
	}

	public String getHelp() {
		return "`{&}body <player> [overlay?]` - Gets an image of the player's body.\n" +
			"- `<player>` can be a username or a UUID.\n" +
			"- `[overlay?]` whether to include the second skin layer.\n" +
			"\n" +
			"Examples:\n" +
			"`{&}body Tis_awesomeness`\n" +
			"`{&}body jeb_`\n" +
			"`{&}body f6489b797a9f49e2980e265a05dbc3af`\n" +
			"`{&}body 069a79f4-44e9-4726-a5be-fca90e38aaf5 overlay`\n";
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
			} else if (!NameUtils.isUuid(uuid)) {
				String m = ":x: The API responded with an error:\n" + uuid;
				return new Result(Outcome.ERROR, m);
			}
			
			param = uuid;
		}

		//Fetch body
		String url = "https://crafatar.com/renders/body/" + param.replace("-", "");
		if (overlay) url += "?overlay";
		return new Result(Outcome.SUCCESS, new EmbedBuilder().setImage(url).setColor(Bot.color).build());
	}
	
}
