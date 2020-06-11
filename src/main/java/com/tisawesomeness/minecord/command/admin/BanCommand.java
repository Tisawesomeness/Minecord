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
import net.dv8tion.jda.api.requests.ErrorResponse;

public class BanCommand extends Command {

	@Override
	public CommandInfo getInfo() {
		return new CommandInfo(
			"ban",
			"Bans/unbans a user/guild from the bot. Omit user/guild to check for a ban.",
			"[user|guild] <id>",
			new String[]{
				"bean",
				"banne",
				"pingb1nzy",
				"strike",
				"smite"
			},
			0,
			true,
			true,
			false
		);
	}

	public String getHelp() {
		return "`{&}ban <id>` - Check if a user or guild is banned.\n" +
			"`{&}ban <user|guild> <id>` - Ban a user or guild.\n" +
			"\n" +
			"`<id>` must be an 18-digit id.\n" +
			"The user or guild does not have to be seen by Minecord.\n" +
			"Banned users and guilds will have all commands fail silently.\n";
	}

	@Override
	public Result run(String[] args, MessageReceivedEvent e) throws Exception {
		
		//Check for proper argument length
		if (args.length < 1) return new Result(Outcome.WARNING, ":warning: Not enough arguments.");
		
		//User part of command
		if ("user".equals(args[0])) {
			//Get user from message
			if (args.length == 1) return new Result(Outcome.WARNING, ":warning: Please define a user.");
            if (!args[1].matches(DiscordUtils.idRegex)) {
                return new Result(Outcome.WARNING, ":warning: Not a valid ID!");
            }
			if (args[1].equals(Config.getOwner())) {
				return new Result(Outcome.WARNING, ":warning: You can't ban the owner!");
			}
			long gid = Long.valueOf(args[1]);
			//Ban or unban user
			boolean banned = Database.isBanned(gid);
			Database.changeBannedUser(gid, !banned);
			//Format message
            User user = Bot.shardManager.retrieveUserById(args[1]).onErrorMap(ErrorResponse.UNKNOWN_USER::test, x -> null).complete();
			String msg = user == null ? args[1] : user.getAsTag();
			msg += banned ? " has been unbanned." : " was struck with the ban hammer!";
			return new Result(Outcome.SUCCESS, msg);
		
		//Guild part of command
		} else if ("guild".equals(args[0])) {
			//Get guild from message
			if (args.length == 1) return new Result(Outcome.WARNING, ":warning: Please define a guild.");
            if (!args[1].matches(DiscordUtils.idRegex)) {
                return new Result(Outcome.WARNING, ":warning: Not a valid ID!");
            }
			Guild guild = Bot.shardManager.getGuildById(args[1]);
			if (guild != null && !Config.getLogChannel().equals("0") && guild.getId().equals(MessageUtils.logChannel.getGuild().getId())) {
				return new Result(Outcome.WARNING, ":warning: You can't ban the guild with the log channel!");
			}
			long gid = Long.valueOf(args[1]);
			//Ban or unban guild
			boolean banned = Database.isBanned(gid);
			Database.changeBannedGuild(gid, !banned);
			//Format message
			String msg = guild.getName() + " (`" + guild.getId() + "`) ";
			msg += banned ? "has been unbanned." : "was struck with the ban hammer!";
			return new Result(Outcome.SUCCESS, msg);
		
		//Query part of command
		} else {
            if (!args[0].matches(DiscordUtils.idRegex)) {
                return new Result(Outcome.WARNING, ":warning: Not a valid ID!");
            }
			String msg = args[0] + (Database.isBanned(Long.valueOf(args[0])) ? " is banned!" : " is not banned.");
			return new Result(Outcome.SUCCESS, msg);
		}
		
	}

}
