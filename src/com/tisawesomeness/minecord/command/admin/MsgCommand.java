package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.command.LegacyCommand;
import com.tisawesomeness.minecord.util.ArrayUtils;
import com.tisawesomeness.minecord.util.DiscordUtils;
import com.tisawesomeness.minecord.util.MessageUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.concurrent.ExecutionException;

public class MsgCommand extends LegacyCommand {

    public CommandInfo getInfo() {
        return new CommandInfo(
                "msg",
                "Open the DMs.",
                "<mention|id> <message>",
                0,
                true,
                true
        );
    }

    public String[] getAliases() {
        return new String[]{"dm", "tell", "pm"};
    }

    public Result run(String[] args, MessageReceivedEvent e) {

        //Check for proper argument length
        if (args.length < 2) {
            return new Result(Outcome.WARNING, ":warning: Please specify a message.");
        }

        //Extract user
        User user = DiscordUtils.findUser(args[0]);
        if (user == null) return new Result(Outcome.ERROR, ":x: Not a valid user!");

        //Send the message
        String msg;
        try {
            PrivateChannel channel = user.openPrivateChannel().submit().get();
            msg = String.join(" ", ArrayUtils.remove(args, 0));
            channel.sendMessage(msg).queue();
        } catch (InterruptedException | ExecutionException ex) {
            ex.printStackTrace();
            return new Result(Outcome.ERROR, ":x: An exception occured.");
        }

        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(e.getAuthor().getName() + " (" + e.getAuthor().getId() + ")",
                null, e.getAuthor().getAvatarUrl());
        eb.setDescription("**Sent a DM to " + user.getName() + " (" + user.getId() + "):**\n" + msg);
        eb.setThumbnail(user.getAvatarUrl());
        MessageUtils.log(eb.build());

        return new Result(Outcome.SUCCESS);
    }

}
