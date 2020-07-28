package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.CommandContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ShutdownCommand extends Command {
	
	public CommandInfo getInfo() {
		return new CommandInfo(
			"shutdown",
			"Shuts down the bot.",
			"[now?]",
			new String[]{"exit"},
                true,
			true,
			false
		);
	}

	public String getHelp() {
		return "Shuts down the bot. Note that the bot may reboot if it is run by a restart script.\n" +
				"Use `{&}shutdown now` to immediately exit.\n";
	}
	
	public Result run(CommandContext ctx) {

		if (ctx.args.length > 0 && "now".equals(ctx.args[0])) {
			System.exit(0);
			throw new AssertionError("System.exit() call failed.");
		}

		ctx.log(":x: **Bot shut down by " + ctx.e.getAuthor().getName() + "**");
		ctx.e.getChannel().sendMessage(":wave: Goodbye!").complete();
		ExecutorService exe = Executors.newSingleThreadExecutor();
		exe.submit(ctx.bot::shutdown);
		exe.shutdown();
		return new Result(Outcome.SUCCESS); // Graceful shutdown, just wait...

	}
	
}
