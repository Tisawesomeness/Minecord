package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.command.Command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

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
	
	public Result run(String[] args, MessageReceivedEvent e) throws Exception {
		return new Result(Outcome.SUCCESS, "Test");
	}
	
}
