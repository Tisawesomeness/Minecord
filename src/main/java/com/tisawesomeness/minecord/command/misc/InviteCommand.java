package com.tisawesomeness.minecord.command.misc;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.CommandContext;

import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;

public class InviteCommand extends AbstractMiscCommand {

	public @NonNull String getId() {
		return "invite";
	}
	public CommandInfo getInfo() {
		return new CommandInfo(
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
