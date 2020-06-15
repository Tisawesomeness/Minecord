package com.tisawesomeness.minecord.setting;

import com.tisawesomeness.minecord.command.CommandContext;
import lombok.NonNull;

import java.util.Optional;

public abstract class Setting<T> implements ISetting<T> {

    /**
     * Gets the value of this setting used in the current context.
     * @param txt The context of the executing command.
     * @return The value of the setting, or null if unset.
     */
    public abstract Optional<T> get(@NonNull CommandContext txt);

    /**
     * Gets the value of this setting used in the current context.
     * @param txt The context of the executing command.
     * @return The value of the setting, or the default if unset.
     */
    public @NonNull T getEffective(@NonNull CommandContext txt) {
        return get(txt).orElse(getDefault());
    }

}
