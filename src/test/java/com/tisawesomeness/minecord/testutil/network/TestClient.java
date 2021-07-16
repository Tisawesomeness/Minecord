package com.tisawesomeness.minecord.testutil.network;

import com.tisawesomeness.minecord.network.APIClient;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import java.io.IOException;
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

    private final Collection<URI> throwingUris = new ArrayList<>();
    public void addThrowingUrl(@NonNull URL url) throws URISyntaxException {
        throwingUris.add(url.toURI());
    }

    @SneakyThrows
    public @NonNull Response head(@NonNull URL url) {
        if (isClosed) {
            throw new IllegalStateException("head() called when client closed!");
        }
        if (throwingUris.contains(url.toURI())) {
            throw new IOException("Mocked IOE");
        }
        throw new UnsupportedOperationException("Unsupported");
    }

    @SneakyThrows
    public @NonNull Response get(@NonNull URL url) {
        if (isClosed) {
            throw new IllegalStateException("get() called when client closed!");
        }
        if (throwingUris.contains(url.toURI())) {
            throw new IOException("Mocked IOE");
        }
        throw new UnsupportedOperationException("Unsupported");
    }

    private final Collection<URI> existingUris = new ArrayList<>();
    public void addUrlThatExists(@NonNull URL url) throws URISyntaxException {
        existingUris.add(url.toURI());
    }
    @SneakyThrows
    public boolean exists(@NonNull URL url) {
        if (isClosed) {
            throw new IllegalStateException("exists() called when client closed!");
        }
        URI uri = url.toURI();
        if (throwingUris.contains(uri)) {
            throw new IOException("Mocked IOE");
        }
        return existingUris.contains(uri);
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
