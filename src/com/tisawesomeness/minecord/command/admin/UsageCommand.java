package com.tisawesomeness.minecord.command.admin;

import java.awt.Color;

import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.Registry;
import com.tisawesomeness.minecord.util.DateUtils;
import com.tisawesomeness.minecord.util.MessageUtils;

import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

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
		for (Command c : Registry.commands) {
			CommandInfo ci = c.getInfo();
			if (ci.name == "") {continue;}
			
			//Build message
			m = m + "`" + Config.getPrefix() + ci.name + "` **-** " + c.uses + "\n";
		}
		m = m.substring(0, m.length() - 1);

		String title = "Command usage for " + DateUtils.getUptime();
		MessageEmbed me = MessageUtils.embedMessage(title, null, m, Color.GREEN);
		return new Result(Outcome.SUCCESS, me);
	}
	
}
