package com.tisawesomeness.minecord.command.misc;

import com.tisawesomeness.minecord.Branding;
import com.tisawesomeness.minecord.command.CommandContext;

import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.MarkdownUtil;

public class CreditsCommand extends AbstractMiscCommand {

    public @NonNull String getId() {
        return "credits";
    }

    private final String DEVS = MarkdownUtil.maskedLink("Tis_awesomeness#8617", "https://github.com/Tisawesomeness") + " - Main Dev\n" +
        MarkdownUtil.maskedLink("jbs#6969", "https://github.com/lordjbs") + " - Developer\n" +
        MarkdownUtil.maskedLink("samueldcs#4675", "https://github.com/samueldcs") + " - Made the Website\n" +
        MarkdownUtil.maskedLink("DJ Electro#1677", "https://github.com/Electromaster232") + " - Helped with Git once";
    
    private final String APIS = "Discord API Wrapper - " + MarkdownUtil.maskedLink("JDA", "https://github.com/DV8FromTheWorld/JDA") + "\n" +
        "MC Account Info - " + MarkdownUtil.maskedLink("Mojang API", "https://wiki.vg/Mojang_API") + "\n" +
        "Skin Renders - " + MarkdownUtil.maskedLink("Crafatar", "https://crafatar.com") + "\n" +
        "Server Pinging - " + MarkdownUtil.maskedLink("MCServerPing", "https://github.com/lucaazalim/minecraft-server-ping") + "\n" +
        "Cape Sites - " +
            MarkdownUtil.maskedLink("Optifine", "https://optifine.net") + ", " +
            MarkdownUtil.maskedLink("LabyMod", "https://www.labymod.net") + ", " +
            MarkdownUtil.maskedLink("MinecraftCapes", "https://www.minecraftcapes.co.uk");

    public void run(String[] args, CommandContext ctx) {
        ctx.triggerCooldown();

        Branding branding = ctx.getBot().getBranding();
        String contrib = MarkdownUtil.maskedLink("Contribute on GitHub", branding.getGithub()) + "\n" +
                MarkdownUtil.maskedLink("Suggest features here", branding.getHelpServer());

        EmbedBuilder eb = new EmbedBuilder()
            .setTitle("Minecord Credits")
            .setColor(branding.getColor())
            .setDescription("Thanks to all these great people who helped to make the bot possible. :heart:")
            .addField("Developers", DEVS, true)
            .addField("Contribute", contrib, true)
            .addField("APIs Used", APIS, false);
        if (ctx.getConfig().isSelfHosted()) {
            String selfHost = "This bot is self-hosted by **" + branding.getAuthor() + "**\n" +
                    "Original Website - " + MarkdownUtil.maskedLink(Branding.WEBSITE, Branding.WEBSITE) + "\n" +
                    "Original Help Server - " + MarkdownUtil.maskedLink(Branding.HELP_SERVER, Branding.HELP_SERVER) + "\n" +
                    "Original Github - " + MarkdownUtil.maskedLink("Source Code", Branding.GITHUB);
            eb.addField("Self-Hosting", selfHost, false);
        }
        eb.setFooter("Website: " + branding.getWebsite());

        ctx.replyRaw(eb);
    }

}