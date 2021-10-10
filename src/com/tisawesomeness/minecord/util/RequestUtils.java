package com.tisawesomeness.minecord.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;
import org.discordbots.api.client.DiscordBotListAPI;
import org.json.JSONArray;
import org.json.JSONObject;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Config;

import net.dv8tion.jda.api.JDA;

public class RequestUtils {
	
	private static final String charset = StandardCharsets.UTF_8.name();
	private static final String jsonType = "application/json";
	private static final String plainType = "text/plain";
	private static final String browserAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11";
	public static DiscordBotListAPI api = null;
	
	private static String get(URLConnection conn, String type) throws IOException {
		InputStream response = conn.getInputStream();
		Scanner scanner = new Scanner(response);
		String responseBody = scanner.hasNext() ? scanner.useDelimiter("\\A").next() : "";
		scanner.close();
		return responseBody;
	}
	
	/**
	 * Performs an HTTP GET request.
	 * @param url The request URL.
	 * @return The response of the request in string form.
	 */
	public static String get(String url) {
		return get(url, null);
	}

	/**
	 * Performs an HTTP GET request.
	 * @param url The request URL.
	 * @return The response of the request in string form.
	 */
	public static String getPlain(String url) {
		return getPlain(url, null);
	}
	
	/**
	 * Performs an HTTP GET request.
	 * @param url The request URL.
	 * @param auth The content of the Authorization header.
	 * @return The response of the request in string form.
	 */
	public static String get(String url, String auth) {
		return get(url, auth, false);
	}
	public static String get(String url, String auth, boolean skipCheck) {
		if (skipCheck || checkURL(url)) {
			try {
				URLConnection conn = open(url, auth, jsonType);
				return get(conn, jsonType);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * Performs an HTTP GET request.
	 * @param url The request URL.
	 * @param auth The content of the Authorization header.
	 * @return The response of the request in string form.
	 */
	public static String getPlain(String url, String auth) {
		try {
			URLConnection conn = open(url, auth, plainType);
			return get(conn, plainType);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Performs an HTTP POST request.
	 * @param url The request URL.
	 * @param query The request payload, in string form.
	 * @return The response of the request in string form.
	 */
	public static String post(String url, String query) {
		return post(url, query, null);
	}
	
	/**
	 * Performs an HTTP POST request.
	 * @param url The request URL.
	 * @param query The request payload, in string form.
	 * @param auth The content of the Authorization header.
	 * @return The response of the request in string form.
	 */
	public static String post(String url, String query, String auth) {
		try {
			URLConnection conn = open(url, auth, jsonType);
			
			OutputStream output = conn.getOutputStream();
			output.write(query.getBytes(charset));
			output.close();
			
			return get(conn, jsonType);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	private static URLConnection open(String url, String auth, String contentType) throws MalformedURLException, IOException {
		URLConnection conn = new URL(url).openConnection();
		conn.setDoOutput(true);
		conn.setRequestProperty("Accept-Charset", charset);
		conn.setRequestProperty("Content-Type", contentType);
		if (auth != null) conn.setRequestProperty("Authorization", auth);
		return conn;
	}
	
	/**
	 * Checks if a URL exists and can respond to an HTTP request.
	 * @param url The URL to check.
	 * @return True if the URL exists, false if it doesn't or an error occured.
	 */
	public static boolean checkURL(String url) {
		return checkURL(url, false);
	}
	
	/**
	 * Checks if a URL exists and can respond to an HTTP request.
	 * @param url The URL to check.
	 * @param fakeUserAgent If true, pretends to be a browser
	 * @return True if the URL exists, false if it doesn't or an error occured.
	 */
	public static boolean checkURL(String url, boolean fakeUserAgent) {
		try {
			HttpURLConnection.setFollowRedirects(false);
			HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
			con.setRequestMethod("HEAD");
			if (fakeUserAgent) {
				con.setRequestProperty("User-Agent", browserAgent);
			}
			int code = con.getResponseCode();
			return code == HttpURLConnection.HTTP_OK;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
	
	public static InputStream downloadImage(String url) throws IOException {
		BufferedImage image = ImageIO.read(new URL(url));
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ImageIO.write(image, "png", os);
		return new ByteArrayInputStream(os.toByteArray());
	}
	
	/**
	 * Sends the guild count
	 */
	public static void sendGuilds() {
		if (Config.getSendServerCount()) {
			List<Integer> serverCounts = new ArrayList<Integer>();
			for (JDA jda : Bot.shardManager.getShards()) serverCounts.add(jda.getGuilds().size());
			api.setStats(serverCounts);
		}
	}

	public static JSONObject loadJSON(String path) throws IOException {
		return new JSONObject(new String(Files.readAllBytes(Paths.get(path))));
	}

	public static JSONArray loadJSONArray(String path) throws IOException {
		return new JSONArray(new String(Files.readAllBytes(Paths.get(path))));
	}
	
	// Converts a string to SHA1 (from http://www.sha1-online.com/sha1-java/)
	public static String sha1(String str) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA1");
			byte[] result = md.digest(str.getBytes());
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < result.length; i++) {
				sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
			return null;
		}
	}

}
