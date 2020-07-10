package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.database.DbUser;
import com.tisawesomeness.minecord.util.DiscordUtils;

import net.dv8tion.jda.api.entities.User;

import java.sql.SQLException;

public class DemoteCommand extends Command {
	
	public CommandInfo getInfo() {
		return new CommandInfo(
			"demote",
			"De-elevate a user.",
			"<user>",
			new String[]{
				"delevate",
				"normie",
				"badboi"},
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
		long id = user.getIdLong();

		//Don't demote a normal user
		DbUser dbUser = txt.getUser(id);
		if (!dbUser.isElevated()) {
			return new Result(Outcome.WARNING, ":warning: User is not elevated!");
		}
		
		//Can't demote the owner
		if (id == Long.valueOf(txt.config.owner)) {
			return new Result(Outcome.WARNING, ":warning: You can't demote the owner!");
		}
		
		//Demote user
		try {
			dbUser.withElevated(false).update();
		} catch (SQLException ex) {
			ex.printStackTrace();
			return new Result(Outcome.ERROR, ":x: There was an internal error.");
		}
		return new Result(Outcome.SUCCESS, ":arrow_down: Demoted " + user.getAsTag());
		
	}

}
