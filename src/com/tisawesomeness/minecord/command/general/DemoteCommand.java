package com.tisawesomeness.minecord.command.general;

import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.database.Database;
import com.tisawesomeness.minecord.util.DiscordUtils;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.utils.PermissionUtil;

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
	
	public Result run(String[] args, MessageReceivedEvent e) throws Exception {
		
		//Check if user is elevated or has the manage messages permission
		if (!Database.isElevated(e.getAuthor().getIdLong())
				&& !PermissionUtil.checkPermission(e.getTextChannel(), e.getMember(), Permission.MESSAGE_MANAGE)) {
			return new Result(Outcome.WARNING, ":warning: You must have permission to manage messages in this channel!");
		}

		//Extract user
		User user = DiscordUtils.findUser(args[0]);
		if (user == null) return new Result(Outcome.ERROR, ":x: Not a valid user!");
		long id = user.getIdLong();
		
		//Don't demote a normal user
		if (!Database.isElevated(id)) {
			return new Result(Outcome.WARNING, ":warning: User is not elevated!");
		}
		
		//Can't demote the owner
		if (id == Long.valueOf(Config.getOwner())) {
			return new Result(Outcome.WARNING, ":warning: You can't demote the owner!");
		}
		
		//Demote user
		Database.changeElevated(id, false);
		return new Result(Outcome.SUCCESS,
			":arrow_down: Demoted " + user.getName() + "#" + user.getDiscriminator()
		);
		
	}

}
