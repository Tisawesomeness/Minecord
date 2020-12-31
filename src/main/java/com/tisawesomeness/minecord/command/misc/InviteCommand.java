package com.tisawesomeness.minecord.command.misc;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.CommandContext;

import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;

public class InviteCommand extends AbstractMiscCommand {

    public @NonNull String getId() {
        return "invite";
    }

    public void run(String[] args, CommandContext ctx) {
        ctx.triggerCooldown();
        EmbedBuilder eb = new EmbedBuilder()
                .addField("Invite me!", ctx.getConfig().getInviteLink(), false)
                .addField("Help server", Bot.helpServer, false)
                .addField("Website", Bot.website, true);
        ctx.reply(eb);
    }

}
