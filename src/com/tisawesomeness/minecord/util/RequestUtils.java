package com.tisawesomeness.minecord.util;

import net.dv8tion.jda.internal.utils.IOUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class RequestUtils {

    private static final String charset = StandardCharsets.UTF_8.name();
    private static final String jsonType = "application/json";
    private static final String plainType = "text/plain";
    private static final String browserAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11";
    private static final int TIMEOUT = 5000;

    private static String get(URLConnection conn) throws IOException {
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
    public static String get(String url) throws IOException {
        return get(url, null);
    }

    /**
     * Performs an HTTP GET request.
     * @param url The request URL.
     * @return The response of the request in string form.
     */
    public static String getPlain(String url) throws IOException {
        return getPlain(url, null);
    }

    /**
     * Performs an HTTP GET request.
     * @param url The request URL.
     * @param auth The content of the Authorization header.
     * @return The response of the request in string form.
     */
    public static String get(String url, String auth) throws IOException {
        return get(url, auth, true);
    }
    public static String get(String url, String auth, boolean skipCheck) throws IOException {
        if (skipCheck || checkURL(url)) {
            URLConnection conn = open(url, auth, jsonType);
            return get(conn);
        }
        throw new IOException("URL" + url + "does not exist");
    }

    /**
     * Performs an HTTP GET request.
     * @param url The request URL.
     * @param auth The content of the Authorization header.
     * @return The response of the request in string form.
     */
    public static String getPlain(String url, String auth) throws IOException {
        URLConnection conn = open(url, auth, plainType);
        return get(conn);
    }

    /**
     * Performs an HTTP POST request.
     * @param url The request URL.
     * @param query The request payload, in string form.
     * @return The response of the request in string form.
     */
    public static String post(String url, String query) throws IOException {
        return post(url, query, null);
    }

    /**
     * Performs an HTTP POST request.
     * @param url The request URL.
     * @param query The request payload, in string form.
     * @param auth The content of the Authorization header.
     * @return The response of the request in string form.
     */
    public static String post(String url, String query, String auth) throws IOException {
        URLConnection conn = open(url, auth, jsonType);
        ((HttpURLConnection) conn).setRequestMethod("POST");

        OutputStream output = conn.getOutputStream();
        output.write(query.getBytes(charset));
        output.close();

        return get(conn);
    }

    private static URLConnection open(String url, String auth, String contentType) throws IOException {
        URLConnection conn = new URL(url).openConnection();
        conn.setDoOutput(true);
        conn.setRequestProperty("Accept-Charset", charset);
        conn.setRequestProperty("Content-Type", contentType);
        conn.setConnectTimeout(TIMEOUT);
        conn.setReadTimeout(TIMEOUT);
        if (auth != null) {
            conn.setRequestProperty("Authorization", auth);
        }
        return conn;
    }

    /**
     * Downloads a file from a URL.
     * @param url The URL to download from.
     * @return The file contents.
     */
    public static byte[] download(String url) throws IOException {
        return download(new URL(url));
    }
    /**
     * Downloads a file from a URL.
     * @param url The URL to download from.
     * @return The file contents.
     */
    public static byte[] download(URL url) throws IOException {
        try (InputStream is = url.openStream()) {
            return IOUtil.readFully(is);
        }
    }

    /**
     * Checks if a URL exists and can respond to an HTTP request.
     * @param url The URL to check.
     * @return True if the URL exists, false if it doesn't or an error occurred.
     */
    public static boolean checkURL(String url) {
        return checkURL(url, false);
    }

    /**
     * Checks if a URL exists and can respond to an HTTP request.
     * @param url The URL to check.
     * @param fakeUserAgent If true, pretends to be a browser
     * @return True if the URL exists, false if it doesn't or an error occurred.
     */
    public static boolean checkURL(String url, boolean fakeUserAgent) {
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            con.setConnectTimeout(TIMEOUT);
            con.setReadTimeout(TIMEOUT);
            con.setInstanceFollowRedirects(false);
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

    /**
     * Checks if a URL exists and can respond to an HTTP request, but using GET instead of HEAD.
     * @param url The URL to check.
     * @return True if the URL exists, false if it doesn't or an error occurred.
     */
    public static boolean checkURLWithGet(String url) {
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            con.setConnectTimeout(TIMEOUT);
            con.setReadTimeout(TIMEOUT);
            con.setInstanceFollowRedirects(false);
            int code = con.getResponseCode();
            return code == HttpURLConnection.HTTP_OK;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Checks if a URL exists and can respond to a socket request.
     * @param url The URL to check.
     * @return True if the URL exists, false if it doesn't or an error occurred.
     */
    public static boolean checkWithSocket(String url) {
        try (Socket s = new Socket(url, 443)) {
            s.setSoTimeout(TIMEOUT);
            return s.isConnected();
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static JSONObject loadJSON(String path) throws IOException {
        return new JSONObject(new String(Files.readAllBytes(Paths.get(path))));
    }

    public static JSONArray loadJSONArray(String path) throws IOException {
        return new JSONArray(new String(Files.readAllBytes(Paths.get(path))));
    }

}
