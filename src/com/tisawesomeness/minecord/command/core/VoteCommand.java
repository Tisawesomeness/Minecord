package com.tisawesomeness.minecord.command.core;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.command.SlashCommand;
import com.tisawesomeness.minecord.util.MessageUtils;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;

public class VoteCommand extends SlashCommand {

    public CommandInfo getInfo() {
        return new CommandInfo(
                "vote",
                "Vote for the bot!",
                null,
                0,
                false,
                false
        );
    }

    @Override
    public String[] getLegacyAliases() {
        return new String[]{"v", "upvote", "updoot", "rep"};
    }

    public Result run(SlashCommandInteractionEvent e) {
        String m = "Top.gg: " + MarkdownUtil.maskedLink("VOTE", "https://top.gg/bot/292279711034245130/vote");
        String title = Config.isSelfHosted() ? "Vote for the main bot!" : "Vote for Minecord!";
        return new Result(Outcome.SUCCESS, MessageUtils.embedMessage(title, null, m, Bot.color));
    }

}
