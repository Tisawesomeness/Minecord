package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.CommandContext;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class ShutdownCommand extends Command {
	
	public CommandInfo getInfo() {
		return new CommandInfo(
			"shutdown",
			"Shuts down the bot.",
			null,
			null,
			0,
			true,
			true,
			false
		);
	}

	public String getHelp() {
		return "Shuts down the bot. Note that the bot may reboot if it is run by a restart script.\n";
	}
	
	public Result run(CommandContext txt) {
		txt.log(":x: **Bot shut down by " + txt.e.getAuthor().getName() + "**");
		txt.e.getChannel().sendMessage(":wave: Goodbye!").complete();
		try {
			// This thread (not the new one created below) should be interrupted by the shutdown
			Executors.newSingleThreadExecutor().submit(() -> txt.bot.shutdown(0)).get();
		} catch (InterruptedException | ExecutionException ex) {
			ex.printStackTrace();
		}
		throw new AssertionError("Bot failed to shut down.");
	}
	
}
