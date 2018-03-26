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

public class SkinCommand extends Command {
	
	public final String steve = "http://taw.net/cfs-filesystemfile.ashx/__key/CommunityServer.Discussions.Components.Files/200/7607.reference-skin-_2D00_-large.png";
	public final String alex = "https://articles-images.sftcdn.net/wp-content/uploads/sites/2/2014/09/Alex1.png";
	
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
		long id = e.getGuild().getIdLong();
		
		//No arguments message
		if (args.length == 0) {
			String m = ":warning: Incorrect arguments." +
				"\n" + Database.getPrefix(id) + "skin <username|uuid> [date]" +
				"\n" + MessageUtils.dateHelp;
			return new Result(Outcome.WARNING, m, 5);
		}

		String player = args[0];
		String param = player;
		if (!player.matches(NameUtils.uuidRegex)) {
			String uuid = null;
			
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
			
			param = uuid;
		}

		//Fetch skin
		String url = "https://crafatar.com/skins/" + param.replaceAll("-", "");
		try {
			e.getTextChannel().sendFile(RequestUtils.downloadImage(url), "skin.png").queue();
		} catch (IOException ex) {
			ex.printStackTrace();
			return new Result(Outcome.ERROR, ":x: Could not download image.");
		}
		return new Result(Outcome.SUCCESS);
	}
	
}
