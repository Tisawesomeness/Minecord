package com.tisawesomeness.minecord.command;

import com.tisawesomeness.minecord.Bot;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.IntegrationType;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.util.EnumSet;
import java.util.Set;

public abstract class LegacyCommand implements Command<MessageReceivedEvent> {

    public static final EnumSet<InteractionContextType> CONTEXTS = EnumSet.of(
            InteractionContextType.GUILD, InteractionContextType.BOT_DM
    );

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
    public boolean supportsContext(Set<IntegrationType> install, InteractionContextType context) {
        return CONTEXTS.contains(context);
    }

    @Override
    public final String getMention() {
        String username = Bot.getSelfUser().getName();
        return MarkdownUtil.monospace(String.format("@%s %s", username, getInfo().name));
    }

    @Override
    public final String debugRunCommand(MessageReceivedEvent e) {
        return e.getMessage().getContentRaw();
    }

    public abstract Result run(String[] args, MessageReceivedEvent e) throws Exception;

}
