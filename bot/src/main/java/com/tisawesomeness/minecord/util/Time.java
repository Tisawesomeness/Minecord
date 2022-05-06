package com.tisawesomeness.minecord.util;

import lombok.NonNull;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public final class Time {
    private Time() {}

    /**
     * Formats a duration in milliseconds into a localized string with the same time in seconds.
     * Example: {@code 2752 (ms) --> 2.752 (s)} for English. Always uses three decimal places.
     * @param millis The number of milliseconds
     * @param locale The locale to use for localization
     * @return A string with a number of seconds to three decimal places
     */
    public static @NonNull String formatMillisAsSeconds(long millis, @NonNull Locale locale) {
        NumberFormat format = DecimalFormat.getInstance(locale);
        format.setRoundingMode(RoundingMode.CEILING);
        format.setMaximumIntegerDigits(3);
        format.setMinimumFractionDigits(3);
        return format.format(millis / 1000.0);
    }

}
