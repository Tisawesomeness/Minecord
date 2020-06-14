package com.tisawesomeness.minecord.command;

import com.tisawesomeness.minecord.Bot;
import lombok.RequiredArgsConstructor;
import lombok.With;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Stores the information available to every command
 */
@With
@RequiredArgsConstructor
public class CommandContext {
    /**
     * The arguments given to the command, split by spaces.
     */
    public final String[] args;
    /**
     * The event that triggered the command.
     */
    public final MessageReceivedEvent e;
    /**
     * A link to the bot instance.
     */
    public final Bot bot;
    /**
     * The current prefix. Either guild-specific or the config default for DMs.
     */
    public final String prefix;
    /**
     * Whether the user executing the command is elevated.
     */
    public final boolean isElevated;
}
