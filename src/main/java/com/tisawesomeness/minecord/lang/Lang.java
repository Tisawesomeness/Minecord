package com.tisawesomeness.minecord.lang;

import com.google.common.base.Splitter;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;
import net.dv8tion.jda.api.Permission;

import java.text.Collator;
import java.text.MessageFormat;
import java.util.*;

/**
 * An enum of all the registered bot languages.
 */
public enum Lang {
    EN_US("en_US", new Locale("en", "US")),
    DE_DE("de_DE", new Locale("de", "DE")),
    PT_BR("pt_BR", new Locale("pt", "BR"));

    private final @NonNull ResourceBundle resource;
    private final @NonNull Collator collator2;
    private final @NonNull Collator collator3;

    @Getter private final @NonNull String code;
    @Getter private final @NonNull Locale locale;
    @Getter private final @NonNull Lang.Features features;

    /**
     * The flag emote ({@code :flag_xx:}) for the country this lang is based in.
     */
    @Getter private final @NonNull String flagEmote;

    Lang(@NonNull String code, @NonNull Locale locale) {
        this.code = code;
        this.locale = locale;
        resource = ResourceBundle.getBundle("lang/lang", locale);
        features = new Features();
        collator2 = newCollator(Collator.SECONDARY);
        collator3 = newCollator(Collator.TERTIARY);
        flagEmote = i18n("lang.flagEmote");
    }
    private Collator newCollator(int strength) {
        Collator collator = Collator.getInstance(locale);
        collator.setStrength(strength);
        return collator;
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
     * @throws java.util.MissingResourceException If the given key could not be found
     */
    public @NonNull String i18n(@NonNull String key) {
        return resource.getString(key);
    }
    /**
     * Gets a localized string for this lang if the key exists.
     * @param key The <b>case-sensitive</b> localization key used in {@link #i18n(String)}
     * @return The localized string, or empty if not found
     */
    public Optional<String> i18nOpt(@NonNull String key) {
        if (resource.keySet().contains(key)) {
            return Optional.of(i18n(key));
        }
        return Optional.empty();
    }
    /**
     * Gets a formatted, localized string for this lang.
     * @param key The <b>case-sensitive</b> localization key used in {@link #i18n(String)}
     * @param args An ordered list of arguments to place into the string
     * @return The localized string
     * @throws java.util.MissingResourceException If the given key could not be found
     * @see MessageFormat
     * @see Locale
     */
    public @NonNull String i18nf(@NonNull String key, Object... args) {
        return new MessageFormat(i18n(key), locale).format(args);
    }
    /**
     * Gets a builder for a formatted, localized markdown string for this lang.
     * @param key The <b>case-sensitive</b> localization key used in {@link #i18n(String)}
     * @param args An ordered list of arguments to place into the string
     * @return A new localized markdown builder
     * @throws java.util.MissingResourceException If the given key could not be found
     * @see LocalizedMarkdownBuilder
     * @see MessageFormat
     * @see Locale
     */
    public @NonNull LocalizedMarkdownBuilder i18nm(@NonNull String key, Object... args) {
        return new LocalizedMarkdownBuilder(new MessageFormat(i18n(key), locale), args);
    }
    /**
     * Gets a formatted, localized string for this lang if the key exists.
     * @param key The <b>case-sensitive</b> localization key used in {@link #i18n(String)}
     * @param args An ordered list of arguments to place into the string
     * @return The localized string, or empty if not found
     * @see MessageFormat
     * @see Locale
     */
    public Optional<String> i18nfOpt(@NonNull String key, Object... args) {
        if (resource.keySet().contains(key)) {
            return Optional.of(i18nf(key, args));
        }
        return Optional.empty();
    }
    /**
     * Takes a localized string and splits it by comma, ignoring empty strings.
     * @param key The <b>case-sensitive</b> localization key used in {@link #i18n(String)}
     * @return A possibly-empty, immutable list of strings
     * @see MessageFormat
     * @see Locale
     */
    public List<String> i18nList(@NonNull String key) {
        if (!resource.keySet().contains(key)) {
            return Collections.emptyList();
        }
        return Splitter.on(',').omitEmptyStrings().splitToList(i18n(key));
    }

    /**
     * Localizes an object as a user-readable string.
     * @param obj The localizable object
     * @return The object's description in this language
     * @throws java.util.MissingResourceException If the localization key could not be found
     */
    public @NonNull String localize(@NonNull Localizable obj) {
        return i18nf(obj.getTranslationKey(), obj.getTranslationArgs());
    }
    /**
     * Localizes an object as a user-readable string.
     * @param obj The localizable object
     * @return A builder to add markdown to the object's description in this language
     * @throws java.util.MissingResourceException If the localization key could not be found
     */
    public @NonNull LocalizedMarkdownBuilder localizeMarkdown(@NonNull Localizable obj) {
        return i18nm(obj.getTranslationKey(), obj.getTranslationArgs());
    }
    /**
     * Localizes a boolean value.
     * @param bool The boolean
     * @return "True" or "False" in this language
     */
    public @NonNull String localize(boolean bool) {
        return bool ? i18n("general.true") : i18n("general.false");
    }
    /**
     * Localizes a permission.
     * @param permission The permission, may be unknown
     * @return The name of the permission in this language
     */
    public @NonNull String localize(Permission permission) {
        return i18nOpt("discord.permissions." + permission.name().toLowerCase())
                .orElseGet(() -> i18n("discord.permissions.unknown"));
    }

    /**
     * Checks if two localized strings are generally equal, according to the current locale.
     * The "same" string may actually have different Unicode code points, those differences are ignored.
     * @param a The source string
     * @param b The target string
     * @return Whether the two strings are equal
     * @see Collator
     * @see Collator#TERTIARY
     */
    public boolean equals(@NonNull String a, @NonNull String b) {
        return collator3.equals(a, b);
    }
    /**
     * Compares two localized strings, according to the current locale.
     * The "same" string may actually have different Unicode code points, those differences are ignored.
     * @param a The source string
     * @param b The target string
     * @return -1, 0, or 1 if string A is less than, equal to, or greater than string B
     * @see Collator
     * @see Collator#TERTIARY
     */
    public int compare(@NonNull String a, @NonNull String b) {
        return collator3.compare(a, b);
    }
    /**
     * Checks if a list contains a string, according to the current locale.
     * The "same" string may actually have different Unicode code points, those differences are ignored.
     * @param list A list of non-null strings
     * @param str The string to look for
     * @return Whether the list contains the given string
     * @see Collator
     * @see Collator#TERTIARY
     */
    public boolean contains(Collection<? extends String> list, @NonNull String str) {
        return list.stream().anyMatch(s -> equals(s, str));
    }
    /**
     * Checks if two localized strings are generally equal ignoring case, according to the current locale.
     * The "same" string may actually have different Unicode code points, those differences are ignored.
     * @param a The source string
     * @param b The target string
     * @return Whether the two strings are equal ignoring case
     * @see Collator
     * @see Collator#SECONDARY
     */
    public boolean equalsIgnoreCase(@NonNull String a, @NonNull String b) {
        return collator2.equals(a, b);
    }
    /**
     * Compares two localized strings ignoring case, according to the current locale.
     * The "same" string may actually have different Unicode code points, those differences are ignored.
     * @param a The source string
     * @param b The target string
     * @return -1, 0, or 1 if string A is less than, equal to, or greater than string B ignoring case
     * @see Collator
     * @see Collator#SECONDARY
     */
    public int compareIgnoreCase(@NonNull String a, @NonNull String b) {
        return collator2.compare(a, b);
    }
    /**
     * Checks if a list contains a string, according to the current locale.
     * The "same" string may actually have different Unicode code points, those differences are ignored.
     * @param list A list of non-null strings
     * @param str The string to look for
     * @return Whether the list contains the given string
     * @see Collator
     * @see Collator#SECONDARY
     */
    public boolean containsIgnoreCase(Collection<? extends String> list, @NonNull String str) {
        return list.stream().anyMatch(s -> equalsIgnoreCase(s, str));
    }

    @Override
    public String toString() {
        return String.format("Lang(%s)", code);
    }

    /**
     * A list of language features and whether they're supported.
     */
    @Value
    public class Features {
        /**
         * Whether this lang is in development, and should be hidden to un-elevated users.
         */
        boolean inDevelopment;
        /**
         * Whether this lang changes the text used in the output of commands.
         */
        boolean botStringsSupported;
        /**
         * Whether this lang adds language-specific command aliases.
         */
        boolean commandAliasSupported;
        /**
         * Whether this lang translates Minecraft item names.
         */
        boolean itemsSupported;
        /**
         * Whether this lang has search strings allowing for easier item searching, such as "gapple" for "golden apple".
         */
        boolean itemSearchSupported;

        private Features() {
            inDevelopment = getBool("lang.inDevelopment");
            botStringsSupported = getBool("lang.botStringsSupported");
            commandAliasSupported = getBool("lang.commandAliasSupported");
            itemsSupported = getBool("lang.itemsSupported");
            itemSearchSupported = getBool("lang.itemSearchSupported");
        }
        private boolean getBool(String key) {
            return Boolean.parseBoolean(resource.getString(key));
        }
    }

}
