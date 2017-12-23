package com.tisawesomeness.minecord.command.general;

import org.apache.commons.lang3.ArrayUtils;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.database.Database;
import com.tisawesomeness.minecord.util.MessageUtils;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.utils.PermissionUtil;

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
	
	public Result run(String[] args, MessageReceivedEvent e) throws Exception {
		
		//Check if user is elevated or has the manage messages permission
		if (!Database.isElevated(e.getAuthor().getIdLong())
				&& !PermissionUtil.checkPermission(e.getTextChannel(), e.getMember(), Permission.MESSAGE_MANAGE)) {
			return new Result(Outcome.WARNING, ":warning: You must have permission to manage messages in this channel!");
		}

		if (args.length == 0) {
			return new Result(Outcome.WARNING, ":warning: Please specify a user.");
		} else {
			
			//Extract user
			args = ArrayUtils.remove(MessageUtils.getContent(e.getMessage(), true, e.getGuild().getIdLong()), 0);
			User user = null;
			if (args[0].matches(MessageUtils.mentionRegex)) {
				user = e.getMessage().getMentionedUsers().get(0);
				if (user.getId() == e.getJDA().getSelfUser().getId()) {
					user = e.getMessage().getMentionedUsers().get(1);
				}
			} else if (args[0].matches(MessageUtils.idRegex)) {
				user = e.getJDA().getUserById(args[0]);
				if (user == null) return new Result(Outcome.ERROR, ":x: Not a valid user!");
			} else {
				return new Result(Outcome.ERROR, ":x: Not a valid user!");
			}
			
			//Don't elevate a normal user
			if (Database.isElevated(user.getIdLong())) {
				return new Result(Outcome.WARNING, ":warning: User is already elevated!");
			}
			
			//Elevate user
			Database.changeElevated(user.getIdLong(), true);
			return new Result(Outcome.SUCCESS,
				":arrow_up: Elevated " + user.getName() + "#" + user.getDiscriminator()
			);
			
		}
	}

}
