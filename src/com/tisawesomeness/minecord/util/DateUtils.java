package com.tisawesomeness.minecord.util;

import com.tisawesomeness.minecord.Bot;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateUtils {

	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy @ hh:mm:ss a z").withZone(ZoneId.systemDefault());
	private static final DateTimeFormatter formatterShort = DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneId.systemDefault());
	
	public static String getUptime() {
		long uptimeRaw = System.currentTimeMillis() - Bot.birth;
		uptimeRaw = Math.floorDiv(uptimeRaw, 1000L);
		String uptime = "";
		
		if (uptimeRaw >= 86400) {
			long days = Math.floorDiv(uptimeRaw, 86400L);
			uptime = days + "d";
			uptimeRaw = uptimeRaw - days * 86400;
		}
		if (uptimeRaw >= 3600) {
			long hours = Math.floorDiv(uptimeRaw, 3600L);
			uptime = uptime + hours + "h";
			uptimeRaw = uptimeRaw - hours * 3600;
		}
		if (uptimeRaw >= 60) {
			long minutes = Math.floorDiv(uptimeRaw, 60L);
			uptime = uptime + minutes + "m";
			uptimeRaw = uptimeRaw - minutes * 60;
		}
		if (uptimeRaw > 0) {
			uptime = uptime + uptimeRaw + "s";
		}
		if (uptime.isEmpty()) {
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
