package com.tisawesomeness.minecord.command;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.setting.SettingRegistry;
import com.tisawesomeness.minecord.setting.impl.DeleteCommandsSetting;
import com.tisawesomeness.minecord.setting.impl.UseMenusSetting;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.With;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

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
                          boolean isElevated, @NonNull String prefix, @NonNull SettingRegistry settings) {
        this(args, e, bot, isElevated, prefix, settings.deleteCommands, settings.useMenus);
    }

    private final @NonNull DeleteCommandsSetting deleteCommandsSetting;
    @Getter(lazy=true) private final boolean deleteCommands = calcDeleteCommands();
    private boolean calcDeleteCommands() {
        return deleteCommandsSetting.getEffective(this);
    }

    private final @NonNull UseMenusSetting useMenusSetting;
    @Getter(lazy=true) private final boolean useMenus = calcUseMenus();
    private boolean calcUseMenus() {
        return useMenusSetting.getEffective(this);
    }

}
