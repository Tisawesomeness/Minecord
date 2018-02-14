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
		
		if (Config.getDevMode()) {
			Message m = e.getChannel().sendMessage(":arrows_counterclockwise: Reloading...").complete();
			Bot.shutdown(m, e.getAuthor());
		} else {
			e.getChannel().sendMessage(":white_check_mark: Reloaded config.").queue();
			Config.read(true);
		}
		
		return new Result(Outcome.SUCCESS);
	}
	
}
