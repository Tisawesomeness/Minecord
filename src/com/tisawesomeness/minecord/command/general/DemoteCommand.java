package com.tisawesomeness.minecord.command.general;

import org.apache.commons.lang3.ArrayUtils;

import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.database.Database;
import com.tisawesomeness.minecord.util.MessageUtils;

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

}
