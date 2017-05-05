package com.tisawesomeness.minecord.command.general;

import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.util.MessageUtils;
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
		return new Result(Outcome.SUCCESS,
			MessageUtils.embedMessage("Invite me!", null, Config.getInvite(), MessageUtils.randomColor())
		);
	}
	
}
