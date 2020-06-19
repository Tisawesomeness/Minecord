package com.tisawesomeness.minecord.command.admin;

import java.io.IOException;
import java.sql.SQLException;

import com.tisawesomeness.minecord.ArgsHandler;
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
		return "Reloads the config, announcement, and item/recipe files, and restarts the database and vote server.";
	}

	public Result run(CommandContext txt) {
		Bot bot = txt.bot;

		Message m = txt.e.getChannel().sendMessage(":arrows_counterclockwise: Reloading...").complete();
		try {
			Database.close();
			Database.start();
			if (txt.config.shouldReceiveVotes()) {
				bot.getVoteHandler().close();
			}
			ArgsHandler args = bot.getArgs();
			Config reloadedConfig = txt.bot.reloadConfig();
			if (reloadedConfig.shouldReceiveVotes()) {
				bot.getVoteHandler().start();
			}
			txt.bot.reloadAnnouncements(reloadedConfig);
			Item.init();
			Recipe.init();
		} catch (SQLException | IOException ex) {
			ex.printStackTrace();
		}
		m.editMessage(":white_check_mark: Reloaded!").queue();
		
		return new Result(Outcome.SUCCESS);
	}
	
}
