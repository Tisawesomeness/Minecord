package com.tisawesomeness.minecord.util;

import lombok.NonNull;
import lombok.SneakyThrows;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Utility class for working with URLs
 */
public final class URLs {
    private URLs() {}

    /**
     * Creates a URL from a string without throwing a checked exception. <b>Verify that all strings passed to this
     * method are valid URLs.</b>
     * @param str The URL in string form
     * @return A URL
     * @throws MalformedURLException sneaky
     */
    @SneakyThrows(MalformedURLException.class)
    public static @NonNull URL createUrl(@NonNull String str) {
        return new URL(str);
    }
    /**
     * Creates a URL from a URI without throwing a checked exception. <b>Verify that all URIs passed to this
     * method are valid URLs.</b>
     * @param uri The URL as a URI
     * @return A URL
     * @throws MalformedURLException sneaky
     */
    @SneakyThrows(MalformedURLException.class)
    public static @NonNull URL createUrl(@NonNull URI uri) {
        return uri.toURL();
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

    @SneakyThrows(UnsupportedEncodingException.class) // Not possible
    public static @NonNull String encode(@NonNull String str) {
        return URLEncoder.encode(str, StandardCharsets.UTF_8.toString());
    }

}
