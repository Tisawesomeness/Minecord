package com.tisawesomeness.minecord.network;

import lombok.NonNull;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import java.io.IOException;
import java.net.URL;

public interface APIClient {

    OkHttpClient.Builder getHttpClientBuilder();

    /**
     * Performs a HEAD request.
     *
     * @param url The URL to send a HEAD request to
     * @return The response of the request, which may be successful or unsuccessful
     * @throws IOException If an I/O error occurs
     */
    @NonNull Response head(@NonNull URL url) throws IOException;

    /**
     * Performs a GET request.
     *
     * @param url The URL to send a GET request to
     * @return The response of the request, which may be successful or unsuccessful
     * @throws IOException If an I/O error occurs
     */
    @NonNull Response get(@NonNull URL url) throws IOException;

    /**
     * Checks if a URL exists and is responsive.
     *
     * @param url The URL to request
     * @return Whether the URL is responsive
     * @throws IOException If an I/O error occurs
     */
    boolean exists(@NonNull URL url) throws IOException;

}
