package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.CommandContext;

import net.dv8tion.jda.api.entities.Message;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

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

	public Result run(CommandContext ctx) {

		Message m = ctx.e.getChannel().sendMessage(":arrows_counterclockwise: Reloading...").complete();
		try {
			ctx.bot.reload();
		} catch (IOException | ExecutionException | InterruptedException ex) {
			ex.printStackTrace();
			return new Result(Outcome.ERROR, ":x: Could not reload!");
		}
		m.editMessage(":white_check_mark: Reloaded!").queue();
		
		return new Result(Outcome.SUCCESS);

	}
	
}
