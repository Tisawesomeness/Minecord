package com.tisawesomeness.minecord.setting;

import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.database.DatabaseCache;
import com.tisawesomeness.minecord.util.type.Validation;

import lombok.NonNull;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Optional;

public interface ISetting<T> {
    /**
     * Gets the user-facing name for this setting.
     * @return The display name.
     */
    @NonNull String getDisplayName();
    /**
     * Determines whether {@code &setting input ...} should return this setting.
     * @param input The string input extracted from a Discord message.
     * @return Whether the given string is an alias for this setting.
     */
    boolean isAlias(@NonNull String input);
    /**
     * Generates the user-facing description for a setting.
     * @param prefix The prefix to use.
     * @param tag The bot mention tag to use.
     * @return The description of what the setting does and the possible values.
     */
    @NonNull String getDescription(@NonNull String prefix, @NonNull String tag);

    /**
     * Parses a setting from user input.
     * @param input The string input extracted from a Discord message.
     * @return The setting's value or a message describing why the input is invalid.
     */
    Validation<T> resolve(@NonNull String input);

    /**
     * Gets the default value for this setting, usually defined by config.
     */
    @NonNull T getDefault();

    /**
     * Gets the value of this setting used in the current context.
     * @param e The event that triggered the executing command.
     * @return The value of the setting, or empty if unset.
     */
    Optional<T> get(@NonNull MessageReceivedEvent e, DatabaseCache cache);
    /**
     * Gets the value of this setting used in the current context.
     * @param txt The context of the executing command.
     * @return The value of the setting, or empty if unset.
     */
    default Optional<T> get(@NonNull CommandContext txt) {
        return get(txt.e, txt.bot.getDatabase().getCache());
    }
    /**
     * Gets the value of this setting used in the current context.
     * @param e The event that triggered the executing command.
     * @return The value of the setting, or the default if unset.
     */
    default @NonNull T getEffective(@NonNull MessageReceivedEvent e, DatabaseCache cache) {
        return get(e, cache).orElse(getDefault());
    }
    /**
     * Gets the value of this setting used in the current context.
     * @param txt The context of the executing command.
     * @return The value of the setting, or the default if unset.
     */
    default @NonNull T getEffective(@NonNull CommandContext txt) {
        return get(txt).orElse(getDefault());
    }
}
