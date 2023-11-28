package com.tisawesomeness.minecord.command.core;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.command.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;

public class CreditsCommand extends SlashCommand {

    public CommandInfo getInfo() {
        return new CommandInfo(
                "credits",
                "See who made the bot possible.",
                null,
                0,
                false,
                false
        );
    }

    public static String[] legacyAliases() {
        return new String[]{"thanks", "thx"};
    }
    @Override
    public String[] getLegacyAliases() {
        return legacyAliases();
    }

    private static final String devs = MarkdownUtil.maskedLink("tis_awesomeness", "https://github.com/Tisawesomeness") + " - Main Dev\n" +
            MarkdownUtil.maskedLink("jbs", "https://github.com/lordjbs") + " - Developer\n" +
            MarkdownUtil.maskedLink("samueldcs", "https://github.com/samueldcs") + " - Made the Website\n" +
            MarkdownUtil.maskedLink("DJ Electro", "https://github.com/Electromaster232") + " - Supplied Hosting";

    private static final String apis = "Discord API Wrapper - " + MarkdownUtil.maskedLink("JDA", "https://github.com/DV8FromTheWorld/JDA") + "\n" +
            "MC Account Info - " +
            MarkdownUtil.maskedLink("Mojang API", "https://wiki.vg/Mojang_API") + ", " +
            MarkdownUtil.maskedLink("Electroid API", "https://github.com/Electroid/mojang-api") + ", " +
            MarkdownUtil.maskedLink("Gapple API", "https://api.gapple.pw/") + "\n" +
            "Skin Renders - " + MarkdownUtil.maskedLink("Crafatar", "https://crafatar.com") + "\n" +
            "Server Pinging - " + MarkdownUtil.maskedLink("MCServerPing", "https://github.com/lucaazalim/minecraft-server-ping") + "\n" +
            "Custom Capes - " + MarkdownUtil.maskedLink("Optifine", "https://optifine.net");

    private static final String host = "The public bot is proudly hosted by " + MarkdownUtil.maskedLink("Endless Hosting", "https://theendlessweb.com/")+ ".\n";

    public Result run(SlashCommandInteractionEvent e) {
        return run(e.getUser());
    }
    public static Result run(User user) {
        String contrib = MarkdownUtil.maskedLink("Contribute on GitHub", Config.getGithub()) + "\n" +
                MarkdownUtil.maskedLink("Suggest features here", Config.getHelpServer());

        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("Minecord Credits")
                .setColor(Bot.color)
                .setDescription("Thanks to all these great people who helped to make the bot possible. :heart:")
                .addField("Developers", devs, true)
                .addField("Contribute", contrib, true)
                .addField("APIs Used", apis, false);
        if (Config.isSelfHosted()) {
            String selfHost = "This bot is self-hosted by **" + Config.getAuthor() + "**\n" +
                    "Original Website - " + Bot.website + "\n" +
                    "Original Help Server - " + Bot.helpServer + "\n" +
                    "Original Github - " + MarkdownUtil.maskedLink("Source Code", Bot.github);
            eb.addField("Self-Hosting", selfHost, false);
        } else {
            eb.addField("Hosting", host, false);
        }
        eb.addField("Special Thanks", user.getAsMention() + " for using Minecord!", false)
                .setFooter("Website: " + Config.getWebsite());
        return new Result(Outcome.SUCCESS, eb.build());
    }

}
