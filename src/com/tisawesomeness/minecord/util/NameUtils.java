package com.tisawesomeness.minecord.util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class NameUtils {

	private static final Pattern UUID_PATTERN = Pattern.compile("[a-f0-9]{32}|[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}");
	private static final Pattern USERNAME_REGEX = Pattern.compile("[0-9A-Za-z_]{1,16}");
	private static final Pattern UUID_REPLACE_PATTERN = Pattern.compile("([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]+)");

	public static boolean isUuid(String str) {
		return UUID_PATTERN.matcher(str).matches();
	}
	public static boolean isUsername(String str) {
		return USERNAME_REGEX.matcher(str).matches();
	}

	/**
	 * Gets a playername from a UUID
	 * @param uuid A UUID with or without dashes
	 */
	public static String getName(String uuid) {
		String url = "https://api.mojang.com/user/profiles/" + uuid + "/names";
		String request = RequestUtils.get(url);
		if (request == null) {
			return null;
		}
		JSONArray names = new JSONArray(request);
		return names.getJSONObject(names.length() - 1).getString("name");
	}
	
	/**
	 * Gets a UUID from a playername
	 */
	public static String getUUID(String playername) {
		return getUUIDInternal(playername);
	}
	
	private static String getUUIDInternal(String query) {
		String url = "https://api.mojang.com/users/profiles/minecraft/" + query;
		String request = RequestUtils.get(url);
		if (request == null) {
			return null;
		}

		JSONObject response = new JSONObject(request);
		if (response.has("error")) {
			String error = response.getString("error");
			String errorMessage = response.getString("errorMessage");
			return error + ": " + errorMessage;
		}
		
		return response.getString("id");
	}
	
	/**
	 * Adds dashes to a UUID
	 */
	public static String formatUUID(String uuid) {
		return UUID_REPLACE_PATTERN.matcher(uuid).replaceFirst("$1-$2-$3-$4-$5");
	}

}
