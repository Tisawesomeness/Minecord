package com.tisawesomeness.minecord.command.misc;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Branding;
import com.tisawesomeness.minecord.command.CommandContext;

import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;

public class InviteCommand extends AbstractMiscCommand {

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
