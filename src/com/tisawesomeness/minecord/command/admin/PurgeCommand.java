package com.tisawesomeness.minecord.command.admin;

import java.util.ArrayList;
import java.util.List;

import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.util.MessageUtils;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.utils.PermissionUtil;

public class PurgeCommand extends Command {
	
	public CommandInfo getInfo() {
		return new CommandInfo(
			"purge",
			"Cleans the bot messages.",
			"[number]",
			new String[]{
				"clear",
				"clean",
				"delete",
				"delet",
				"prune",
				"destroy"},
			5000,
			false,
			false,
			true
		);
	}
	
	public Result run(String[] args, MessageReceivedEvent e) {
		
		//Check for bot permissions
		if (!e.getGuild().getSelfMember().hasPermission(e.getTextChannel(), Permission.MESSAGE_MANAGE)) {
			return new Result(Outcome.ERROR, ":x: I do not have permissions to manage messages!");
		}
		//Check if user is elevated or has the manage messages permission
		if (!Config.getElevatedUsers().contains(e.getAuthor())
				&& !PermissionUtil.checkPermission(e.getTextChannel(), e.getMember(), Permission.MESSAGE_MANAGE)) {
			return new Result(Outcome.WARNING, ":warning: You must have permission to manage messages in this channel!");
		}
		
		//Parse args
		int num = 50;
		if (args.length > 0 && args[0].matches("^[0-9]+$")) {
			num = Integer.valueOf(args[0]);
		} else {
			return new Result(Outcome.WARNING, ":warning: Please specify a number!");
		}
		
		//Repeat until either the amount of messages are found or 100 non-bot messages in a row
		MessageHistory mh = new MessageHistory(e.getTextChannel());
		int empty = 0;
		ArrayList<Message> mine = new ArrayList<>();
		while (mine.size() < num) {
			try {

				//Fetch messages in batches of 26
				ArrayList<Message> temp = new ArrayList<>();
				List<Message> msgs = mh.retrievePast(25).complete(true);
				for (Message m : msgs) {
					if (m.getAuthor() == e.getJDA().getSelfUser()) {
						temp.add(m);
					}
				}
				
				//If no messages were found, log it
				if (temp.size() > 0) {
					empty = 0;
				} else {
					empty++;
				}
				
				mine.addAll(temp);
				
			} catch (RateLimitedException ex) {
				ex.printStackTrace();
			}
			if (empty >= 4) {
				if (mine.size() == 0) {
					return new Result(Outcome.ERROR, "Could not find any bot messages within the last 100 messages.");
				}
				break;
			}
		}
		
		//Delete messages
		if (mine.size() == 1) {
			mine.get(0).delete().queue();
			MessageUtils.notify("1 message purged.", e.getTextChannel());
		} else {
			e.getTextChannel().deleteMessages(mine).queue();
			MessageUtils.notify(mine.size() + " messages purged.", e.getTextChannel());
		}
		
		return new Result(Outcome.SUCCESS, "");
	}
	
}
