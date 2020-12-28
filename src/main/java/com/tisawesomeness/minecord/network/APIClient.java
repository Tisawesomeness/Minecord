package com.tisawesomeness.minecord.network;

import lombok.Getter;
import lombok.NonNull;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * A simplified HTTP client that can be used to communicate with web JSON APIs.
 */
public class APIClient {

    private static final int MAX_REQUESTS_PER_HOST = 25;
    /**
     * The builder used to construct a {@link OkHttpClient} instance
     */
    @Getter private final OkHttpClient.Builder httpClientBuilder;
    private final Dispatcher dispatcher;
    private final ConnectionPool connectionPool;

    /**
     * Creates a new API client with the default settings.
     */
    public APIClient() {
        // client is set to JDA defaults
        dispatcher = new Dispatcher();
        dispatcher.setMaxRequestsPerHost(MAX_REQUESTS_PER_HOST);
        connectionPool = new ConnectionPool(5, 10, TimeUnit.SECONDS);
        httpClientBuilder = new OkHttpClient.Builder()
                .connectionPool(connectionPool)
                .dispatcher(dispatcher);
    }

    /**
     * Performs a GET request.
     * @param url The URL to send a GET request to
     * @return The response of the request, which may be successful or unsuccessful
     * @throws IOException If an I/O error occurs
     */
    public @NonNull Response get(@NonNull URL url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/json")
                .build();
        OkHttpClient client = httpClientBuilder.build();
        return Objects.requireNonNull(client.newCall(request).execute());
    }

    public int getQueuedCallsCount() {
        return dispatcher.queuedCallsCount();
    }
    public int getRunningCallsCount() {
        return dispatcher.runningCallsCount();
    }
    public int getIdleConnectionCount() {
        return connectionPool.idleConnectionCount();
    }
    public int getConnectionCount() {
        return connectionPool.connectionCount();
    }

}
