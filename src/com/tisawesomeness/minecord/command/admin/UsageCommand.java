package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.Module;
import com.tisawesomeness.minecord.command.Registry;
import com.tisawesomeness.minecord.util.DateUtils;
import com.tisawesomeness.minecord.util.MessageUtils;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class UsageCommand extends Command {
	
	public CommandInfo getInfo() {
		return new CommandInfo(
			"usage",
			"Shows how often commands are used.",
			"",
			new String[0],
			0,
			true,
			true,
			false
		);
	}
	
	public Result run(String[] args, MessageReceivedEvent e) {
		
		//Iterate over commands
		String m = "";
		for (Module mod : Registry.modules) {
			for (Command c : mod.getCommands()) {
				CommandInfo ci = c.getInfo();
				if ("".equals(ci.name)) {continue;}
				
				//Build message
				m += "`" + MessageUtils.getPrefix(e) + ci.name + "` **-** " + c.uses + "\n";
			}
		}
		m = m.substring(0, m.length() - 1);

		String title = "Command usage for " + DateUtils.getUptime();
		MessageEmbed me = MessageUtils.embedMessage(title, null, m, Bot.color);
		return new Result(Outcome.SUCCESS, me);
	}
	
}
