package com.tisawesomeness.minecord.command;

import com.tisawesomeness.minecord.Bot;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

public abstract class LegacyCommand implements Command<MessageReceivedEvent> {

    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public final void sendSuccess(MessageReceivedEvent e, MessageCreateData message) {
        e.getChannel().sendMessage(message).queue();
    }
    @Override
    public final void sendFailure(MessageReceivedEvent e, MessageCreateData message) {
        e.getChannel().sendMessage(message).queue();
    }

    @Override
    public final String getMention() {
        String username = Bot.shardManager.getShardById(0).getSelfUser().getName();
        return MarkdownUtil.monospace(String.format("@%s %s", username, getInfo().name));
    }

    @Override
    public final String debugRunCommand(MessageReceivedEvent e) {
        return e.getMessage().getContentRaw();
    }

    public abstract Result run(String[] args, MessageReceivedEvent e) throws Exception;

}
