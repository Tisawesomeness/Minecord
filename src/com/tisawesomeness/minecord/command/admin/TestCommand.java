package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.util.MessageUtils;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

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
		System.out.println(String.join(" ", MessageUtils.getContent(e.getMessage(), false)));
		System.out.println(String.join(" ", MessageUtils.getContent(e.getMessage(), true)));
		return new Result(Outcome.SUCCESS, "Test");
	}
	
}
