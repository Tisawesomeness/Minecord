package com.tisawesomeness.minecord.command.misc;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.command.Command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;

public class CreditsCommand extends Command {

    public CommandInfo getInfo() {
		return new CommandInfo(
			"credits",
			"See who made the bot possible.",
			null,
			new String[]{"thanks", "thx"},
			0,
			false,
			false,
			false
		);
    }

    private String devs = MarkdownUtil.maskedLink("Tis_awesomeness#8617", "https://github.com/Tisawesomeness") + " - Main Dev\n" +
        MarkdownUtil.maskedLink("jbs#6969", "https://github.com/lordjbs") + " - Developer\n" +
        MarkdownUtil.maskedLink("samueldcs#4675", "https://github.com/samueldcs") + " - Made the Website\n" +
        MarkdownUtil.maskedLink("DJ Electro#1677", "https://github.com/Electromaster232") + " - Supplied Hosting";
    
    private String apis = "Discord API Wrapper - " + MarkdownUtil.maskedLink("JDA", "https://github.com/DV8FromTheWorld/JDA") + "\n" +
        "MC Account Info - " + MarkdownUtil.maskedLink("Mojang API", "https://wiki.vg/Mojang_API") + "\n" +
        "Skin Renders - " + MarkdownUtil.maskedLink("Crafatar", "https://crafatar.com") + "\n" +
        "Server Pinging - " + MarkdownUtil.maskedLink("MCServerPing", "https://github.com/lucaazalim/minecraft-server-ping") + "\n" +
        "Custom Capes - " + MarkdownUtil.maskedLink("Optifine", "https://optifine.net");
    
    private String host = "The public bot is proudly hosted by " + MarkdownUtil.maskedLink("Endless Hosting", "https://theendlessweb.com/")+ ".\n";

    public Result run(String[] args, MessageReceivedEvent e) throws Exception {
        String contrib = MarkdownUtil.maskedLink("Contribute on GitHub", Config.getGithub()) + "\n" +
                MarkdownUtil.maskedLink("Suggest features here", Config.getHelpServer());

        EmbedBuilder eb = new EmbedBuilder()
            .setTitle("Minecord Credits")
            .setColor(Bot.color)
            .setDescription("Thanks to all these great people who helped to make the bot possible. :heart:")
            .addField("Developers", devs, true)
            .addField("Contribute", contrib, true)
            .addField("APIs Used", apis, false);
        if (Config.isIsSelfHosted()) {
            String selfHost = "This bot is self-hosted by **" + Config.getAuthor() + "**\n" +
                "Original Website - " + MarkdownUtil.maskedLink(Bot.website, Bot.website) + "\n" +
                "Original Help Server - " + MarkdownUtil.maskedLink(Bot.helpServer, Bot.helpServer) + "\n" +
                "Original Github - " + MarkdownUtil.maskedLink("Source Code", Bot.github);
            eb.addField("Self-Hosting", selfHost, false);
        } else {
            eb.addField("Hosting", host, false);
        }
        eb.addField("Special Thanks", e.getAuthor().getAsMention() + " for using Minecord!", false)
                .setFooter("Website: " + Config.getWebsite());
        return new Result(Outcome.SUCCESS, eb.build());
    }

}