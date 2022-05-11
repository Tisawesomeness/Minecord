package com.tisawesomeness.minecord.common;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;

@Getter
@RequiredArgsConstructor
public class OkHttpConnection {
    /** Builder used to create the HTTP client */
    private final @NonNull OkHttpClient.Builder httpClientBuilder;
    /** Dispatcher used to deploy HTTP requests */
    private final @NonNull Dispatcher dispatcher;
    /** Pool used to manage HTTP connections */
    private final @NonNull ConnectionPool connectionPool;

    /**
     * Closes the HTTP client.
     */
    public void close() {
        connectionPool.evictAll();
        dispatcher.executorService().shutdown();
    }
}
