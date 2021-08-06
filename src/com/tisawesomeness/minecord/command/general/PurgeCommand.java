package com.tisawesomeness.minecord.command.general;

import java.util.ArrayList;
import java.util.List;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.database.Database;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.RateLimitedException;

public class PurgeCommand extends Command {
	
	public CommandInfo getInfo() {
		return new CommandInfo(
			"purge",
			"Cleans the bot messages.",
			"<number>",
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

	public String getHelp() {
		return "`{&}purge <number>` - Cleans messages in the current channel **sent by the bot**.\n" +
			"The user must have *Manage Messages* permissions.\n" +
			"The number of messages must be between 1-1000.\n" +
			"If deleting more than 50 messages, the bot must have *Manage Messages* permissions.\n";
	}
	
	public Result run(String[] args, MessageReceivedEvent e) {

		// Guild-only command
		if (!e.isFromGuild()) {
			return new Result(Outcome.WARNING, ":warning: This command is not available in DMs.");
		}
		
		//Check if user is elevated or has the manage messages permission
		if (!Database.isElevated(e.getAuthor().getIdLong())
				&& !e.getMember().hasPermission(e.getTextChannel(), Permission.MESSAGE_MANAGE)) {
			return new Result(Outcome.WARNING, ":warning: You must have permission to manage messages in this channel!");
		}
		
		//Parse args
		int num = 50;
		boolean perms = false;
		if (args.length > 0 && args[0].matches("^[0-9]+$")) {
			num = Integer.valueOf(args[0]);
			
			//Check for bot permissions
			perms = e.getGuild().getSelfMember().hasPermission(e.getTextChannel(), Permission.MESSAGE_MANAGE);
			if (perms) {
				if (num <= 0 || num > 1000) {
					return new Result(Outcome.ERROR, ":x: The number must be between 1-1000.");
				}
			} else {
				if (num <= 0 || num > 50) {
					return new Result(Outcome.ERROR,
						":x: The number must be between 1-50, I don't have permission to manage messages!", 1.5);
				}
			}
			
		} else {
			return new Result(Outcome.WARNING, ":warning: Please specify a number!");
		}
		
		//Repeat until either the amount of messages are found or 100 non-bot messages in a row
		MessageHistory mh = new MessageHistory(e.getTextChannel());
		int empty = 0;
		ArrayList<Message> mine = new ArrayList<>();
		while (mine.size() < num) {
			try {

				//Fetch messages in batches of 25
				ArrayList<Message> temp = new ArrayList<>();
				List<Message> msgs = mh.retrievePast(25).complete(true);
				boolean exit = false;
				for (Message m : msgs) {
					if (m.getAuthor().getId().equals(e.getJDA().getSelfUser().getId())) {
						temp.add(m);
					}
					if (mine.size() + temp.size() >= num) {
						exit = true;
						break;
					}
				}
				
				mine.addAll(temp);
				if (exit) {break;}
				
				//If no messages were found, log it
				if (temp.size() > 0) {
					empty = 0;
				} else {
					empty++;
				}
				
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
		TextChannel c = e.getTextChannel();
		if (mine.size() == 1) {
			mine.get(0).delete().queue();
			c.sendMessage("1 message purged.").queue();
		} else if (!perms) {
			for (Message m : mine) {
				m.delete().queue();
			}
			c.sendMessage(mine.size() + " messages purged.").queue();
		} else {
			e.getTextChannel().deleteMessages(mine).queue();
			c.sendMessage(mine.size() + " messages purged.").queue();
		}
		
		return new Result(Outcome.SUCCESS);
	}
	
}
