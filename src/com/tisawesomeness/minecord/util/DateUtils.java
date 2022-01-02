package com.tisawesomeness.minecord.util;

import com.tisawesomeness.minecord.Bot;

public class DateUtils {

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

}
