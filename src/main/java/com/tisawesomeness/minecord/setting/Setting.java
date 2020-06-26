package com.tisawesomeness.minecord.setting;

import com.tisawesomeness.minecord.command.CommandContext;

import lombok.NonNull;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Optional;

public abstract class Setting<T> implements ISetting<T> {

    /**
     * Gets the value of this setting used in the current context.
     * @param e The event that triggered the executing command.
     * @return The value of the setting, or null if unset.
     */
    public abstract Optional<T> get(@NonNull MessageReceivedEvent e);

    /**
     * Gets the value of this setting used in the current context.
     * @param txt The context of the executing command.
     * @return The value of the setting, or null if unset.
     */
    public Optional<T> get(@NonNull CommandContext txt) {
        return get(txt.e);
    }

    /**
     * Gets the value of this setting used in the current context.
     * @param e The event that triggered the executing command.
     * @return The value of the setting, or the default if unset.
     */
    public @NonNull T getEffective(@NonNull MessageReceivedEvent e) {
        return get(e).orElse(getDefault());
    }

    /**
     * Gets the value of this setting used in the current context.
     * @param txt The context of the executing command.
     * @return The value of the setting, or the default if unset.
     */
    public @NonNull T getEffective(@NonNull CommandContext txt) {
        return getEffective(txt.e);
    }

}
