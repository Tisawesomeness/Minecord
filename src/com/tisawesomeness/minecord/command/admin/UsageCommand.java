package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.Registry;
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
		String title = "";
		for (Command c : Registry.commands) {
			CommandInfo ci = c.getInfo();
			if (ci.name == "") {continue;}
			
			//Calculate uptime
			long uptime = System.currentTimeMillis() - Bot.birth;
			long hours = uptime / 3600000;
			long rate;
			if (hours >= 1) {
				title = "Command Uses Per Hour";
				rate = Math.round(1000 * (c.uses / hours)) / 1000;
			} else {
				int minutes = (int) Math.floor(uptime / 60000);
				String plural = "s";
				if (minutes == 1) {
					plural = "";
				}
				title = "Command Uses For " + minutes + " Minute" + plural;
				rate = c.uses;
			}
			
			//Build message
			m = m + "`" + Config.getPrefix() + ci.name + "` **-** " + rate + "\n";
		}
		m = m.substring(0, m.length() - 1);

		MessageEmbed me = MessageUtils.wrapMessage(title, null, m, MessageUtils.randomColor());
		return new Result(Outcome.SUCCESS, me);
	}
	
}
