package com.tisawesomeness.minecord.command.admin;

import java.io.IOException;
import java.sql.SQLException;

import com.tisawesomeness.minecord.Announcement;
import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.database.Database;
import com.tisawesomeness.minecord.item.Item;
import com.tisawesomeness.minecord.item.Recipe;

import net.dv8tion.jda.api.entities.Message;

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

	public String getHelp() {
		if (Config.getDevMode()) {
			return "Reloads all non-reflection code, keeping the JDA instance.\n";
		}
		return "Reloads the config, announcement, and item/recipe files, and restarts the database and vote server.";
	}

	public Result run(CommandContext txt) {
		Bot bot = txt.bot;
		
		if (Config.getDevMode()) {
			Message m = txt.e.getChannel().sendMessage(":arrows_counterclockwise: Reloading...").complete();
			bot.shutdown(m, txt.e.getAuthor());
		} else {
			Message m = txt.e.getChannel().sendMessage(":arrows_counterclockwise: Reloading...").complete();
			try {
				Database.close();
				Database.start();
				if (Config.getReceiveVotes()) {
					bot.getVoteHandler().close();
				}
				Config.read(bot, true);
				if (Config.getReceiveVotes()) {
					bot.getVoteHandler().start();
				}
				Announcement.init(Config.getPath());
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
