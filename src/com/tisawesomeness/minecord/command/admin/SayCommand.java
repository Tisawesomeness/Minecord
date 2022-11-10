package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.command.LegacyCommand;
import com.tisawesomeness.minecord.util.ArrayUtils;
import com.tisawesomeness.minecord.util.DiscordUtils;
import com.tisawesomeness.minecord.util.MessageUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SayCommand extends LegacyCommand {

    public CommandInfo getInfo() {
        return new CommandInfo(
                "say",
                "Send a message.",
                "<channel> <message>",
                0,
                true,
                true
        );
    }

    public String[] getAliases() {
        return new String[]{"talk", "announce"};
    }

    public String getHelp() {
        return "`{&}say <channel> <message>` - Make the bot send a message.\n" +
                "`<channel>` is either a # channel name or a channel ID.\n";
    }

    public Result run(String[] args, MessageReceivedEvent e) {

        //Check for proper argument length
        if (args.length < 2) {
            return new Result(Outcome.WARNING, ":warning: Please specify a message.");
        }

        //Extract channel
        TextChannel channel = DiscordUtils.findChannel(args[0]);
        if (channel == null) return new Result(Outcome.ERROR, ":x: Not a valid channel!");

        //Send the message
        String msg = String.join(" ", ArrayUtils.remove(args, 0));
        channel.sendMessage(msg).queue();

        //Log it
        EmbedBuilder eb = new EmbedBuilder();
        Guild guild = channel.getGuild();
        eb.setAuthor(e.getAuthor().getName() + " (" + e.getAuthor().getId() + ")",
                null, e.getAuthor().getAvatarUrl());
        eb.setDescription("**Sent a msg to `" + channel.getName() + "` (" + channel.getId() + ")**\non `" +
                guild.getName() + "` (" + guild.getId() + "):\n" + msg);
        eb.setThumbnail(guild.getIconUrl());
        MessageUtils.log(eb.build());

        return new Result(Outcome.SUCCESS);
    }

}
