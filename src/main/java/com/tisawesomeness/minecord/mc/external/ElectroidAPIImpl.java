package com.tisawesomeness.minecord.mc.external;

import com.tisawesomeness.minecord.mc.player.Username;
import com.tisawesomeness.minecord.network.APIClient;
import com.tisawesomeness.minecord.network.StatusCodes;
import com.tisawesomeness.minecord.util.network.URLUtils;

import lombok.Cleanup;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Implements the Electroid API wrapper.
 */
@Slf4j
@RequiredArgsConstructor
public class ElectroidAPIImpl extends ElectroidAPI {

    private static final String BASE = "https://api.ashcon.app/mojang/v2/user/";
    private final @NonNull APIClient client;

    protected Optional<String> requestPlayer(@NonNull Username username) throws IOException {
        String encodedName = URLUtils.encode(username.toString());
        URL url = URLUtils.createUrl(BASE + encodedName);
        @Cleanup Response response = client.get(url);
        return processResponse(response);
    }

    protected Optional<String> requestPlayer(@NonNull UUID uuid) throws IOException {
        URL url = URLUtils.createUrl(BASE + uuid);
        @Cleanup Response response = client.get(url);
        return processResponse(response);
    }

    private static Optional<String> processResponse(@NonNull Response response) throws IOException {
        if (response.code() == StatusCodes.NOT_FOUND) {
            return Optional.empty();
        }
        String msg = Objects.requireNonNull(response.body()).string();
        if (!response.isSuccessful()) {
            log.error("Electroid API error: " + msg);
            throw new IOException(response.code() + " error from Electroid API: " + response.message());
        }
        return Optional.of(msg);
    }

}
