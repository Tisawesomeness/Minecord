package com.tisawesomeness.minecord.command.general;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.database.Database;
import com.tisawesomeness.minecord.util.DateUtils;
import com.tisawesomeness.minecord.util.DiscordUtils;
import com.tisawesomeness.minecord.util.MessageUtils;

import java.awt.Color;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Guild.BoostTier;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;
import net.dv8tion.jda.api.utils.TimeUtil;

public class GuildCommand extends Command {

    public CommandInfo getInfo() {
		return new CommandInfo(
			"guild",
			"Shows guild info.",
			null,
			null,
			0,
			false,
			false,
			false
		);
    }
    
    public Result run(String[] args, MessageReceivedEvent e) {

        //If the author used the admin keyword and is an elevated user
        boolean elevated = false;
        Guild g;
		if (args.length > 1 && args[1].equals("admin") && Database.isElevated(e.getAuthor().getIdLong())) {
            elevated = true;
            if (!args[0].matches(DiscordUtils.idRegex)) {
                return new Result(Outcome.WARNING, ":warning: Not a valid ID!");
            }
            g = Bot.shardManager.getGuildById(args[0]);
            if (g == null) {
                return new Result(Outcome.WARNING, ":warning: Minecord does not know that guild ID!");
            }
        } else {
            g = e.getGuild();
        }
        User owner = g.retrieveOwner().complete().getUser();

        // Generate guild info
        EmbedBuilder eb = new EmbedBuilder()
            .setTitle(MarkdownSanitizer.escape(g.getName()))
            .setColor(Color.GREEN)
            .setImage(g.getIconUrl())
            .addField("ID", g.getId(), true)
            .addField("Users", String.valueOf(g.getMemberCount()), true)
            .addField("Roles", String.valueOf(g.getRoles().size()), true)
            .addField("Categories", String.valueOf(g.getCategories().size()), true)
            .addField("Text Channels", String.valueOf(g.getTextChannels().size()), true)
            .addField("Voice Channels", String.valueOf(g.getVoiceChannels().size()), true)
            .addField("Region", g.getRegion().getName(), true)
            .addField("Owner", MarkdownSanitizer.escape(owner.getAsTag()), true)
            .addField("Owner ID", owner.getId(), true)
            .addField("Created", DateUtils.getDateAgo(TimeUtil.getTimeCreated(g)), false);
         if (g.getBoostTier() == BoostTier.UNKNOWN) {
            eb.addField("Boosts", g.getBoostCount() + " (Unknown Tier)", true);
        } else {
            eb.addField("Boosts", String.format("%d (Tier %s)", g.getBoostCount(), g.getBoostTier().getKey()), true);
        }
        if (g.getVanityCode() != null) {
            eb.addField("Vanity Code", g.getVanityCode(), true);
        }
        if (g.getDescription() != null) {
            eb.addField("Description", MarkdownSanitizer.escape(g.getDescription()), false);
        }
        if (elevated && Database.isBanned(g.getIdLong())) {
            eb.setDescription("__**USER BANNED FROM MINECORD**__");
        }
        return new Result(Outcome.SUCCESS, MessageUtils.addFooter(eb).build());
    }
    
}