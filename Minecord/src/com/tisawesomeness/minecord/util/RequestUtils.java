package com.tisawesomeness.minecord.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public class RequestUtils {
	
	static final String charset = java.nio.charset.StandardCharsets.UTF_8.name();
	
	/**
	 * Performs an HTTP GET request.
	 * @param url The request URL.
	 * @param contentType The content type of the request.
	 * @return The response of the request in string form.
	 */
	public static String get(String url, String contentType) {
		if (!checkURL(url)) {return null;}
		try {
			
			URLConnection connection = new URL(url).openConnection();
			connection.setRequestProperty("Accept-Charset", charset);
			connection.setRequestProperty("Content-Type", contentType);
			InputStream response = connection.getInputStream();
			
			Scanner scanner = new Scanner(response);
			String responseBody = scanner.useDelimiter("\\A").next();
			scanner.close();
			return responseBody;
			
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	/**
	 * Performs an HTTP POST request.
	 * @param url The request URL.
	 * @param query The request payload, in string form.
	 * @param contentType The content type of the request.
	 * @return The response of the request in string form.
	 */
	public static String post(String url, String query, String contentType) {
		URLConnection connection;
		try {
			
			connection = new URL(url).openConnection();
			connection.setDoOutput(true);
			connection.setRequestProperty("Accept-Charset", charset);
			connection.setRequestProperty("Content-Type", contentType);
			
			OutputStream output = connection.getOutputStream();
			output.write(query.getBytes(charset));
			output.close();
			
			InputStream response = connection.getInputStream();

			String outputStr = null;
			if (charset != null) {
			    BufferedReader reader = new BufferedReader(new InputStreamReader(response, charset));
			    for (String line; (line = reader.readLine()) != null;) {
			        outputStr = line;
			    }
			    reader.close();
			}
			return outputStr;
			
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	/**
	 * Checks if a URL exists and can respond to an HTTP request.
	 * @param url The URL to check.
	 * @return True if the URL exists, false if it doesn't or an error occured.
	 */
	public static boolean checkURL(String url) {
        try {
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            con.setRequestMethod("HEAD");
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            return false;
        }
    }

}
