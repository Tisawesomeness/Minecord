package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.util.DateUtils;
import com.tisawesomeness.minecord.util.MessageUtils;
import com.tisawesomeness.minecord.util.NameUtils;

import java.util.Arrays;

public class UuidCommand extends Command {
	
	public CommandInfo getInfo() {
		return new CommandInfo(
			"uuid",
			"Gets the UUID of a player.",
			"<username> [date]",
			new String[]{"u"},
                false,
			false,
			true
		);
	}

	public String getHelp() {
		return "`{&}uuid <player> [date]` - Gets a player's short and long UUID.\n" +
			"\n" +
			"- `<player>` can be a username or a UUID.\n" +
			"- " + DateUtils.dateHelp + "\n" +
			"\n" +
			"Examples:\n" +
			"`{&}uuid Tis_awesomeness`\n" +
			"`{&}uuid Notch 3/2/06 2:47:32`\n" +
			"`{&}uuid f6489b797a9f49e2980e265a05dbc3af`\n" +
			"`{&}uuid 069a79f4-44e9-4726-a5be-fca90e38aaf5 3/26`\n";
	}
	
	public Result run(CommandContext ctx) {
		
		// No arguments message
		if (ctx.args.length == 0) {
			return new Result(Outcome.WARNING, ":warning: You must specify a player.");
		}
		
		String username = ctx.args[0];
		String uuid = null;
		
		// Parse date argument
		if (ctx.args.length > 1) {
			long timestamp = DateUtils.getTimestamp(Arrays.copyOfRange(ctx.args, 1, ctx.args.length));
			if (timestamp == -1) {
				return new Result(Outcome.WARNING, MessageUtils.dateErrorString(ctx.prefix, "uuid"));
			}
			
		// Get the UUID
			uuid = NameUtils.getUUID(username, timestamp);
		} else {
			uuid = NameUtils.getUUID(username);
		}
		
		// Check for errors
		if (uuid == null) {
			String m = ":x: The Mojang API could not be reached." +
				"\n" + "Are you sure that username exists?" +
				"\n" + "Usernames are case-sensitive.";
			return new Result(Outcome.WARNING, m);
		} else if (!uuid.matches(NameUtils.uuidRegex)) {
			String m = ":x: The API responded with an error:\n" + uuid;
			return new Result(Outcome.ERROR, m);
		}
		
		// Get NameMC url
		String url = "https://namemc.com/profile/" + uuid;
		
		// Proper apostrophe grammar
		String title = username;
		if (title.endsWith("s")) {
			title = title + "' UUID";
		} else {
			title = title + "'s UUID";
		}
		
		String m = String.format("Short: `%s`\nLong: `%s`", uuid, NameUtils.formatUUID(uuid));
		
		return new Result(Outcome.SUCCESS, ctx.embedURL(title, url, m).build());
	}
	
}
