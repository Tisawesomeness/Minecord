package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.command.CommandContext;

import lombok.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ShutdownCommand extends AbstractAdminCommand {

	public @NonNull String getId() {
		return "shutdown";
	}
	public CommandInfo getInfo() {
		return new CommandInfo(
                true,
				true,
				false
		);
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
