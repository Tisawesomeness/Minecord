package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.command.meta.CommandContext;
import com.tisawesomeness.minecord.util.Discord;

import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class SayCommand extends AbstractAdminCommand {

    public @NonNull String getId() {
        return "say";
    }

    public void run(String[] args, CommandContext ctx) {

        //Check for proper argument length
        if (args.length < 2) {
            ctx.showHelp();
            return;
        }

        //Extract channel
        TextChannel channel = Discord.findChannel(args[0], ctx.getBot().getShardManager());
        if (channel == null) {
            ctx.warn("Not a valid channel!");
            return;
        }

        //Send the message
        String msg = ctx.joinArgsSlice(1);
        channel.sendMessage(msg).queue();

        //Log it
        EmbedBuilder eb = new EmbedBuilder();
        Guild guild = channel.getGuild();
        User a = ctx.getE().getAuthor();
        eb.setAuthor(a.getAsTag() + " (`" + a.getId() + "`)", null, a.getAvatarUrl());
        eb.setDescription("**Sent a msg to `" + channel.getName() + "` (`" + channel.getId() + "`)**\non `" +
            guild.getName() + "` (" + guild.getId() + "):\n" + msg);
        eb.setThumbnail(guild.getIconUrl());
        ctx.log(eb.build());

        ctx.reply("Message sent!");
    }

}
