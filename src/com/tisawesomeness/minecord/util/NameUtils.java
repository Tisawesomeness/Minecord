package com.tisawesomeness.minecord.util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class NameUtils {

	// '#' existed before but breaks the API
	// ':' and ')' sometimes break the API, but since they were included in only one known name "1cream#:)"
	// and Mojang's validation criteria is not clear, these characters are not supported
	private static final int MAX_LENGTH = 25;
	private static final Pattern USERNAME_REGEX = Pattern.compile(String.format("^[\\w !@$\\-.?]{1,%d}$", MAX_LENGTH));
	private static final Pattern EMAIL_CASE_PATTERN = Pattern.compile("^[0-9A-Za-z_\\-.*@]+$");

	private static final Pattern UUID_PATTERN = Pattern.compile("[a-f0-9]{32}|[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}");
	private static final Pattern UUID_REPLACE_PATTERN = Pattern.compile("([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]+)");

	private static final URL BASE_URL = createUrl();

	private static URL createUrl() {
		try {
			return new URL("https://api.mojang.com/users/profiles/minecraft/");
		} catch (MalformedURLException ex) {
			throw new AssertionError(ex);
		}
	}

	public static boolean isUuid(String str) {
		return UUID_PATTERN.matcher(str).matches();
	}
	public static boolean isUsername(String str) {
		if (".".equals(str) || "..".equals(str)) {
			return false;
		}
		return USERNAME_REGEX.matcher(str).matches();
	}

	/**
	 * Gets a playername from a UUID
	 * @param uuid A UUID with or without dashes
	 */
	public static String getName(String uuid) throws IOException {
		String url = "https://api.mojang.com/user/profiles/" + uuid + "/names";
		String request = RequestUtils.get(url);
		if (request.isEmpty()) {
			return null;
		}
		JSONArray names = new JSONArray(request);
		return names.getJSONObject(names.length() - 1).getString("name");
	}

	/**
	 * Gets a UUID from a playername
	 */
	public static String getUUID(String playername) throws IOException {
		String url = getUUIDUrl(playername).toString();
		String request = RequestUtils.get(url);
		if (request.isEmpty()) {
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

	private static URL getUUIDUrl(String username) {
		try {
			// Email usernames ("sample@email.com") only work when the @ is unescaped
			// This special case skips URL encoding if all characters (excluding @) are the same after encoding
			if (username.contains("@") && EMAIL_CASE_PATTERN.matcher(username).matches()) {
				return new URL(BASE_URL, username);
			}
			// Otherwise, encoding is necessary to clean out naughty characters
			return new URL(BASE_URL, encode(username));
		} catch (MalformedURLException ex) {
			throw new AssertionError(ex);
		}
	}
	private static String encode(String str) {
		try {
			return URLEncoder.encode(str, StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException ex) {
			throw new AssertionError(ex);
		}
	}

	/**
	 * Adds dashes to a UUID
	 */
	public static String formatUUID(String uuid) {
		return UUID_REPLACE_PATTERN.matcher(uuid).replaceFirst("$1-$2-$3-$4-$5");
	}

}
