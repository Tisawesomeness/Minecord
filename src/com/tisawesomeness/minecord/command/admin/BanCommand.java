package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.database.Database;
import com.tisawesomeness.minecord.util.DiscordUtils;
import com.tisawesomeness.minecord.util.MessageUtils;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class BanCommand extends Command {

	@Override
	public CommandInfo getInfo() {
		return new CommandInfo(
			"ban",
			"Bans a user or guild from using the bot. Omit user or guild to check for a ban.",
			"<user|guild> <id>",
			new String[]{
				"bean",
				"banne",
				"bann�",
				"pingb1nzy"
			},
			0,
			true,
			true,
			false
		);
	}

	@Override
	public Result run(String[] args, MessageReceivedEvent e) throws Exception {
		
		//Check for proper argument length
		if (args.length < 1) return new Result(Outcome.WARNING, ":warning: Not enough arguments.");
		
		//User part of command
		if ("user".equals(args[0])) {
			//Get user from message
			if (args.length == 1) return new Result(Outcome.WARNING, ":warning: Please define a user.");
			User user = DiscordUtils.findUser(args[1]);
			if (user == null) return new Result(Outcome.ERROR, ":x: Not a valid user!");
			if (user.getId().equals(Config.getOwner())) {
				return new Result(Outcome.WARNING, ":warning: You can't ban the owner!");
			}
			//Ban or unban user
			boolean banned = Database.isBanned(user.getIdLong());
			Database.changeBannedUser(user.getIdLong(), !banned);
			//Format message
			String msg = user.getName() + "#" + user.getDiscriminator() + " (`" + user.getId() + "`) ";
			msg += banned ? "has been unbanned." : "was struck with the ban hammer!";
			return new Result(Outcome.SUCCESS, msg);
		
		//Guild part of command
		} else if ("guild".equals(args[0])) {
			//Get guild from message
			if (args.length == 1) return new Result(Outcome.WARNING, ":warning: Please define a guild.");
			Guild guild = Bot.shardManager.getGuildById(args[1]);
			if (guild == null) return new Result(Outcome.ERROR, ":x: Not a valid guild!");
			if (guild.getId().equals(MessageUtils.logChannel.getGuild().getId())) {
				return new Result(Outcome.WARNING, ":warning: You can't ban the guild with the log channel!");
			}
			//Ban or unban guild
			boolean banned = Database.isBanned(guild.getIdLong());
			Database.changeBannedGuild(guild.getIdLong(), !banned);
			//Format message
			String msg = guild.getName() + " (`" + guild.getId() + "`) ";
			msg += banned ? "has been unbanned." : "was struck with the ban hammer!";
			return new Result(Outcome.SUCCESS, msg);
		
		//Query part of command
		} else {
			long id = 0;
			String name = null;
			User user = DiscordUtils.findUser(args[0]);
			if (user == null) {
				Guild guild = Bot.shardManager.getGuildById(args[0]);
				if (guild == null) return new Result(Outcome.ERROR, ":x: Not a valid user or guild!");
				id = guild.getIdLong();
				name = guild.getName();
			} else {
				id = user.getIdLong();
				name = user.getName() + "#" + user.getDiscriminator();
			}
			String msg = name + " (`" + id + "`) ";
			msg += Database.isBanned(id) ? "is banned!" : "is not banned.";
			return new Result(Outcome.SUCCESS, msg);
			
		}
		
	}

}
