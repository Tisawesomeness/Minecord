package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.database.dao.DbUser;
import com.tisawesomeness.minecord.util.DiscordUtils;

import net.dv8tion.jda.api.entities.User;

import java.sql.SQLException;

public class PromoteCommand extends Command {
	
	public CommandInfo getInfo() {
		return new CommandInfo(
			"promote",
			"Elevate a user.",
			"<user>",
			new String[]{
				"elevate",
				"rankup"},
			5000,
			true,
			true,
			true
		);
	}
	
	public Result run(CommandContext txt) {

		if (txt.args.length == 0) {
			return new Result(Outcome.WARNING, ":warning: You must specify a user!");
		}

		//Extract user
		User user = DiscordUtils.findUser(txt.args[0], txt.bot.getShardManager());
		if (user == null) return new Result(Outcome.ERROR, ":x: Not a valid user!");
		
		//Don't elevate a normal user
		DbUser dbUser = txt.getUser(user);
		if (dbUser.isElevated()) {
			return new Result(Outcome.WARNING, ":warning: User is already elevated!");
		}
		
		//Elevate user
		try {
			dbUser.withElevated(true).update();
		} catch (SQLException ex) {
			ex.printStackTrace();
			return new Result(Outcome.ERROR, ":x: There was an internal error.");
		}
		return new Result(Outcome.SUCCESS, ":arrow_up: Elevated " + user.getAsTag());
		
	}

}
