package com.tisawesomeness.minecord.command;

import com.tisawesomeness.minecord.Lang;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@RequiredArgsConstructor
public enum Module {
    PLAYER(),
    UTILITY(),
    DISCORD(),
    CONFIG(),
    MISC(),
    ADMIN(true),
    CUSTOM;

    @Getter private final boolean hidden;
    Module() {
        hidden = false;
    }

    /**
     * Gets the ID of this module for localization purposes
     * @return The lowercase enum name
     */
    public @NonNull String getId() {
        return name().toLowerCase();
    }
    /**
     * Gets the display name for this module
     * @param lang The lang to use
     * @return The localized display name
     */
    public @NonNull String getDisplayName(Lang lang) {
        return lang.i18n(formatKey("name"));
    }
    /**
     * Defines the help text shown by {@code &help <module>}.
     * @return The help string, or empty if not defined
     */
    public Optional<String> getHelp(Lang lang, @NonNull String prefix) {
        return lang.i18nfOpt(formatKey("help"), prefix);
    }

    /**
     * Gets a module from its name.
     * @param name The case-insensitive name
     * @return The module, or empty if not found
     */
    public static Optional<Module> from(@NonNull String name, Lang lang) {
        return Arrays.stream(values())
                .filter(m -> m.getDisplayName(lang).equalsIgnoreCase(name))
                .findFirst();
    }

    private String formatKey(String key) {
        return String.format("module.%s.%s", getId(), key);
    }
}
