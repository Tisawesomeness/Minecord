package com.tisawesomeness.minecord.command.misc;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.CommandContext;

import net.dv8tion.jda.api.EmbedBuilder;

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
	
	public Result run(CommandContext ctx) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.addField("Invite me!", ctx.config.getInviteLink(), false);
		eb.addField("Help server", Bot.helpServer, false);
		eb.addField("Website", Bot.website, true);
		eb = ctx.brand(eb);
		return new Result(Outcome.SUCCESS, eb.build());
	}
	
}
