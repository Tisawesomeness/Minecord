package com.tisawesomeness.minecord.command.misc;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.CommandContext;

import net.dv8tion.jda.api.utils.MarkdownUtil;

public class VoteCommand extends Command {

    public CommandInfo getInfo() {
		return new CommandInfo(
			"vote",
			"Vote for the bot!",
			null,
			new String[]{"v", "upvote", "updoot", "rep"},
			0,
			false,
			false,
			false
		);
    }

    public Result run(CommandContext txt) {
        String m = "Top.gg: " + MarkdownUtil.maskedLink("VOTE", "https://top.gg/bot/292279711034245130/vote");
        return new Result(Outcome.SUCCESS, txt.embedMessage("Vote for Minecord!", m).build());
    }

}