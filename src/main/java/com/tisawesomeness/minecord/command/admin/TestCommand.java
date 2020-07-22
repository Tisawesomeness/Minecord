package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.CommandContext;

public class TestCommand extends Command {
	
	public CommandInfo getInfo() {
		return new CommandInfo(
			"test",
			"Test command.",
			null,
			null,
			5000,
			true,
			true,
			true
		);
	}
	
	public Result run(CommandContext ctx) {
		return new Result(Outcome.SUCCESS, "Test");
	}
	
}
