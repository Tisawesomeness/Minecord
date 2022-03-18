package com.tisawesomeness.minecord.mc.external;

import com.tisawesomeness.minecord.mc.player.Username;
import com.tisawesomeness.minecord.network.APIClient;
import com.tisawesomeness.minecord.network.StatusCodes;
import com.tisawesomeness.minecord.util.UrlUtils;

import lombok.Cleanup;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import okhttp3.Response;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Implements the Electroid API wrapper.
 */
@RequiredArgsConstructor
public class ElectroidAPIImpl extends ElectroidAPI {

    private static final String BASE = "https://api.ashcon.app/mojang/v2/user/";
    private final @NonNull APIClient client;

    protected Optional<String> requestPlayer(@NonNull Username username) throws IOException {
        String encodedName = UrlUtils.encode(username.toString());
        URL url = UrlUtils.createUrl(BASE + encodedName);
        @Cleanup Response response = client.get(url);
        return processResponse(response);
    }

    protected Optional<String> requestPlayer(@NonNull UUID uuid) throws IOException {
        URL url = UrlUtils.createUrl(BASE + uuid);
        @Cleanup Response response = client.get(url);
        return processResponse(response);
    }

    private static Optional<String> processResponse(@NonNull Response response) throws IOException {
        if (response.code() == StatusCodes.NOT_FOUND) {
            return Optional.empty();
        }
        String msg = Objects.requireNonNull(response.body()).string();
        if (!response.isSuccessful()) {
            System.err.println("Electroid API error: " + msg);
            throw new IOException(response.code() + " error from Electroid API: " + response.message());
        }
        return Optional.of(msg);
    }

}
