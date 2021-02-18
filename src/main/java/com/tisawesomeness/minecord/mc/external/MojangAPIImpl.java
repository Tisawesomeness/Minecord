package com.tisawesomeness.minecord.mc.external;

import com.tisawesomeness.minecord.mc.player.Username;
import com.tisawesomeness.minecord.network.APIClient;
import com.tisawesomeness.minecord.network.StatusCodes;
import com.tisawesomeness.minecord.util.UUIDUtils;
import com.tisawesomeness.minecord.util.network.URLUtils;

import com.google.common.base.CharMatcher;
import lombok.Cleanup;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import okhttp3.Response;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Implements the Mojang API.
 */
@RequiredArgsConstructor
public class MojangAPIImpl extends MojangAPI {

    private static final CharMatcher EMAIL_CASE_MATCHER = CharMatcher.inRange('0', '9')
            .or(CharMatcher.inRange('A', 'Z'))
            .or(CharMatcher.inRange('a', 'z'))
            .or(CharMatcher.anyOf("_-.*@"));
    private static final URL BASE_URL = URLUtils.createUrl("https://api.mojang.com/users/profiles/minecraft/");
    private final APIClient client;
    
    protected Optional<String> requestUUID(@NonNull Username username) throws IOException {
        @Cleanup Response response = client.get(getUuidUrl(username));
        return getContentIfPresent(response);
    }

    private static URL getUuidUrl(@NonNull Username username) {
        try {
            // Email usernames ("sample@email.com") only work when the @ is unescaped
            // This special case skips URL encoding if all characters (excluding @) are the same after encoding
            if (username.contains("@") && EMAIL_CASE_MATCHER.matchesAllOf(username)) {
                return new URL(BASE_URL, username.toString());
            }
            // Otherwise, encoding is necessary to clean out naughty characters
            return new URL(BASE_URL, URLUtils.encode(username.toString()));
        } catch (MalformedURLException ex) {
            throw new AssertionError(ex);
        }
    }

    protected Optional<String> requestNameHistory(@NonNull UUID uuid) throws IOException {
        // UUID must have hyphens stripped
        String link = String.format("https://api.mojang.com/user/profiles/%s/names", UUIDUtils.toShortString(uuid));
        @Cleanup Response response = client.get(URLUtils.createUrl(link));
        return getContentIfPresent(response);
    }

    protected Optional<String> requestProfile(@NonNull UUID uuid) throws IOException {
        // UUID must have hyphens stripped
        String link = "https://sessionserver.mojang.com/session/minecraft/profile/" + UUIDUtils.toShortString(uuid);
        @Cleanup Response response = client.get(URLUtils.createUrl(link));
        return getContentIfPresent(response);
    }

    private static Optional<String> getContentIfPresent(@NonNull Response response) throws IOException {
        throwIfError(response);
        if (response.code() == StatusCodes.NO_CONTENT) {
            return Optional.empty();
        }
        String content = Objects.requireNonNull(response.body()).string();
        return Optional.of(content);
    }
    private static void throwIfError(Response response) throws IOException {
        if (!response.isSuccessful()) {
            throw new IOException(response.code() + " error from Mojang API: " + response.message());
        }
    }

}
