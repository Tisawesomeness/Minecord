package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.command.Result;
import com.tisawesomeness.minecord.util.DiscordUtils;

import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.concurrent.ExecutionException;

public class MsgCommand extends AbstractAdminCommand {

    public @NonNull String getId() {
        return "msg";
    }

    public Result run(String[] args, CommandContext ctx) {
        //Check for proper argument length
        if (args.length < 2) {
            return ctx.showHelp();
        }

        //Extract user
        User user = DiscordUtils.findUser(args[0], ctx.getBot().getShardManager());
        if (user == null) {
            return ctx.warn("Not a valid user!");
        }

        //Send the message
        String msg = null;
        try {
            PrivateChannel channel = user.openPrivateChannel().submit().get();
            msg = ctx.joinArgsSlice(1);
            channel.sendMessage(msg).queue();
        } catch (InterruptedException | ExecutionException ex) {
            ex.printStackTrace();
            return ctx.warn("There was an internal error.");
        }

        EmbedBuilder eb = new EmbedBuilder();
        User a = ctx.getE().getAuthor();
        eb.setAuthor(a.getAsTag() + " (`" + a.getId() + "`)", null, a.getAvatarUrl());
        eb.setDescription("**Sent a DM to " + user.getAsTag() + " (`" + user.getId() + "`):**\n" + msg);
        eb.setThumbnail(user.getAvatarUrl());
        ctx.log(eb.build());

        return ctx.reply("Message sent!");
    }

}
