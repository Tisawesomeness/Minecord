package com.tisawesomeness.minecord.command.misc;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.util.MessageUtils;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
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

    public Result run(String[] args, MessageReceivedEvent e) {
        String m = "Top.gg: " + MarkdownUtil.maskedLink("VOTE", "https://top.gg/bot/292279711034245130/vote");
        String title = Config.isIsSelfHosted() ? "Vote for the main bot!" : "Vote for Minecord!";
        return new Result(Outcome.SUCCESS, MessageUtils.embedMessage(title, null, m, Bot.color));
    }

}