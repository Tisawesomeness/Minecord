package com.tisawesomeness.minecord.util.network;

import lombok.NonNull;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Utility class for working with URLs
 */
public final class URLUtils {
    private URLUtils() {}

    /**
     * Creates a URL from a string without throwing a checked exception. <b>Verify that all strings passed to this
     * method are valid URLs.</b>
     * @param str The URL in string form
     * @return A URL
     * @throws AssertionError if the string is not a valid URL
     */
    public static URL createUrl(String str) {
        try {
            return new URL(str);
        } catch (MalformedURLException ex) {
            throw new AssertionError(ex);
        }
    }

    /**
     * Changes a string URL from HTTP to HTTPS if it begins with "http:"
     * @param link A URL as a string (though any string will work)
     * @return The string changed to HTTPS, or the same string unmodified
     */
    public static @NonNull String httpToHttps(@NonNull String link) {
        if (link.startsWith("http:")) {
            return "https" + link.substring(4);
        }
        return link;
    }

    public static @NonNull String encode(@NonNull String str) {
        try {
            return URLEncoder.encode(str, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            throw new AssertionError(ex);
        }
    }

}
