package com.tisawesomeness.minecord.network;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import okhttp3.*;

import java.io.IOException;
import java.net.URL;

/**
 * A simplified HTTP client that can be used to communicate with web JSON APIs.
 */
@RequiredArgsConstructor
public class OkAPIClient implements APIClient {

    /**
     * The builder used to construct a {@link OkHttpClient} instance
     */
    private final @NonNull OkHttpClient.Builder httpClientBuilder;
    private final @NonNull Dispatcher dispatcher;
    private final @NonNull ConnectionPool connectionPool;

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

    @Override
    public void close() {
        connectionPool.evictAll();
        dispatcher.executorService().shutdown();
    }

}
