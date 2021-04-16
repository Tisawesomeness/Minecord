package com.tisawesomeness.minecord.command.core;

import com.tisawesomeness.minecord.Branding;
import com.tisawesomeness.minecord.command.meta.CommandContext;

import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;

public class InviteCommand extends AbstractCoreCommand {

    public @NonNull String getId() {
        return "invite";
    }

    public void run(String[] args, CommandContext ctx) {
        ctx.triggerCooldown();
        Branding branding = ctx.getBot().getBranding();
        EmbedBuilder eb = new EmbedBuilder()
                .addField("Invite me!", branding.getInvite(), false)
                .addField("Help server", branding.getHelpServer(), false)
                .addField("Website", branding.getWebsite(), true);
        ctx.reply(eb);
    }

}
