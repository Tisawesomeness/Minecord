package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.LegacyCommand;
import com.tisawesomeness.minecord.util.ArrayUtils;
import com.tisawesomeness.minecord.util.DiscordUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

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
        String authorLogMsg = DiscordUtils.tagAndId(e.getAuthor());
        eb.setAuthor(authorLogMsg, null, e.getAuthor().getAvatarUrl());
        String sentLogMsg = "Sent a message to " + DiscordUtils.tagAndId(user);
        System.out.println(authorLogMsg + " " + sentLogMsg + ":\n" + msg);
        eb.setDescription(MarkdownUtil.bold(sentLogMsg) + ":\n" + msg);
        eb.setThumbnail(user.getAvatarUrl());
        Bot.logger.log(MessageCreateData.fromEmbeds(eb.build()));

        return new Result(Outcome.SUCCESS);
    }

}
