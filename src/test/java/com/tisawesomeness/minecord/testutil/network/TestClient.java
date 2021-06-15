package com.tisawesomeness.minecord.testutil.network;

import com.tisawesomeness.minecord.network.APIClient;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

/**
 * A mock API client used to simulate HTTP requests.
 */
@Slf4j
public class TestClient implements APIClient {

    private boolean isClosed;

    public OkHttpClient.Builder getHttpClientBuilder() {
        throw new UnsupportedOperationException("Unsupported");
    }

    public @NonNull Response head(@NonNull URL url) {
        throw new UnsupportedOperationException("Unsupported");
    }

    public @NonNull Response get(@NonNull URL url) {
        throw new UnsupportedOperationException("Unsupported");
    }

    private final Collection<URI> existingUris = new ArrayList<>();
    public void addUrlThatExists(@NonNull URL url) throws URISyntaxException {
        existingUris.add(url.toURI());
    }
    public boolean exists(@NonNull URL url) {
        if (isClosed) {
            throw new IllegalStateException("exists() called when client closed!");
        }
        try {
            return existingUris.contains(url.toURI());
        } catch (URISyntaxException ex) {
            throw new AssertionError(ex);
        }
    }

    public int getQueuedCallsCount() {
        return 0;
    }
    public int getRunningCallsCount() {
        return 0;
    }
    public int getIdleConnectionCount() {
        return 0;
    }
    public int getConnectionCount() {
        return 0;
    }

    public void close() {
        isClosed = true;
    }

}
