package com.tisawesomeness.minecord.command.misc;

import com.tisawesomeness.minecord.Bot;
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
    
    private String contrib = MarkdownUtil.maskedLink("Contribute on GitHub", Bot.github) + "\n" +
        MarkdownUtil.maskedLink("Suggest features here", Bot.helpServer);
    
    private String apis = "Discord API Wrapper - " + MarkdownUtil.maskedLink("JDA", "https://github.com/DV8FromTheWorld/JDA") + "\n" +
        "MC Account Info - " + MarkdownUtil.maskedLink("Mojang API", "https://wiki.vg/Mojang_API") + "\n" +
        "Skin Renders - " + MarkdownUtil.maskedLink("Crafatar", "https://crafatar.com") + "\n" +
        "Server Pinging - " + MarkdownUtil.maskedLink("MCServerPing", "https://github.com/lucaazalim/minecraft-server-ping") + "\n" +
        "Cape Sites - " +
            MarkdownUtil.maskedLink("Optifine", "https://optifine.net") + ", " +
            MarkdownUtil.maskedLink("LabyMod", "https://www.labymod.net") + ", " +
            MarkdownUtil.maskedLink("MinecraftCapes", "https://www.minecraftcapes.co.uk");
    
    private String host = "The public bot is proudly hosted by " + MarkdownUtil.maskedLink("Endless Hosting", "https://theendlessweb.com/")+ ".\n";

    public Result run(String[] args, MessageReceivedEvent e) throws Exception {
        EmbedBuilder eb = new EmbedBuilder()
            .setTitle("Minecord Credits")
            .setColor(Bot.color)
            .setDescription("Thanks to all these great people who helped to make the bot possible. :heart:")
            .addField("Developers", devs, true)
            .addField("Contribute", contrib, true)
            .addField("APIs Used", apis, false)
            .addField("Hosting", host, false)
            .addField("Special Thanks", e.getAuthor().getAsMention() + " for using Minecord!", false)
            .setFooter("Website: " + Bot.website);
        return new Result(Outcome.SUCCESS, eb.build());
    }

}