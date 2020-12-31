package com.tisawesomeness.minecord.command.misc;

import com.tisawesomeness.minecord.command.CommandContext;

import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.MarkdownUtil;

public class VoteCommand extends AbstractMiscCommand {

    public @NonNull String getId() {
        return "vote";
    }

    public void run(String[] args, CommandContext ctx) {
        ctx.triggerCooldown();
        String m = "Top.gg: " + MarkdownUtil.maskedLink("VOTE", "https://top.gg/bot/292279711034245130/vote");
        ctx.reply(new EmbedBuilder().setTitle("Vote for Minecord!").setDescription(m));
    }

}