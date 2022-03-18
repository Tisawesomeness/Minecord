package com.tisawesomeness.minecord.network;

import lombok.NonNull;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;

public final class NetUtil {
    private NetUtil() {}

    private static final int LONGEST_DEBUGGABLE_ERROR = 256;

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
