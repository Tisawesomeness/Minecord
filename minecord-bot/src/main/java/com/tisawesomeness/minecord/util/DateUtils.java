package com.tisawesomeness.minecord.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

@Deprecated
public final class DateUtils {

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy @ hh:mm:ss a z").withZone(ZoneId.systemDefault());
    private static DateTimeFormatter formatterShort = DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneId.systemDefault());

    /**
     * Converts a string into a Calendar with the supplied date format.
     * @param string The string to convert.
     * @param format The SimpleDateFormat to use.
     * @return A Calendar with the date data.
     */
    public static Calendar parse(String string, String format) throws ParseException {
        SimpleDateFormat inputDateFormat = new SimpleDateFormat(format);
        Date date = inputDateFormat.parse(string);
        long time = date.getTime();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        return cal;
    }

    /**
     * Gets a string from a timestamp.
     * @param timestamp The long timestamp.
     * @return The String in m/d/y h:m:s format.
     */
    public static String getString(long timestamp) {
        return new SimpleDateFormat("MM/dd/yy hh:mm:ss a").format(new Date(timestamp));
    }

    /**
     * Gets the number of days, hours, minutes, and seconds since a specific timestamp.
     * @param startTime The start timestamp in miliseconds. Must be less than {@link System#currentTimeMillis()}.
     * @return A string in the format "?d?h?m?s".
     */
    public static String getDurationString(long startTime) {
        return getDurationString(startTime, System.currentTimeMillis());
    }
    /**
     * Gets the number of days, hours, minutes, and seconds since a specific timestamp.
     * @param startTime The start timestamp in miliseconds.
     * @param endTime The end timestamp in miliseconds. Must be greater than {@code startTime}.
     * @return A string in the format "?d?h?m?s".
     */
    public static String getDurationString(long startTime, long endTime) {
        if (startTime >= endTime) {
            throw new IllegalArgumentException("end time must be greater than start time");
        }
        long duration = (endTime - startTime) / 1000;
        if (duration == 0) {
            return "0s";
        }
        String durationString = "";

        if (duration >= 86400) {
            long days = duration / 86400;
            duration -= days * 86400;
            durationString += days + "d";
        }
        if (duration >= 3600) {
            long hours = duration / 3600;
            duration -= hours * 3600;
            durationString += hours + "h";
        }
        // Does not account for leap second, precision not needed
        if (duration >= 60) {
            long minutes = duration / 60;
            duration -= minutes * 60;
            durationString += minutes + "m";
        }
        if (duration > 0) {
            durationString += duration + "s";
        }
        return durationString;
    }

    /**
     * Returns a string with the time the bot took to boot up, in seconds, to 3 decimal places
     */
    public static String getBootTime(long bootTime) {
        return (double) bootTime / 1000 + "s";
    }

    /**
     * Generates a string with the formatted date and the amount of days since that date
     * @param time The timestamp to measure, must be in the past
     * @return A string formatted to "%s (%d days ago)"
     */
    public static String getDateAgo(long time) {
        return getDateAgo(Instant.ofEpochMilli(time).atOffset(ZoneOffset.UTC));
    }

    /**
     * Generates a string with the formatted date and the amount of days since that date
     * @param time The date to measure, must be in the past
     * @return A string formatted to "%s (%d days ago)"
     */
    public static String getDateAgo(OffsetDateTime time) {
        return String.format("%s (**%d** days ago)", time.format(formatter), time.until(OffsetDateTime.now(), ChronoUnit.DAYS));
    }

    /**
     * Generates a string with the formatted date and the amount of days since that date
     * @param time The timestamp to measure, must be in the past
     * @return A string formatted to "%s (%d days ago)"
     */
    public static String getDateAgoShort(long time) {
        return getDateAgoShort(Instant.ofEpochMilli(time).atOffset(ZoneOffset.UTC));
    }

    /**
     * Generates a string with the formatted date and the amount of days since that date
     * @param time The date to measure, must be in the past
     * @return A string formatted to "%s (%d days ago)"
     */
    public static String getDateAgoShort(OffsetDateTime time) {
        return String.format("%s (**%d** days ago)", time.format(formatterShort), time.until(OffsetDateTime.now(), ChronoUnit.DAYS));
    }

}
