package com.tisawesomeness.minecord.testutil.network;

import com.tisawesomeness.minecord.common.OkHttpConnection;
import com.tisawesomeness.minecord.network.OkAPIClient;

import lombok.NonNull;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

public final class OkUtil {
    private OkUtil() {}

    private static final int MAX_REQUESTS_PER_HOST = 25;
    private static final int MAX_IDLE_CONNECTIONS = 5;
    private static final int KEEP_ALIVE_DURATION = 1000;

    public static @NonNull OkAPIClient buildSampleClient() {
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequestsPerHost(MAX_REQUESTS_PER_HOST);
        ConnectionPool connectionPool = new ConnectionPool(MAX_IDLE_CONNECTIONS, KEEP_ALIVE_DURATION, TimeUnit.MILLISECONDS);
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder()
                .connectionPool(connectionPool)
                .dispatcher(dispatcher);
        OkHttpConnection connection = new OkHttpConnection(httpClientBuilder, dispatcher, connectionPool);
        return new OkAPIClient(connection);
    }

}
