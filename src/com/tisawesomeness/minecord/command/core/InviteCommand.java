package com.tisawesomeness.minecord.command.core;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.command.SlashCommand;
import com.tisawesomeness.minecord.util.MessageUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;

public class InviteCommand extends SlashCommand {

    public CommandInfo getInfo() {
        return new CommandInfo(
                "invite",
                "Invite the bot!",
                null,
                2000,
                false,
                false
        );
    }

    public Result run(SlashCommandInteractionEvent e) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.addField("Invite me!", MarkdownUtil.maskedLink(Config.getInvite(), Config.getInvite()), false);
        eb.addField("Help server", MarkdownUtil.maskedLink(Config.getHelpServer(), Config.getHelpServer()), false);
        eb.addField("Website", MarkdownUtil.maskedLink(Config.getWebsite(), Config.getWebsite()), true);
        eb.setColor(Bot.color);
        eb = MessageUtils.addFooter(eb);
        return new Result(Outcome.SUCCESS, eb.build());
    }

}
