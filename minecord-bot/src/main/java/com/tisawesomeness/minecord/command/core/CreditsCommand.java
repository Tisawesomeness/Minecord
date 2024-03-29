package com.tisawesomeness.minecord.command.core;

import com.tisawesomeness.minecord.BotBranding;
import com.tisawesomeness.minecord.command.meta.CommandContext;

import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.MarkdownUtil;

public class CreditsCommand extends AbstractCoreCommand {

    public @NonNull String getId() {
        return "credits";
    }

    private final String DEVS = MarkdownUtil.maskedLink("Tis_awesomeness#8617", "https://github.com/Tisawesomeness") + " - Main Dev\n" +
            MarkdownUtil.maskedLink("jbs#6969", "https://github.com/lordjbs") + " - Developer\n" +
            MarkdownUtil.maskedLink("samueldcs#4675", "https://github.com/samueldcs") + " - Made the Website\n" +
            MarkdownUtil.maskedLink("DJ Electro#1677", "https://github.com/Electromaster232") + " - Supplied Hosting";
    
    private final String APIS = "Discord API Wrapper - " + MarkdownUtil.maskedLink("JDA", "https://github.com/DV8FromTheWorld/JDA") + "\n" +
            "MC Account Info - " +
                    MarkdownUtil.maskedLink("Mojang API", "https://wiki.vg/Mojang_API") + ", " +
                    MarkdownUtil.maskedLink("Electroid API", "https://github.com/Electroid/mojang-api") + ", " +
                    MarkdownUtil.maskedLink("Gapple API", "https://api.gapple.pw/") + "\n" +
            "Skin Renders - " + MarkdownUtil.maskedLink("Crafatar", "https://crafatar.com") + "\n" +
            "Server Pinging - " + MarkdownUtil.maskedLink("MCServerPing", "https://github.com/lucaazalim/minecraft-server-ping") + "\n" +
            "Optifine Capes - " + MarkdownUtil.maskedLink("Optifine", "https://optifine.net");

    private final String HOST = "The public bot is proudly hosted by " + MarkdownUtil.maskedLink("Endless Hosting", "https://theendlessweb.com/");

    public void run(String[] args, CommandContext ctx) {
        ctx.triggerCooldown();

        BotBranding branding = ctx.getBot().getBranding();
        String contrib = MarkdownUtil.maskedLink("Contribute on GitHub", branding.getGithub()) + "\n" +
                MarkdownUtil.maskedLink("Suggest features here", branding.getHelpServer());

        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("Minecord Credits")
                .setColor(branding.getColor())
                .setDescription("Thanks to all these great people who helped make the bot possible. :heart:")
                .addField("Developers", DEVS, true)
                .addField("Contribute", contrib, true)
                .addField("APIs Used", APIS, false);
        if (ctx.getConfig().isSelfHosted()) {
            String selfHost = "This bot is self-hosted by **" + branding.getAuthor() + "**\n" +
                    "Original Website - " + MarkdownUtil.maskedLink(BotBranding.WEBSITE, BotBranding.WEBSITE) + "\n" +
                    "Original Help Server - " + MarkdownUtil.maskedLink(BotBranding.HELP_SERVER, BotBranding.HELP_SERVER) + "\n" +
                    "Original Github - " + MarkdownUtil.maskedLink("Source Code", BotBranding.GITHUB);
            eb.addField("Self-Hosting", selfHost, false);
        } else {
            eb.addField("Hosting", HOST, false);
        }
        String userMention = ctx.getE().getAuthor().getAsMention();
        eb.addField("Special Thanks", userMention + " for using Minecord!", false)
                .setFooter("Website - " + branding.getWebsite());

        ctx.replyRaw(eb);
    }

}