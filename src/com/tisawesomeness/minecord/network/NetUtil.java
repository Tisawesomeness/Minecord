package com.tisawesomeness.minecord.network;

import lombok.NonNull;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

public final class NetUtil {
    private NetUtil() {}

    private static final int LONGEST_DEBUGGABLE_ERROR = 256;

    /**
     * Attempts to parse an IPv4 address from a string.
     * @param ip The string to parse, in the form "1.2.3.4"
     * @return The parsed address, or empty if invalid
     */
    public static Optional<Inet4Address> getAddress(String ip) {
        String[] parts = ip.split("\\.");
        if (parts.length != 4) {
            return Optional.empty();
        }
        try {
            // get using the byte array since it doesn't resolve
            return Optional.of((Inet4Address) InetAddress.getByAddress(new byte[]{
                    (byte) Integer.parseInt(parts[0]),
                    (byte) Integer.parseInt(parts[1]),
                    (byte) Integer.parseInt(parts[2]),
                    (byte) Integer.parseInt(parts[3])
            }));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        } catch (UnknownHostException ex) {
            throw new AssertionError("impossible, array is always length 4");
        }
    }

    /**
     * Throws a formatted IOE if the response is not successful.
     * The message contains the response code and the body error message if it exists.
     * @param response the response
     * @param apiName the API name to use in the error message
     * @throws IOException if the response is not successful
     */
    public static void throwIfError(@NonNull Response response, @NonNull String apiName) throws IOException {
        if (!response.isSuccessful()) {
            ResponseBody body = response.body();
            String error = String.format("%s error from %s: %s", response.code(), apiName, response.message());
            if (body == null) {
                throw new IOException(error);
            }
            if (body.contentLength() > LONGEST_DEBUGGABLE_ERROR) {
                throw new IOException(error + " | " + body.string().substring(0, LONGEST_DEBUGGABLE_ERROR) + "...");
            }
            throw new IOException(error + " | " + body.string());
        }
    }

}
