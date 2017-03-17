package com.tisawesomeness.minecord.util;

import org.json.JSONObject;

public class NameUtils {
	
	public static final String uuidRegex = "[a-f0-9]{32}|[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}";
	
	/**
	 * 
	 * @param playername
	 * @return
	 */
	public static String getUUID(String playername) {
		return getUUIDInternal(playername);
	}
	
	/**
	 * 
	 * @param playername
	 * @param timestamp
	 * @return
	 */
	public static String getUUID(String playername, long timestamp) {
		return getUUIDInternal(playername + "?at=" + timestamp);
	}
	
	private static String getUUIDInternal(String query) {
		String url = "https://api.mojang.com/users/profiles/minecraft/" + query;
		String request = RequestUtils.get(url, "application/json");
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

}
