package com.tisawesomeness.minecord.setting;

import com.tisawesomeness.minecord.share.util.Validation;

import lombok.NonNull;

/**
 * Provides {@link Setting} display info and user input parsing.
 * @param <T> The type of the setting
 */
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
     * @param input The non-empty string input extracted from a Discord message.
     * @return The setting's value or a message describing why the input is invalid.
     */
    Validation<T> resolve(@NonNull String input);

}
