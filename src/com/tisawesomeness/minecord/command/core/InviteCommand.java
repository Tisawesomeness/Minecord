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
                0,
                false,
                false
        );
    }

    public Result run(SlashCommandInteractionEvent e) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Invite Minecord");
        String links = MarkdownUtil.maskedLink("INVITE", Config.getInvite()) +
                " | " + MarkdownUtil.maskedLink("SUPPORT", Config.getHelpServer()) +
                " | " + MarkdownUtil.maskedLink("WEBSITE", Config.getWebsite());
        eb.setDescription(links);
        eb.setColor(Bot.color);
        eb = MessageUtils.addFooter(eb);
        return new Result(Outcome.SUCCESS, eb.build());
    }

}
