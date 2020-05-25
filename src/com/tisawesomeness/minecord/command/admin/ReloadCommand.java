package com.tisawesomeness.minecord.command.admin;

import java.io.IOException;
import java.sql.SQLException;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.database.Database;
import com.tisawesomeness.minecord.database.VoteHandler;
import com.tisawesomeness.minecord.item.Item;
import com.tisawesomeness.minecord.item.Recipe;

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
			try {
				Database.close();
				Database.init();
				if (Config.getReceiveVotes()) {
					VoteHandler.close();
				}
				Config.read(true);
				if (Config.getReceiveVotes()) {
					VoteHandler.init();
				}
				Item.init(Config.getPath());
				Recipe.init(Config.getPath());
			} catch (SQLException | IOException ex) {
				ex.printStackTrace();
			}
			m.editMessage(":white_check_mark: Reloaded!").queue();
		}
		
		return new Result(Outcome.SUCCESS);
	}
	
}
