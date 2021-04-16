package com.tisawesomeness.minecord.mc.external;

import com.tisawesomeness.minecord.mc.player.Username;
import com.tisawesomeness.minecord.network.APIClient;
import com.tisawesomeness.minecord.network.StatusCodes;
import com.tisawesomeness.minecord.util.URLs;
import com.tisawesomeness.minecord.util.UUIDs;

import lombok.Cleanup;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Implements the Mojang API.
 */
@RequiredArgsConstructor
public class MojangAPIImpl extends MojangAPI {

    private static final Pattern EMAIL_CASE_PATTERN = Pattern.compile("^[0-9A-Za-z_\\-.*@]+$");
    private static final URL BASE_URL = URLs.createUrl("https://api.mojang.com/users/profiles/minecraft/");
    public static final int LONGEST_DEBUGGABLE_ERROR = 256;
    private final APIClient client;
    
    protected Optional<String> requestUUID(@NonNull Username username) throws IOException {
        // While technically possible usernames, these two cannot be queried since they mess up URLs
        String name = username.toString();
        if (".".equals(name) || "..".equals(name)) {
            return Optional.empty();
        }
        @Cleanup Response response = client.get(getUuidUrl(username));
        return getContentIfPresent(response);
    }

    private static URL getUuidUrl(@NonNull Username username) {
        try {
            // Email usernames ("sample@email.com") only work when the @ is unescaped
            // This special case skips URL encoding if all characters (excluding @) are the same after encoding
            if (username.contains("@") && EMAIL_CASE_PATTERN.matcher(username).matches()) {
                return new URL(BASE_URL, username.toString());
            }
            // Otherwise, encoding is necessary to clean out naughty characters
            return new URL(BASE_URL, URLs.encode(username.toString()));
        } catch (MalformedURLException ex) {
            throw new AssertionError(ex);
        }
    }

    protected Optional<String> requestNameHistory(@NonNull UUID uuid) throws IOException {
        // UUID must have hyphens stripped
        String link = String.format("https://api.mojang.com/user/profiles/%s/names", UUIDs.toShortString(uuid));
        @Cleanup Response response = client.get(URLs.createUrl(link));
        return getContentIfPresent(response);
    }

    protected Optional<String> requestProfile(@NonNull UUID uuid) throws IOException {
        // UUID must have hyphens stripped
        String link = "https://sessionserver.mojang.com/session/minecraft/profile/" + UUIDs.toShortString(uuid);
        @Cleanup Response response = client.get(URLs.createUrl(link));
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
            ResponseBody body = response.body();
            String error = response.code() + " error from Mojang API: " + response.message();
            if (body == null || body.contentLength() > LONGEST_DEBUGGABLE_ERROR) {
                throw new IOException(error);
            }
            throw new IOException(error + " | " + body.string());
        }
    }

}
