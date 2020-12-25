package com.tisawesomeness.minecord.util.network;

import java.net.MalformedURLException;
import java.net.URL;

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

}
