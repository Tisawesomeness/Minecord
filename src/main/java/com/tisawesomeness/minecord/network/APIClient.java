package com.tisawesomeness.minecord.network;

import com.tisawesomeness.minecord.config.config.HttpConfig;

import lombok.Getter;
import lombok.NonNull;
import okhttp3.*;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * A simplified HTTP client that can be used to communicate with web JSON APIs.
 */
public class APIClient {

    /**
     * The builder used to construct a {@link OkHttpClient} instance
     */
    @Getter private final OkHttpClient.Builder httpClientBuilder;
    private final Dispatcher dispatcher;
    private final ConnectionPool connectionPool;

    /**
     * Creates a new API client with the default settings.
     */
    public APIClient(HttpConfig config) {
        // client is set to JDA defaults
        dispatcher = new Dispatcher();
        dispatcher.setMaxRequestsPerHost(config.getMaxRequestsPerHost());
        connectionPool = new ConnectionPool(config.getMaxIdleConnections(),
                config.getKeepAlive(), TimeUnit.MILLISECONDS);
        httpClientBuilder = new OkHttpClient.Builder()
                .connectionPool(connectionPool)
                .dispatcher(dispatcher);
    }

    /**
     * Performs a HEAD request.
     * @param url The URL to send a HEAD request to
     * @return The response of the request, which may be successful or unsuccessful
     * @throws IOException If an I/O error occurs
     */
    public @NonNull Response head(@NonNull URL url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .head()
                .build();
        return dispatch(request);
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
        return dispatch(request);
    }

    private @NonNull Response dispatch(@NonNull Request request) throws IOException {
        OkHttpClient client = httpClientBuilder.build();
        return client.newCall(request).execute();
    }

    /**
     * Checks if a URL exists and is responsive.
     * @param url The URL to request
     * @return Whether the URL is responsive
     * @throws IOException If an I/O error occurs
     */
    public boolean exists(@NonNull URL url) throws IOException {
        return head(url).code() == StatusCodes.OK;
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

    /**
     * Closes this client and all current connections.
     */
    public void close() {
        connectionPool.evictAll();
        dispatcher.executorService().shutdown();
    }

}
