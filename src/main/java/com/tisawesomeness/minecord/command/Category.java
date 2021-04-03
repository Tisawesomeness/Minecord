package com.tisawesomeness.minecord.command;

import com.tisawesomeness.minecord.lang.Lang;
import com.tisawesomeness.minecord.lang.Localizable;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

/**
 * A category for organizing {@link Command Commands}.
 * Cannot conflict with the word "meta", or the word "extra" in the current or default language.
 */
@RequiredArgsConstructor
public enum Category implements Localizable {
    CORE(),
    PLAYER(),
    UTILITY(),
    DISCORD(),
    CONFIG(),
    ADMIN(true),
    CUSTOM;

    @Getter private final boolean hidden;
    Category() {
        hidden = false;
    }

    /**
     * Gets the ID of this category for localization purposes
     * @return The lowercase enum name
     */
    public @NonNull String getId() {
        return name().toLowerCase();
    }
    /**
     * Defines the help text shown by {@code &help <category>}.
     * @return The help string, or empty if not defined
     */
    public Optional<String> getHelp(Lang lang, @NonNull String prefix) {
        return lang.i18nfOpt(formatKey("help"), prefix);
    }

    /**
     * Gets a category from its name.
     * @param name The case-insensitive name
     * @param lang The current language
     * @return The category, or empty if not found
     */
    public static Optional<Category> from(@NonNull String name, Lang lang) {
        return Arrays.stream(values())
                .filter(m -> lang.equalsIgnoreCase(lang.localize(m), name))
                .findFirst();
    }

    public @NonNull String getTranslationKey() {
        return formatKey("name");
    }
    public Object[] getTranslationArgs() {
        return new Object[0];
    }

    private String formatKey(String key) {
        return String.format("category.%s.%s", getId(), key);
    }

}
