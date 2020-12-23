package com.tisawesomeness.minecord.mc.player;

import com.google.common.base.CharMatcher;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Represents a Minecraft Java username.
 * <p>
 *     Normally, usernames can only be 3-16 characters and contain only ASCII letters, numbers, and underscores,
 *     but due to glitches or other methods, "invalid" accounts exist. The Mojang API only works on ASCII usernames
 *     (although non-ASCII usernames exist), so this class is limited to ASCII usernames and imposes an additional
 *     sanity check of 1 to {@link #MAX_LENGTH} characters.
 * </p>
 * <p>
 *     <b>Beware: usernames are NOT guarenteed to contain only letters, numbers, and underscores!</b> They may have
 *     spaces or special characters (such as {@code /}).
 * </p>
 * <p>
 *     Note that names with swear words are often filtered out or involuntarily changed by Mojang, leading to blocked
 *     but valid names.
 * </p>
 * Convert a string to a username using {@link #from(String)} (which validates length and characters) and convert a
 * username back to a string by simply calling {@link #toString()}.
 * @see #isValid()
 */
@EqualsAndHashCode
public class Username implements CharSequence, Comparable<Username> {
    private static final Pattern VALID_USERNAME_PATTERN = Pattern.compile("^[0-9A-Za-z_]{3,16}$");
    /**
     * The max length of a username for sanity check purposes.
     */
    public static final int MAX_LENGTH = 256;

    private final @NonNull String name;
    private Username(@NonNull String name) {
        this.name = name;
    }

    /**
     * Creates a username from a string.
     * @param name Any input string
     * @return A username if the input string has only 1-{@link #MAX_LENGTH} ASCII characters, empty otherwise
     */
    public static Optional<Username> from(@NonNull String name) {
        if (name.isEmpty() || name.length() > MAX_LENGTH || !isAscii(name)) {
            return Optional.empty();
        }
        return Optional.of(new Username(name));
    }
    private static boolean isAscii(@NonNull CharSequence str) {
        return CharMatcher.ascii().matchesAllOf(str);
    }

    /**
     * Checks if a username is valid according to Mojang's current (as of 12/22/20) requirements:
     * 3-16 letters, numbers, or underscores.
     * @return True only if the username is valid
     */
    public boolean isValid() {
        return VALID_USERNAME_PATTERN.matcher(name).matches();
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
