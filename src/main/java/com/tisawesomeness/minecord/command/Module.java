package com.tisawesomeness.minecord.command;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Optional;

@RequiredArgsConstructor
public enum Module {
    PLAYER("Player"),
    UTILITY("Utility"),
    DISCORD("Discord"),
    CONFIG("Config"),
    MISC("Misc"),
    ADMIN("Admin", true, "**These commands require elevation to use.**\n\n" +
            "`{&}info admin` - Displays bot info, including used memory and boot time.\n" +
            "`{&}settings admin <context/list [guild id]/channel id>` - View the bot's setting for another guild, channel, or user.\n" +
            "`{&}set admin <context> <setting> <value>` - Change the bot's setting for another guild, channel, or user.\n" +
            "`{&}reset admin <context> <setting>` - Reset the bot's setting for another guild, channel, or user.\n" +
            "`{&}perms <channel id> admin` - Test the bot's permissions in any channel.\n" +
            "`{&}user <user id> admin [mutual]` - Show info, ban status, and elevation for a user outside of the current guild. Include \"mutual\" to show mutual guilds.\n" +
            "`{&}guild <guild id> admin` - Show info and ban status for another guild.\n");

    @Getter private final @NonNull String name;
    @Getter private final boolean hidden;
    private final @Nullable String moduleHelp;

    Module(@NonNull String name) {
        this(name, false, null);
    }

    /**
     * Defines the help text shown by {@code &help <module>}.
     * Use {@code {&}} to substitute the current prefix.
     * @return The help string, or empty if not defined
     */
    public Optional<String> getHelp(@NonNull CharSequence prefix) {
        return Optional.ofNullable(moduleHelp).map(s -> s.replace("{&}", prefix));
    }

    /**
     * Gets a module from its name.
     * @param name The case-insensitive name
     * @return The module, or empty if not found
     */
    public static Optional<Module> from(@NonNull String name) {
        return Arrays.stream(values())
                .filter(m -> m.name.equalsIgnoreCase(name))
                .findFirst();
    }
}
