package com.tisawesomeness.minecord.setting;

import lombok.NonNull;

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
     * Gets the default value for this setting, usually defined by config.
     */
    @NonNull T getDefault();

    /**
     * Parses a setting from user input.
     * @param input The string input extracted from a Discord message.
     * @return The setting's value or a message describing why the input is invalid.
     */
    ResolveResult<T> resolve(@NonNull String input);

}
