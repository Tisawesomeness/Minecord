package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.database.dao.DbGuild;
import com.tisawesomeness.minecord.database.dao.DbUser;
import com.tisawesomeness.minecord.util.DiscordUtils;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.sql.SQLException;

public class BanCommand extends Command {

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
				true,
			true,
			false
		);
	}

	public String getHelp() {
		return "`{&}ban <id>` - Check if a user or guild is banned.\n" +
			"`{&}ban <user|guild> <id>` - Ban a user or guild.\n" +
			"\n" +
			"`<id>` must be a valid user or guild id.\n" +
			"The user or guild does not have to be seen by Minecord.\n" +
			"Banned users and guilds will have all commands fail silently.\n";
	}

	public Result run(CommandContext ctx) {
		String[] args = ctx.args;
		
		//Check for proper argument length
		if (args.length < 1) return new Result(Outcome.WARNING, ":warning: Not enough arguments.");

		ShardManager sm = ctx.bot.getShardManager();
		
		//User part of command
		if ("user".equals(args[0])) {
			//Get user from message
			if (args.length == 1) return new Result(Outcome.WARNING, ":warning: Please define a user.");
            if (!DiscordUtils.isDiscordId(args[1])) {
                return new Result(Outcome.WARNING, ":warning: Not a valid ID!");
            }
			if (ctx.config.isOwner(args[1])) {
				return new Result(Outcome.WARNING, ":warning: You can't ban the owner!");
			}
			long uid = Long.valueOf(args[1]);
			DbUser dbUser = ctx.getUser(uid);
			//Ban or unban user
			boolean banned = dbUser.isBanned();
			try {
				dbUser.withBanned(!banned).update();
			} catch (SQLException ex) {
				ex.printStackTrace();
				return new Result(Outcome.ERROR, ":x: There was an internal error.");
			}
			//Format message
            User user = sm.retrieveUserById(args[1]).onErrorMap(ErrorResponse.UNKNOWN_USER::test, x -> null).complete();
			String msg = user == null ? args[1] : user.getAsTag();
			msg += banned ? " has been unbanned." : " was struck with the ban hammer!";
			return new Result(Outcome.SUCCESS, msg);
		
		//Guild part of command
		} else if ("guild".equals(args[0])) {
			//Get guild from message
			if (args.length == 1) return new Result(Outcome.WARNING, ":warning: Please define a guild.");
            if (!DiscordUtils.isDiscordId(args[1])) {
                return new Result(Outcome.WARNING, ":warning: Not a valid ID!");
            }
			Guild guild = sm.getGuildById(args[1]);
            long logChannelID = ctx.config.getLogChannelId();
			if (guild != null && logChannelID != 0) {
				TextChannel logChannel = sm.getTextChannelById(logChannelID);
				if (logChannel != null && guild.getId().equals(logChannel.getGuild().getId())) {
					return new Result(Outcome.WARNING, ":warning: You can't ban the guild with the log channel!");
				}
			}
			long gid = Long.valueOf(args[1]);
			DbGuild dbGuild = ctx.getGuild(gid);
			//Ban or unban guild
			boolean banned = dbGuild.isBanned();
			try {
				dbGuild.withBanned(!banned).update();
			} catch (SQLException ex) {
				ex.printStackTrace();
				return new Result(Outcome.ERROR, ":x: There was an internal error.");
			}
			//Format message
			String msg = guild.getName() + " (`" + guild.getId() + "`) ";
			msg += banned ? "has been unbanned." : "was struck with the ban hammer!";
			return new Result(Outcome.SUCCESS, msg);
		
		//Query part of command
		} else {
            if (!DiscordUtils.isDiscordId(args[0])) {
                return new Result(Outcome.WARNING, ":warning: Not a valid ID!");
            }
            long id = Long.valueOf(args[0]);
            boolean banned = ctx.getGuild(id).isBanned() || ctx.getUser(id).isBanned();
			String msg = args[0] + (banned ? " is banned!" : " is not banned.");
			return new Result(Outcome.SUCCESS, msg);
		}
		
	}

}
