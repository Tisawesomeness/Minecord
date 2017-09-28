package com.tisawesomeness.minecord.util;

import org.json.JSONArray;
import org.json.JSONObject;

public class NameUtils {
	
	public static final String uuidRegex = "[a-f0-9]{32}|[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}";
	public static final String playerRegex = "[0-9A-Za-z_]{1,16}";
	public static final String discordRegex = ".{1,32}";
	
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
	
	/**
	 * Gets a UUID from a playername and Unix timestamp
	 */
	public static String getUUID(String playername, long timestamp) {
		return getUUIDInternal(playername + "?at=" + timestamp);
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
		return uuid.replaceFirst(
			"([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]+)",
			"$1-$2-$3-$4-$5"
		);
	}

}
