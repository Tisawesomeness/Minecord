package com.tisawesomeness.minecord.util;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

public final class TimeUtils {
    private TimeUtils() {}

    /**
     * Formats a time into a localized, human-readable string
     * @param time A point in time
     * @param locale The locale to use for localization
     * @return A formatted string with the time, date, and timezone
     */
    public static @NonNull String format(@NonNull TemporalAccessor time, @NonNull Locale locale, Format format) {
        return format(format.formatter, time, locale);
    }
    private static @NonNull String format(@NonNull DateTimeFormatter formatter, @NonNull TemporalAccessor time,
                                 @NonNull Locale locale) {
        return formatter
                .withLocale(locale)
                .withZone(ZoneId.systemDefault())
                .format(time);
    }

    /**
     * An enum of standard localized date/time formats.
     */
    @RequiredArgsConstructor
    public enum Format {
        /**
         * A short numeric date/time with timezone.
         */
        DATETIME(new DateTimeFormatterBuilder()
                .appendLocalized(FormatStyle.SHORT, FormatStyle.SHORT)
                .appendPattern(" z")
                .toFormatter()),
        /**
         * A medium-length numeric date.
         */
        DATE(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));

        private final DateTimeFormatter formatter;
    }

}
