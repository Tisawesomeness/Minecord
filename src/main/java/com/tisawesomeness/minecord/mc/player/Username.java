package com.tisawesomeness.minecord.mc.player;

import com.google.common.base.CharMatcher;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.util.Optional;
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
    /**
     * The max length of a username for sanity check purposes.
     */
    public static final int MAX_LENGTH = 256;

    private final @NonNull String name;
    /**
     * Creates a username from a string.
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
}
