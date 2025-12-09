package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.LegacyCommand;
import com.tisawesomeness.minecord.util.DiscordUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

public class LogCommand extends LegacyCommand {

    @Override
    public CommandInfo getInfo() {
        return new CommandInfo(
                "log",
                "Log a message",
                "<message>",
                0,
                true,
                true
        );
    }

    @Override
    public Result run(String[] args, MessageReceivedEvent e) throws Exception {
        if (args.length < 1) {
            return new Result(Outcome.WARNING, ":warning: Please specify a message.");
        }

        EmbedBuilder eb = new EmbedBuilder();

        String authorLogMsg = DiscordUtils.tagAndId(e.getAuthor());
        eb.setAuthor(authorLogMsg, null, e.getAuthor().getAvatarUrl());

        String msg = String.join(" ", args);
        eb.setDescription(msg);

        Bot.logger.log(MessageCreateData.fromEmbeds(eb.build()));
        return new Result(Outcome.SUCCESS);
    }

}
