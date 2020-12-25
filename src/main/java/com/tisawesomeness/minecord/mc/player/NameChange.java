package com.tisawesomeness.minecord.mc.player;

import lombok.NonNull;
import lombok.Value;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.Comparator;
import java.util.Optional;

/**
 * Represents a point in a player's name change history. Either the name change is the original name
 * ({@link #isOriginal()}) is {@code true} and {@link #getTime()} is empty, or the name was changed at a specific time
 * ({@link #isOriginal()}) is {@code false} and {@link #getTime()} is present.
 * <p>
 *     The natural order (see {@link Comparator}) for name changes is original names first, then earliest time to
 *     latest time.
 * </p>
 * @see #withTimestamp(Username, long)
 * @see #original(Username)
 */
@Value
public class NameChange implements Comparable<NameChange> {
    private static final Comparator<NameChange> comparator = initComparator();

    /**
     * The username the player was <b>changed to</b>.
     */
    @NonNull Username username;
    @Nullable Instant time;

    private NameChange(@NonNull Username username, @Nullable Instant time) {
        this.username = username;
        this.time = time;
    }

    /**
     * Creates a name change with the given username and timestamp.
     * @param username The username the player was <b>changed to</b>
     * @param timestamp The timestamp the username was changed
     */
    public static @NonNull NameChange withTimestamp(@NonNull Username username, long timestamp) {
        return new NameChange(username, Instant.ofEpochMilli(timestamp));
    }
    /**
     * Creates a name change with the original username.
     * @param username The username the player had <b>when the account was created</b>
     */
    public static @NonNull NameChange original(@NonNull Username username) {
        return new NameChange(username, null);
    }

    /**
     * @return The time the username was changed, or empty if it was the original username
     */
    public Optional<Instant> getTime() {
        return Optional.ofNullable(time);
    }
    /**
     * @return True if the username in this name change was the original username
     */
    public boolean isOriginal() {
        return time == null;
    }

    /**
     * Compares this name change to another, see the class documentation for the ordering.
     * @param other The other name change
     * @return -1, 0, or 1 if this name change is less than, equal to,
     * or greater than the other name change respectively
     */
    public int compareTo(@NonNull NameChange other) {
        return comparator.compare(this, other);
    }

    @Override
    public @NonNull String toString() {
        if (time == null) {
            return String.format("NameChange(%s original)", username);
        }
        return String.format("NameChange(%s at %s)", username, time);
    }

    private static Comparator<NameChange> initComparator() {
        Comparator<Instant> nullsFirst = Comparator.nullsFirst(Comparator.naturalOrder());
        Comparator<NameChange> timeComparator = Comparator.comparing(nc -> nc.time, nullsFirst);
        return timeComparator.thenComparing(NameChange::getUsername);
    }

}
