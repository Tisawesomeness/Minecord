package com.tisawesomeness.minecord.command.admin;

import java.io.IOException;
import java.sql.SQLException;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.database.Database;
import com.tisawesomeness.minecord.database.VoteHandler;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

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
			Message m = e.getChannel().sendMessage(":arrows_counterclockwise: Reloading...").complete();
			Config.read(true);
			try {
				Database.close();
				Database.init();
				VoteHandler.close();
				VoteHandler.init();
			} catch (SQLException | IOException ex) {
				ex.printStackTrace();
			}
			m.editMessage(":white_check_mark: Reloaded!").queue();
		}
		
		return new Result(Outcome.SUCCESS);
	}
	
}
