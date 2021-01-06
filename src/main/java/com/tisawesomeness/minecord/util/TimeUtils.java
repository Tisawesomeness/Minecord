package com.tisawesomeness.minecord.util;

import com.tisawesomeness.minecord.Lang;
import com.tisawesomeness.minecord.util.discord.LocalizedMarkdownBuilder;
import com.tisawesomeness.minecord.util.type.IntegralDuration;

import lombok.NonNull;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;
import java.util.Optional;

public final class TimeUtils {
    private TimeUtils() {}

    private static final DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
            .appendLocalized(FormatStyle.SHORT, FormatStyle.SHORT)
            .appendPattern(" z")
            .toFormatter();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);

    /**
     * Formats a date/time into a localized, human-readable string with timezone.
     * @param datetime A point in time
     * @param locale The locale to use for localization
     * @return A formatted string with the time, date, and timezone
     */
    public static @NonNull String formatDateTime(@NonNull TemporalAccessor datetime, @NonNull Locale locale) {
        return format(DATE_TIME_FORMATTER, datetime, locale);
    }
    /**
     * Formats a date into a localized, human-readable string.
     * @param date A point in time
     * @param locale The locale to use for localization
     * @return A formatted string with only the date
     */
    public static @NonNull String formatDate(@NonNull TemporalAccessor date, @NonNull Locale locale) {
        return format(DATE_FORMATTER, date, locale);
    }
    private static @NonNull String format(@NonNull DateTimeFormatter formatter, @NonNull TemporalAccessor time,
                                 @NonNull Locale locale) {
        return formatter
                .withLocale(locale)
                .withZone(ZoneId.systemDefault())
                .format(time);
    }

    /**
     * Localizes an IntegralDation to a string such as "x days ago". The longest unit that will be used is DAYS
     * and the shortest unit is SECONDS. If the duration is shorter than a second, the equivalent of "just now"
     * is returned.
     * @param duration The duration to localize
     * @param lang The language
     * @return A localized, human-readable string
     */
    public static @NonNull String localizeIntegralDuration(@NonNull IntegralDuration duration, Lang lang) {
        return getDurationKey(duration)
                .map(key -> lang.i18nf(key, duration.getValue()))
                .orElse(localizedNoDuration(lang));
    }
    public static @NonNull Optional<LocalizedMarkdownBuilder> localizeIntegralDurationBuilder(
            @NonNull IntegralDuration duration, Lang lang) {
        return getDurationKey(duration)
                .map(key -> lang.i18nm(key, duration.getValue()));
    }
    public static @NonNull String localizedNoDuration(Lang lang) {
        return lang.i18n("general.justNow");
    }
    private static Optional<String> getDurationKey(@NonNull IntegralDuration duration) {
        switch (duration.getUnit()) {
            case DAYS:
                return Optional.of("general.daysAgo");
            case HOURS:
                return Optional.of("general.hoursAgo");
            case MINUTES:
                return Optional.of("general.minutesAgo");
            case SECONDS:
                return Optional.of("general.secondsAgo");
        }
        return Optional.empty();
    }

}
