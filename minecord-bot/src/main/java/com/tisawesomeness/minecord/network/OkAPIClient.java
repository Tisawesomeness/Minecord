package com.tisawesomeness.minecord.network;

import com.tisawesomeness.minecord.common.OkHttpConnection;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.URL;

/**
 * A simplified HTTP client that can be used to communicate with web JSON APIs.
 */
@RequiredArgsConstructor
public class OkAPIClient implements APIClient {

    private final OkHttpConnection connection;

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
        OkHttpClient client = connection.getHttpClientBuilder().build();
        return client.newCall(request).execute();
    }

    @Override
    public boolean exists(@NonNull URL url) throws IOException {
        try (Response response = head(url)) {
            return response.code() == StatusCodes.OK;
        }
    }

    @Override
    public int getQueuedCallsCount() {
        return connection.getDispatcher().queuedCallsCount();
    }
    @Override
    public int getRunningCallsCount() {
        return connection.getDispatcher().runningCallsCount();
    }
    @Override
    public int getIdleConnectionCount() {
        return connection.getConnectionPool().idleConnectionCount();
    }
    @Override
    public int getConnectionCount() {
        return connection.getConnectionPool().connectionCount();
    }

}
