package com.tisawesomeness.minecord.network;

import lombok.Getter;
import lombok.NonNull;
import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * A simplified HTTP client that can be used to communicate with web JSON APIs.
 */
public class OkAPIClient implements APIClient {

    /**
     * The builder used to construct a {@link OkHttpClient} instance
     */
    @Getter private final OkHttpClient.Builder httpClientBuilder;

    private final Dispatcher dispatcher;
    private final ConnectionPool connectionPool;

    /**
     * Creates a new API client with the default settings.
     */
    public OkAPIClient() {
        // client is set to JDA defaults
        dispatcher = new Dispatcher();
        dispatcher.setMaxRequestsPerHost(25);
        connectionPool = new ConnectionPool(5, 10000, TimeUnit.MILLISECONDS);
        httpClientBuilder = new OkHttpClient.Builder()
                .connectionPool(connectionPool)
                .dispatcher(dispatcher);
    }

    @Override
    public @NonNull Response head(@NonNull URL url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .head()
                .build();
        return dispatch(request);
    }

    @Override
    public @NonNull Response get(@NonNull URL url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/json")
                .build();
        return dispatch(request);
    }

    @Override
    public @NonNull Response post(@NonNull URL url, @NonNull JSONObject payload, @NonNull String auth) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MediaType.get("application/json"), payload.toString()))
                .header("Authorization", auth)
                .build();
        return dispatch(request);
    }

    private @NonNull Response dispatch(@NonNull Request request) throws IOException {
        OkHttpClient client = httpClientBuilder.build();
        return client.newCall(request).execute();
    }

    @Override
    public boolean exists(@NonNull URL url) throws IOException {
        return head(url).code() == StatusCodes.OK;
    }

    @Override
    public int getQueuedCallsCount() {
        return dispatcher.queuedCallsCount();
    }
    @Override
    public int getRunningCallsCount() {
        return dispatcher.runningCallsCount();
    }
    @Override
    public int getIdleConnectionCount() {
        return connectionPool.idleConnectionCount();
    }
    @Override
    public int getConnectionCount() {
        return connectionPool.connectionCount();
    }

}
