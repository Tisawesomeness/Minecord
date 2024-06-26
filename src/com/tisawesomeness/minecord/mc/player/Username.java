package com.tisawesomeness.minecord.mc.player;

import lombok.NonNull;

import java.util.regex.Pattern;

/**
 * Represents a Minecraft Java username.
 * <p>
 *     <b>Beware: usernames are NOT guaranteed to contain only letters, numbers, and underscores!</b> They may have
 *     spaces or special characters, such as {@code $}. Normally, usernames can only be 3-16 characters and contain
 *     only ASCII letters, numbers, and underscores, but due to glitches or other methods, "invalid" accounts exist.
 *     This can be checked with {@link #isValid()}. Also note that some usernames have characters that can't be put
 *     into the Mojang API, such as {@code #}. This can be checked with {@link #isSupportedByMojangAPI()}.
 * </p>
 * <p>
 *     Note that names with swear words are often filtered out or involuntarily changed by Mojang, leading to blocked
 *     but valid names. Some names are blocked from being registered and will not show up on the API if nobody has
 *     the name, even if someone tries to change their name to it.
 * </p>
 */
public class Username implements CharSequence, Comparable<Username> {

    /**
     * The max length of a username.
     */
    public static final int MAX_LENGTH = 25;

    private static final Pattern VALID_USERNAME_PATTERN = Pattern.compile("^\\w{3,16}$");
    // '#' existed before but breaks the API
    // ':' and ')' sometimes break the API, but since they were included in only one known name "1cream#:)"
    // and Mojang's validation criteria is not clear, these characters are not supported
    private static final Pattern MOJANG_SUPPORTED_PATTERN = Pattern.compile(String.format(
            "^[\\w !@$\\-.?]{1,%d}$", MAX_LENGTH));
    private static final char QUOTE = '"';
    private static final char BACKTICK = '`';

    private final @NonNull String name;
    /**
     * Creates a username from a string literal.
     * @param name Any input string
     */
    public Username(@NonNull String name) {
        this.name = name;
    }

    /**
     * Checks if a username is valid according to Mojang's current (as of 3/4/21) requirements:
     * 3-16 letters, numbers, or underscores.
     * @return True only if the username is valid
     */
    public boolean isValid() {
        return VALID_USERNAME_PATTERN.matcher(name).matches();
    }

    /**
     * Checks if a username can be sent to the Mojang API.
     * Some characters (such as {@code -} or {@code $} are not valid but Mojang will process them.
     * Others (such as {@code #} and all non-ASCII) have appeared in usernames before but make Mojang freak out.
     * @return True only if the username contains only 1-{@link #MAX_LENGTH} approved characters, empty otherwise
     */
    public boolean isSupportedByMojangAPI() {
        return MOJANG_SUPPORTED_PATTERN.matcher(name).matches();
    }

    public boolean contains(@NonNull CharSequence s) {
        return name.contains(s);
    }


    /**
     * <p>
     *     Takes an input string, which may or may not be quoted, and parses it.
     *     If the input is unquoted, the username is interpreted literally.
     *     Otherwise, the username extends until the first closing quote character.
     * </p>
     * <p>
     *     The purpose of parsing strings is to ensure that spaces and special characters in names don't interfere with
     *     other arguments in commands.
     * </p>
     * @param input The input string
     * @return The parsed username
     * @throws IllegalArgumentException If the input is empty
     */
    public static @NonNull Username parse(@NonNull String input) {
        if (input.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (input.length() <= 2) {
            return new Username(input);
        }
        char startQuote = input.charAt(0);
        if (startQuote == QUOTE || startQuote == BACKTICK) {
            int endQuotePos = input.indexOf(startQuote, 1);
            if (endQuotePos != -1) {
                return new Username(input.substring(1, endQuotePos));
            }
        }
        return new Username(input);
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

    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Username)) {
            return false;
        }
        Username other = (Username) o;
        return name.equals(other.name);
    }
    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * @return The username as a string
     */
    @Override
    public @NonNull String toString() {
        return name;
    }

}
