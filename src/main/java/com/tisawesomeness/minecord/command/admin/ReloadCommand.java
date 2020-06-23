package com.tisawesomeness.minecord.command.admin;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.CommandContext;

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
		return "Reloads the config, announcement, and item/recipe files, and restarts the database and vote server.\n" +
				"If there is an exception, shut down the bot with {&}shutdown or do a hard reset.";
	}

	public Result run(CommandContext txt) {

		Message m = txt.e.getChannel().sendMessage(":arrows_counterclockwise: Reloading...").complete();
		try {
			txt.bot.reload();
		} catch (SQLException | IOException | ExecutionException | InterruptedException ex) {
			ex.printStackTrace();
			try {
				m.editMessage(":x: Could not reload! Shutting down...").complete();
			} catch (Exception ex2) {
				ex2.printStackTrace(); // Bot must shut down regardless of exceptions
			}
			txt.bot.shutdown(1);
			throw new AssertionError("Bot failed to shut down.");
		}
		m.editMessage(":white_check_mark: Reloaded!").queue();
		
		return new Result(Outcome.SUCCESS);

	}
	
}
