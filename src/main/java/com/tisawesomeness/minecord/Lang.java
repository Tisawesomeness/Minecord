package com.tisawesomeness.minecord;

import lombok.Getter;
import lombok.NonNull;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * An enum of all the registered bot languages.
 */
public enum Lang {
    EN_US(new Locale("en", "US")),
    DE_DE(new Locale("de", "DE")),
    PT_BR(new Locale("pt", "BR"));

    private final ResourceBundle resource;
    @Getter private final @NonNull Locale locale;
    /**
     * An estimate of how complete the language is.
     */
    @Getter private final int percentComplete;
    /**
     * Whether this lang changes the text used in the output of commands.
     */
    @Getter private final boolean botStringsSupported;
    /**
     * Whether this lang adds language-specific command aliases.
     */
    @Getter private final boolean commandAliasSupported;
    /**
     * Whether this lang translates Minecraft item names.
     */
    @Getter private final boolean itemsSupported;
    /**
     * Whether this lang has search strings allowing for easier item searching, such as "gapple" for "golden apple".
     */
    @Getter private final boolean itemSearchSupported;

    Lang(@NonNull Locale locale) {
        this.locale = locale;
        resource = ResourceBundle.getBundle("lang", locale);
        percentComplete = Integer.parseInt(resource.getString("lang.percentComplete"));
        if (percentComplete < 0 || percentComplete > 100) {
            throw new IllegalArgumentException(
                    "\"lang.percentComplete\" in " + locale.getDisplayName() + "must be an integer between 0-100.");
        }
        botStringsSupported = Boolean.parseBoolean(resource.getString("lang.botStringsSupported"));
        commandAliasSupported = Boolean.parseBoolean(resource.getString("lang.commandAliasSupported"));
        itemsSupported = Boolean.parseBoolean(resource.getString("lang.itemsSupported"));
        itemSearchSupported = Boolean.parseBoolean(resource.getString("lang.itemSearchSupported"));
    }

    /**
     * Gets the default lang for the bot without configuration.
     */
    public static Lang getDefault() {
        return EN_US;
    }

    /**
     * Gets the localization string for this lang.
     * <br>If not found, {@link #getDefault()} is used instead.
     * <br>Keys are in the format {@code category.optionalSubCategory.name}.
     * @param key The localization key. For example, {@code command.server.embedTitle}.
     * @return The localized string.
     * @throws java.util.MissingResourceException If the given key could not be found.
     */
    public String get(@NonNull String key) {
        return resource.getString(key);
    }

}
