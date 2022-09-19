package com.tisawesomeness.minecord.mc.external;

import com.tisawesomeness.minecord.mc.player.Username;
import com.tisawesomeness.minecord.network.APIClient;
import com.tisawesomeness.minecord.network.NetUtil;
import com.tisawesomeness.minecord.network.StatusCodes;
import com.tisawesomeness.minecord.util.UrlUtils;
import com.tisawesomeness.minecord.util.UuidUtils;

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
import java.util.regex.Pattern;

/**
 * Implements the Mojang API.
 */
@RequiredArgsConstructor
public class MojangAPIImpl extends MojangAPI {

    private static final Pattern EMAIL_CASE_PATTERN = Pattern.compile("^[0-9A-Za-z_\\-.*@]+$");
    private static final URL BASE_URL = UrlUtils.createUrl("https://api.mojang.com/users/profiles/minecraft/");
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
            return new URL(BASE_URL, UrlUtils.encode(username.toString()));
        } catch (MalformedURLException ex) {
            throw new AssertionError(ex);
        }
    }

    protected Optional<String> requestProfile(@NonNull UUID uuid) throws IOException {
        // UUID must have hyphens stripped
        String link = "https://sessionserver.mojang.com/session/minecraft/profile/" + UuidUtils.toShortString(uuid);
        @Cleanup Response response = client.get(UrlUtils.createUrl(link));
        return getContentIfPresent(response);
    }

    private static Optional<String> getContentIfPresent(@NonNull Response response) throws IOException {
        NetUtil.throwIfError(response, "Mojang API");
        if (response.code() == StatusCodes.NO_CONTENT) {
            return Optional.empty();
        }
        String content = Objects.requireNonNull(response.body()).string();
        return Optional.of(content);
    }

}
