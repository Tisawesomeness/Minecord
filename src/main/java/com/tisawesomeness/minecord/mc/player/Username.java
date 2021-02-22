package com.tisawesomeness.minecord.mc.player;

import com.google.common.base.CharMatcher;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Represents a Minecraft Java username.
 * <p>
 *     <b>Beware: usernames are NOT guarenteed to contain only letters, numbers, and underscores!</b> They may have
 *     spaces or special characters (such as {@code /}). Normally, usernames can only be 3-16 characters and contain
 *     only ASCII letters, numbers, and underscores, but due to glitches or other methods, "invalid" accounts exist.
 *     This can be checked with {@link #isSupportedByMojangAPI()}.
 * </p>
 * <p>
 *     Note that names with swear words are often filtered out or involuntarily changed by Mojang, leading to blocked
 *     but valid names. Some names are blocked from being registered and will not show up on the API if nobody has
 *     the name, even if someone tries to change their name to it.
 * </p>
 */
@EqualsAndHashCode
public class Username implements CharSequence, Comparable<Username> {

    private static final Pattern VALID_USERNAME_PATTERN = Pattern.compile("^[0-9A-Za-z_]{3,16}$");
    private static final char BACKSLASH = '\\';
    private static final char QUOTE = '"';
    private static final char BACKTICK = '`';

    /**
     * The max length of a username for sanity check purposes.
     */
    public static final int MAX_LENGTH = 256;

    private final @NonNull String name;
    /**
     * Creates a username from a string literal.
     * @param name Any input string
     */
    public Username(@NonNull String name) {
        this.name = name;
    }

    /**
     * Checks if a username is valid according to Mojang's current (as of 12/22/20) requirements:
     * 3-16 letters, numbers, or underscores.
     * @return True only if the username is valid
     */
    public boolean isValid() {
        return VALID_USERNAME_PATTERN.matcher(name).matches();
    }

    /**
     * If this username was parsed from a command, determines the number of arguments (separated by spaces) this
     * username takes up.
     * @return A number greater than or equal to 1
     */
    public int argsUsed() {
        return CharMatcher.is(' ').countIn(name) + 1;
    }

    /**
     * Checks if a username can be sent to the Mojang API.
     * @return True only if the username contains only 1-{@link #MAX_LENGTH} ASCII characters, empty otherwise
     */
    public boolean isSupportedByMojangAPI() {
        return !name.isEmpty() && name.length() <= MAX_LENGTH && isAscii(name);
    }
    private static boolean isAscii(@NonNull CharSequence str) {
        return CharMatcher.ascii().matchesAllOf(str);
    }

    public boolean contains(@NonNull CharSequence s) {
        return name.contains(s);
    }

    /**
     * Escapes a username for use in commands by replacing " with \" and \ with \\.
     * <br>This is needed just in case a username with a quote or backslash ever gets created.
     * @param input The unescaped username as input
     * @return The escaped username
     */
    public static @NonNull String escape(@NonNull CharSequence input) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            if (ch == QUOTE || ch == BACKSLASH) {
                sb.append(BACKSLASH);
            }
            sb.append(ch);
        }
        return sb.toString();
    }

    /**
     * Determines if a string will be treated as quoted. Note that only the starting quote is necessary.
     * @param input The string
     * @return Whether the string will be parsed like a quoted name
     */
    public static boolean isQuoted(@NonNull CharSequence input) {
        char ch = input.charAt(0);
        return ch == QUOTE || ch == BACKTICK;
    }

    /**
     * <p>
     *     Takes an input string, which may or may not be quoted, and parses it.
     *     If the input is unquoted, the username is interpreted literally.
     *     Otherwise, the username extends until the closing quote character. Quotes can be either quote characters or
     *     backticks, both may be escaped by a backslash, and backslashes themselves can be escaped.
     * </p>
     * <p>
     *     The purpose of parsing strings it to ensure that spaces and special characters in names don't interfere with
     *     other arguments in commands.
     * </p>
     * @param input The input string
     * @return The parsed username
     */
    public static @NonNull Username parse(@NonNull String input) {
        if (input.length() <= 1) {
            return new Username(input);
        }
        QuoteType quoteType = QuoteType.from(input.charAt(0));
        if (quoteType == QuoteType.NONE) {
            return new Username(input);
        }

        // Starting at 1 to not include the first quote
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < input.length() - 1; i++) {
            char ch = input.charAt(i);
            if (ch == BACKSLASH) {
                char next = input.charAt(i + 1);
                if (next == BACKSLASH || next == quoteType.ch) {
                    sb.append(next);
                    // make sure next char does not get processed
                    i++;
                    continue;
                }
            } else if (ch == quoteType.ch) {
                return new Username(sb.toString());
            }
            sb.append(ch);
        }
        char lastChar = input.charAt(input.length() - 1);
        if (lastChar != quoteType.ch) {
            sb.append(lastChar);
        }
        return new Username(sb.toString());
    }

    public int length() {
        return name.length();
    }
    public char charAt(int i) {
        return name.charAt(i);
    }
    public CharSequence subSequence(int i, int i1) {
        return name.subSequence(i, i1);
    }

    /**
     * Compares this username to another alphabetically
     * @param other The other username
     * @return -1, 0, or 1 if this username is less than, equal to,
     * or greater than the username change respectively
     */
    public int compareTo(@NonNull Username other) {
        return name.compareTo(other.name);
    }

    /**
     * @return The username as a string
     */
    @Override
    public @NonNull String toString() {
        return name;
    }

    @RequiredArgsConstructor
    private enum QuoteType {
        QUOTES(QUOTE), BACKTICKS(BACKTICK), NONE('\0');

        public final char ch;
        public static QuoteType from(char ch) {
            return Arrays.stream(QuoteType.values())
                    .filter(qt -> ch == qt.ch)
                    .findFirst()
                    .orElse(NONE);
        }
    }

}
