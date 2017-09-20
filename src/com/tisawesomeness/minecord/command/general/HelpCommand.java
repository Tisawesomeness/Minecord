package com.tisawesomeness.minecord.command.general;

import java.awt.Color;

import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.Registry;
import com.tisawesomeness.minecord.util.MessageUtils;

import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class HelpCommand extends Command {
	
	public CommandInfo getInfo() {
		return new CommandInfo(
			"help",
			"Displays this help menu.",
			null,
			new String[]{
				"commands",
				"cmds"},
			0,
			false,
			false,
			true
		);
	}
	
	public Result run(String[] args, MessageReceivedEvent e) {
		String m = "";
		
		//If the author used the admin keyword and is an elevated user
		boolean elevated = false;
		if (args.length > 0 && args[0].equals("admin") && Config.getElevatedUsers().contains(e.getAuthor().getId())) {
			elevated = true;
		}
		
		//Iterate through every registered command
		for (Command c : Registry.commands) {
			if (c != null) {
				CommandInfo ci = c.getInfo();
				if (!ci.hidden || elevated) {
					//Fetch basic info
					String name = ci.name;
					String description = ci.description;
					
					//Add text objects
					if ("".equals(name)) {
						if (description != null && (!ci.elevated || elevated)) {
							m += description + "\n";
						}
						continue;
					}
					
					//Fetch info and build message line
					String usage = ci.usage;
					String gap = "";
					if (!"".equals(usage)) {
						gap = " ";
					}
					m += "`" + Config.getPrefix() + name + gap + usage + "` **-** " + description + "\n";
				}
			}
		}
		m = m.substring(0, m.length() - 1); //Remove trailing newline
		
		MessageEmbed me = MessageUtils.embedMessage(null, null, m, Color.GREEN);
		
		return new Result(Outcome.SUCCESS, me);
	}

}
