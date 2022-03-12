package com.tisawesomeness.minecord.mc.external;

import com.tisawesomeness.minecord.network.APIClient;
import com.tisawesomeness.minecord.network.NetUtil;
import com.tisawesomeness.minecord.network.StatusCodes;
import com.tisawesomeness.minecord.util.URLs;

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
 * Implements the Gapple API.
 */
@RequiredArgsConstructor
public class GappleAPIImpl extends GappleAPI {

    private static final URL BASE_URL = URLs.createUrl("https://api.gapple.pw/status/");

    private final @NonNull APIClient client;

    protected Optional<String> requestAccountStatus(@NonNull UUID uuid) throws IOException {
        URL url = new URL(BASE_URL, uuid.toString());
        @Cleanup Response response = client.get(url);
        if (response.code() == StatusCodes.NOT_FOUND) {
            return Optional.empty();
        }
        NetUtil.throwIfError(response, "Gapple API");
        String content = Objects.requireNonNull(response.body()).string();
        return Optional.of(content);
    }

}
