package com.tisawesomeness.minecord.util.type;

import com.tisawesomeness.minecord.lang.Localizable;

import com.google.common.base.Preconditions;
import lombok.*;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * A fixed length of time consisting of a whole number of specific time unit, such as "8 seconds" or "42 days".
 * Mixed time quantities such as "4 hours and 23 minutes" cannot be represented.
 * <br>Localization treats this duration as "x days ago".
 */
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class IntegralDuration implements Comparable<IntegralDuration>, Localizable {
    /**
     * Constant for a duration of zero.
     */
    public static final IntegralDuration ZERO = fromUnit(ChronoUnit.FOREVER, 0);

    /**
     * The unit used to convey the length of this duration.
     */
    @Getter private final ChronoUnit unit;
    /**
     * The quantity of this duration's unit. May be zero or negative.
     */
    @Getter private final long value;

    /**
     * Creates a new IntegralDuration from a unit and its quantity.
     * @param unit A unit of time
     * @param value The quantity of time defined in terms of {@code unit}, may be zero or negative
     * @return The created IntegralDuration
     */
    public static IntegralDuration fromUnit(ChronoUnit unit, long value) {
        return new IntegralDuration(unit, value);
    }
    /**
     * Creates a new IntegralDuration from a duration and a range of acceptable units.
     * For example, "7 hours, 4 minutes 23 seconds" will be converted to "7 hours" if hours is in the given range,
     * "4 minutes" if minutes is in the given range, and so on.
     * If the range of units is exhausted, the result will be {@link #ZERO}.
     * @param duration The duration to convert, may be zero or negative
     * @param from The starting time unit
     * @param to The ending time unit
     * @return The created IntegralDuration
     * @throws IllegalArgumentException If the starting time unit comes after the ending time unit
     */
    public static IntegralDuration fromDuration(@NonNull Duration duration, ChronoUnit from, ChronoUnit to) {
        Preconditions.checkArgument(from.compareTo(to) <= 0,
                "The from unit %s was greater than the to unit %s", from, to);
        if (duration.isZero()) {
            return ZERO;
        }
        Deque<ChronoUnit> units = new LinkedList<>(EnumSet.range(from, to));
        Iterator<ChronoUnit> iter = units.descendingIterator();
        while (iter.hasNext()) {
            ChronoUnit unit = iter.next();
            long value = duration.dividedBy(unit.getDuration());
            if (Math.abs(value) >= 1) {
                return fromUnit(unit, value);
            }
        }
        return ZERO;
    }

    /**
     * @return True if this duration represents a duration of zero
     */
    public boolean isZero() {
        return value == 0;
    }

    public IntegralDuration toUnit(ChronoUnit unit) {
        if (isZero()) {
            return ZERO;
        }
        long value = toDuration().dividedBy(unit.getDuration());
        if (value == 0) {
            return ZERO;
        }
        return fromUnit(unit, value);
    }

    /**
     * @return This IntegralDuration as an equivalent Duration
     */
    public Duration toDuration() {
        if (isZero()) {
            return Duration.ZERO;
        }
        return unit.getDuration().multipliedBy(value);
    }

    public @NonNull String getTranslationKey() {
        if (isZero()) {
            return "general.justNow";
        }
        switch (unit) {
            case DAYS:
                return "general.daysAgo";
            case HOURS:
                return "general.hoursAgo";
            case MINUTES:
                return "general.minutesAgo";
            case SECONDS:
                return "general.secondsAgo";
        }
        if (unit.compareTo(ChronoUnit.DAYS) > 0) {
            return "general.daysAgo";
        }
        return "general.justNow";
    }

    public Object[] getTranslationArgs() {
        if (unit.compareTo(ChronoUnit.DAYS) > 0) {
            return new Object[]{toDuration().dividedBy(unit.getDuration())};
        }
        return new Object[]{value};
    }

    /**
     * Checks if this duration is longer or shorter than the other duration.
     * @param other The other duration to compare to
     * @return The comparator value, negative if less, positive if greater
     */
    public int compareTo(@NonNull IntegralDuration other) {
        return toDuration().compareTo(other.toDuration());
    }

    /**
     * @return A programmer-friendly representation of this duration
     */
    @Override
    public String toString() {
        return String.format("IntegralDuration(%d %s)", value, unit.toString().toLowerCase());
    }

}
