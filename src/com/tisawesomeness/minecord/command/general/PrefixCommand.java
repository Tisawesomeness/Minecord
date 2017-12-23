package com.tisawesomeness.minecord.command.general;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.database.Database;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.utils.PermissionUtil;

public class PrefixCommand extends Command {
	
	public CommandInfo getInfo() {
		return new CommandInfo(
			"prefix",
			"Change the prefix.",
			"[prefix]",
			new String[]{
				"resetprefix",
				"changeprefix"},
			5000,
			false,
			false,
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
			
			//Print current prefix
			return new Result(Outcome.SUCCESS,
				"The current prefix is `" + Database.getPrefix(e.getGuild().getIdLong()) + "`"
			);
			
		} else {
			
			//No prefixes longer than 16 characters
			if (args[0].length() > 16) {
				return new Result(Outcome.WARNING, ":warning: The prefix you specified is too long!");
			}
			//Easter egg for those naughty bois
			if (args[0].equals("'") && args[1].equals("OR") && args[2].equals("1=1")) {
				return new Result(Outcome.SUCCESS, "Nice try.");
			}
			//Set new prefix
			Database.changePrefix(e.getGuild().getIdLong(), args[0]);
			return new Result(Outcome.SUCCESS, ":white_check_mark: Prefix changed to `" + args[0] + "`");
			
		}
	}

}
