package com.tisawesomeness.minecord.command;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.setting.SettingRegistry;
import com.tisawesomeness.minecord.setting.impl.DeleteCommandsSetting;
import com.tisawesomeness.minecord.setting.impl.UseMenusSetting;
import lombok.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.time.OffsetDateTime;

/**
 * Stores the information available to every command
 */
@RequiredArgsConstructor
public class CommandContext {
    /**
     * The arguments given to the command, split by spaces. May be length 0.
     */
    @With
    public final @NonNull String[] args;
    /**
     * The event that triggered the command.
     */
    public final @NonNull MessageReceivedEvent e;
    /**
     * The loaded config.
     */
    public final @NonNull Config config;
    /**
     * A link to the bot instance.
     */
    public final @NonNull Bot bot;
    /**
     * Whether the user executing the command is elevated.
     */
    public final boolean isElevated;
    /**
     * The current prefix. Either guild-specific or the config default for DMs.
     */
    public final @NonNull String prefix;

    public CommandContext(@NonNull String[] args, @NonNull MessageReceivedEvent e, @NonNull Bot bot,
                          @NonNull Config config, boolean isElevated, @NonNull String prefix,
                          @NonNull SettingRegistry settings) {
        this(args, e, config, bot, isElevated, prefix, settings.deleteCommands, settings.useMenus);
    }

    // @Getter is private so the public alternative with correct grammar can be used instead, keeping laziness

    private final @NonNull DeleteCommandsSetting deleteCommandsSetting;
    @Getter(value=AccessLevel.PRIVATE, lazy=true) private final boolean deleteCommands = calcDeleteCommands();
    public boolean shouldDeleteCommands() { return isDeleteCommands(); }
    private boolean calcDeleteCommands() {
        return deleteCommandsSetting.getEffective(this);
    }

    private final @NonNull UseMenusSetting useMenusSetting;
    @Getter(value=AccessLevel.PRIVATE, lazy=true) private final boolean useMenus = calcUseMenus();
    public boolean shouldUseMenus() { return isUseMenus(); }
    private boolean calcUseMenus() {
        return useMenusSetting.getEffective(this);
    }

    /**
     * Logs a message to the logging channel.
     */
    public void log(String m) {
        bot.log(m);
    }
    /**
     * Logs a message to the logging channel.
     */
    public void log(Message m) {
        bot.log(m);
    }
    /**
     * Logs a message to the logging channel.
     */
    public void log(MessageEmbed m) {
        bot.log(m);
    }

}
