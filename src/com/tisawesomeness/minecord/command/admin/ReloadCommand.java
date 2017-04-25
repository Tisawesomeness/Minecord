package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.command.Command;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class ReloadCommand extends Command {
	
	public CommandInfo getInfo() {
		return new CommandInfo(
			"reload",
			"Reloads the bot.",
			null,
			new String[]{
				"restart",
				"reboot",
				"refresh"},
			0,
			true,
			true,
			false
		);
	}
	
	public Result run(String[] args, MessageReceivedEvent e) {
		
		//Check for dev mode
		if (!Config.getDevMode()) {
			return new Result(Outcome.ERROR, "You can only reload code in dev mode!");
		}
		
		//Shut down bot
		Message m = e.getChannel().sendMessage(":arrows_counterclockwise: Reloading...").complete();
		Bot.shutdown(m, e.getAuthor());
		
		return new Result(Outcome.SUCCESS, "");
	}
	
}
