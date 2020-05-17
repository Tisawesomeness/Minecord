package com.tisawesomeness.minecord.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.tisawesomeness.minecord.Bot;

public class DateUtils {
	
	private static final String timestampRegex = "^[0-9]{4,}$";
	private static final String dateRegex = "^(0?[1-9]|1[012])/(0?[1-9]|[12][0-9]|3[01])$";
	private static final String shortDateRegex = "^(?:(?:(?:0?[13578]|1[02])(\\/|-|\\.)31)\\1|(?:(?:0?[1,3-9]|1[0-2])(\\/|-|\\.)(?:29|30)\\2))(?:\\d{2})$|^(?:0?2(\\/|-|\\.)29\\3(?:(?:(?:0[48]|[2468][048]|[13579][26])|(?:00))))$|^(?:(?:0?[1-9])|(?:1[0-2]))(\\/|-|\\.)(?:0?[1-9]|1\\d|2[0-8])\\4(?:\\d{2})$";
	private static final String fullDateRegex = "^(?:(?:(?:0?[13578]|1[02])(\\/|-|\\.)31)\\1|(?:(?:0?[1,3-9]|1[0-2])(\\/|-|\\.)(?:29|30)\\2))(?:(?:1[6-9]|[2-9]\\d)\\d{2})$|^(?:0?2(\\/|-|\\.)29\\3(?:(?:(?:1[6-9]|[2-9]\\d)(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:(?:0?[1-9])|(?:1[0-2]))(\\/|-|\\.)(?:0?[1-9]|1\\d|2[0-8])\\4(?:(?:1[6-9]|[2-9]\\d)\\d{2})$";
	private static final String timeRegex = "^(0?[1-9]|1[012]):[0-5][0-9]$";
	private static final String timeRegex24 = "^(0?[1-9]|1[0-9]|2[0-4]):[0-5][0-9]$";
	private static final String fullTimeRegex = "^(0?[1-9]|1[012]):[0-5][0-9]:[0-5][0-9]$";
	private static final String fullTimeRegex24 = "^(0?[1-9]|1[0-9]|2[0-4]):[0-5][0-9]:[0-5][0-9]$";
	private static final String timeZoneRegex = "^(?:Z|[+-](?:2[0-3]|[01][0-9]):[0-5][0-9])$";
	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a z").withZone(ZoneId.systemDefault());
	
	/**
	 * Parses a list of date-related strings to build a date, then a timestamp.
	 * @param args The list of arguments
	 * @return A timestamp in long format.
	 */
	public static long getTimestamp(String args[]) {
		
		int year = -1;
		int month = -1;
		int day = -1;
		int hour = -1;
		int minute = -1;
		int second = -1;
		boolean hours24 = false;
		int ampm = -1;
		TimeZone timezone = Calendar.getInstance().getTimeZone();
		
		for (String arg : args) {
			if (arg == null) {continue;}
			if (arg.matches(timestampRegex)) {
				return Long.valueOf(arg);
			}
			if (arg.matches(fullDateRegex)) {
				Calendar cal = parseUnstable(arg, "MM/dd/yyyy");
				month = cal.get(Calendar.MONTH) + 1;
				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);
				continue;
			}
			if (arg.matches(shortDateRegex)) {
				Calendar cal = parseUnstable(arg, "MM/dd/yy");
				month = cal.get(Calendar.MONTH) + 1;
				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);
				continue;
			}
			if (arg.matches(dateRegex)) {
				Calendar cal = parseUnstable(arg, "MM/dd");
				month = cal.get(Calendar.MONTH) + 1;
				day = cal.get(Calendar.DAY_OF_MONTH);
				continue;
			}
			if (arg.matches(fullTimeRegex24)) {
				Calendar cal = parseUnstable(arg, "kk:mm:ss");
				hour = cal.get(Calendar.HOUR);
				minute = cal.get(Calendar.MINUTE);
				second = cal.get(Calendar.SECOND);
				hours24 = true;
				continue;
			}
			if (arg.matches(fullTimeRegex)) {
				Calendar cal = parseUnstable(arg, "hh:mm:ss");
				hour = cal.get(Calendar.HOUR);
				minute = cal.get(Calendar.MINUTE);
				second = cal.get(Calendar.SECOND);
				continue;
			}
			if (arg.matches(timeRegex24)) {
				Calendar cal = parseUnstable(arg, "kk:mm");
				hour = cal.get(Calendar.HOUR);
				minute = cal.get(Calendar.MINUTE);
				hours24 = true;
				continue;
			}
			if (arg.matches(timeRegex)) {
				Calendar cal = parseUnstable(arg, "hh:mm");
				hour = cal.get(Calendar.HOUR);
				minute = cal.get(Calendar.MINUTE);
				continue;
			}
			if (arg.equalsIgnoreCase("AM") || arg.equalsIgnoreCase("PM")) {
				Calendar cal = parseUnstable(arg, "a");
				ampm = cal.get(Calendar.AM_PM);
				continue;
			}
			if (arg.matches(timeZoneRegex)) {
				Calendar cal = parseUnstable(arg, "X");
				timezone = cal.getTimeZone();
				continue;
			}
			Calendar cal = parseUnstable(arg, "Z");
			if (cal == null) {
				MessageUtils.log("Invalid date argument: " + arg);
			} else {
				timezone = cal.getTimeZone();
			}
			
		}
		
		if (minute == -1 && day == -1) {
			return -1;
		} else {
			if (minute == -1) {
				hour = 0;
				minute = 0;
			}
			if (second == -1) {
				second = 0;
			}
			if (day == -1) {
				month = Calendar.getInstance().get(Calendar.MONTH);
				day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
			}
			if (year == -1) {
				year = Calendar.getInstance().get(Calendar.YEAR);
			}
			if (ampm == -1) {
				ampm = Calendar.AM;
			}
		}
		
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, day, hour, minute, second);
		cal.setTimeZone(timezone);
		if (!hours24) {
			cal.set(Calendar.AM_PM, ampm);
		}
		
		return cal.getTime().getTime();
	}
	
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
	 * Converts a string into a Calendar with the supplied date format.
	 * This method does not handle errors.
	 * @param string The string to convert.
	 * @param format The SimpleDateFormat to use.
	 * @return A Calendar with the date data.
	 */
	private static Calendar parseUnstable(String string, String format) {
		SimpleDateFormat inputDateFormat = new SimpleDateFormat(format); //MM/dd/yyyy hh:mm:ss a
		Date date = null;
		try {
			date = inputDateFormat.parse(string);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
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
	
	public static String getUptime() {
		long uptimeRaw = System.currentTimeMillis() - Bot.birth;
		uptimeRaw = Math.floorDiv(uptimeRaw, 1000);
		String uptime = "";
		
		if (uptimeRaw >= 86400) {
			long days = Math.floorDiv(uptimeRaw, 86400);
			uptime = days + "d";
			uptimeRaw = uptimeRaw - days * 86400;
		}
		if (uptimeRaw >= 3600) {
			long hours = Math.floorDiv(uptimeRaw, 3600);
			uptime = uptime + hours + "h";
			uptimeRaw = uptimeRaw - hours * 3600;
		}
		if (uptimeRaw >= 60) {
			long minutes = Math.floorDiv(uptimeRaw, 60);
			uptime = uptime + minutes + "m";
			uptimeRaw = uptimeRaw - minutes * 60;
		}
		if (uptimeRaw > 0) {
			uptime = uptime + uptimeRaw + "s";
		}
		if ("".equals(uptime)) {
			uptime = "0s";
		}
		
		return uptime;
	}

	/**
	 * Returns a string with the time the bot took to boot up, in seconds, to 3 decimal places
	 */
	public static String getBootTime() {
		return (double) Bot.bootTime / 1000 + "s";
	}

	/**
	 * Generates a string with the formatted date and the amount of days since that date
	 * @param time The date to measure, must be in the past
	 * @return A string formatted to "%s (%d days ago)"
	 */
	public static String getDateAgo(OffsetDateTime time) {
		return String.format("%s (**%d** days ago)", time.format(formatter), time.until(OffsetDateTime.now(), ChronoUnit.DAYS));
	}

}
