package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.command.CommandContext;

import lombok.NonNull;

public class TestCommand extends AbstractAdminCommand {

	public @NonNull String getId() {
		return "test";
	}
	public CommandInfo getInfo() {
		return new CommandInfo(
                true,
				true,
				true
		);
	}
	
	public Result run(CommandContext ctx) {
		return new Result(Outcome.SUCCESS, "Test");
	}
	
}
