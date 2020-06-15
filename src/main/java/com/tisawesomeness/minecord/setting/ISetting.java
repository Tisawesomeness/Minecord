package com.tisawesomeness.minecord.setting;

import lombok.NonNull;

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
     * Whether the setting can be changed for users.
     */
    boolean supportsUsers();
    /**
     * Whether the setting can be changed for guilds.
     */
    boolean supportsGuilds();

    /**
     * Gets the value of this setting for the user.
     * @param id The ID of the Discord user.
     * @return The value of the setting, or null if unset.
     */
    Optional<T> getUser(long id);
    /**
     * Gets the value of this setting for the guild.
     * @param id The ID of the Discord guild.
     * @return The value of the setting, or null if unset.
     */
    Optional<T> getGuild(long id);
    /**
     * Gets the default value for this setting, usually defined by config.
     */
    @NonNull T getDefault();

    /**
     * Generates the user-facing description for a setting.
     * @param prefix The prefix to use.
     * @param tag The bot mention tag to use.
     * @return The description of what the setting does and the possible values.
     */
    @NonNull String getDescription(String prefix, String tag);

    /**
     * Parses a setting from user input.
     * @param input The string input extracted from a Discord message.
     * @return The setting's value or {@code null} if the input is invalid.
     */
    Optional<T> resolve(@NonNull String input);
}
