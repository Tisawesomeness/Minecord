package com.tisawesomeness.minecord;

import lombok.Getter;
import lombok.NonNull;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * An enum of all the registered bot languages.
 */
public enum Lang {
    EN_US("en_US", new Locale("en", "US")),
    DE_DE("de_DE", new Locale("de", "DE")),
    PT_BR("pt_BR", new Locale("pt", "BR"));

    private final ResourceBundle resource;

    @Getter private final @NonNull String code;
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
    /**
     * Whether this lang is in development, and should be hidden to un-elevated users.
     */
    @Getter private final boolean inDevelopment;
    /**
     * The flag emote ({@code :flag_xx:}) for the country
     */
    @Getter private final String flagEmote;

    Lang(@NonNull String code, @NonNull Locale locale) {
        this.code = code;
        this.locale = locale;

        resource = ResourceBundle.getBundle("lang", locale);
        percentComplete = Integer.parseInt(resource.getString("lang.percentComplete"));
        if (percentComplete < 0 || percentComplete > 100) {
            throw new IllegalArgumentException(
                    "\"lang.percentComplete\" in " + locale.getDisplayName() + "must be an integer between 0-100.");
        }
        botStringsSupported = getBool("lang.botStringsSupported");
        commandAliasSupported = getBool("lang.commandAliasSupported");
        itemsSupported = getBool("lang.itemsSupported");
        itemSearchSupported = getBool("lang.itemSearchSupported");
        inDevelopment = getBool("lang.inDevelopment");
        flagEmote = get("lang.flagEmote");
    }
    private boolean getBool(String key) {
        return Boolean.parseBoolean(resource.getString(key));
    }

    /**
     * Gets the default lang for the bot without configuration.
     */
    public static Lang getDefault() {
        return EN_US;
    }

    /**
     * Gets the lang associated with a language code
     * @param code The case-insensitive language code, formatted like {@code aa_BB} where
     *             {@code aa} is the language and {@code bb} is the country
     * @return The lang if found, otherwise empty
     */
    public static Optional<Lang> from(@NonNull String code) {
        return Arrays.stream(values())
                .filter(l -> l.code.equalsIgnoreCase(code))
                .findFirst();
    }

    /**
     * Gets the localization string for this lang.
     * <br>If not found, {@link #getDefault()} is used instead.
     * <br>Keys are in the format {@code category.optionalSubCategory.name}, where categories often follow package names.
     * <br>Lang config keys are in the {@code lang} category.
     * @param key The <b>case-sensitive</b> localization key. For example, {@code command.server.embedTitle}.
     * @return The localized string
     * @throws java.util.MissingResourceException If the given key could not be found.
     */
    public @NonNull String get(@NonNull String key) {
        return resource.getString(key);
    }
    /**
     * Gets a formatted, localized string for this lang.
     * @param key The <b>case-sensitive</b> localization key used in {@link #get(String)}
     * @param args An ordered list of arguments to place into the string
     * @return The localized string
     * @throws java.util.MissingResourceException If the given key could not be found.
     * @see MessageFormat
     * @see Locale
     */
    public @NonNull String getf(@NonNull String key, Object... args) {
        return new MessageFormat(get(key), locale).format(args);
    }

}
