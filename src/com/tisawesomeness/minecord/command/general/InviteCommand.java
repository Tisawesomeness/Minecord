package com.tisawesomeness.minecord.command.general;

import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.util.MessageUtils;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class InviteCommand extends Command {
	
	public CommandInfo getInfo() {
		return new CommandInfo(
			"invite",
			"Invite the bot!",
			null,
			null,
			2000,
			false,
			false,
			true
		);
	}
	
	public Result run(String[] args, MessageReceivedEvent e) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.addField("Invite me!", Config.getInvite(), true);
		eb.addField("Help server", InfoCommand.helpServer, true);
		eb = MessageUtils.addFooter(eb);
		return new Result(Outcome.SUCCESS, eb.build());
	}
	
}
