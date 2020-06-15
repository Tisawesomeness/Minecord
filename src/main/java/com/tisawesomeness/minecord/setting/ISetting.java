package com.tisawesomeness.minecord.setting;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

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
     * @return The setting's value or a message describing why the input is invalid.
     */
    ResolveResult<T> resolve(@NonNull String input);

    /**
     * The result of the {@link #resolve(String input)} method, containing a setting value or a message explaining why the input was invalid.
     * @param <T> The type of the setting.
     */
    @RequiredArgsConstructor
    class ResolveResult<T> {
        @Getter private final Optional<T> value;
        private final String msg;

        /**
         * Creates a successful resolve result.
         * @param val The value of the setting parsed from user input.
         */
        public ResolveResult(T val) {
            this(Optional.of(val), null);
        }
        /**
         * Creates a failed resolve result.
         * @param msg The message to display.
         */
        public ResolveResult(String msg) {
            this(Optional.empty(), msg);
        }

        /**
         * Gets the status associated with a failed resolve.
         * @throws UnsupportedOperationException When the value is valid.
         */
        public InvalidInputStatus toStatus() {
            if (value.isPresent()) {
                throw new UnsupportedOperationException("Resolve returned a valid value, use it.");
            }
            return new InvalidInputStatus(msg);
        }
    }

}
