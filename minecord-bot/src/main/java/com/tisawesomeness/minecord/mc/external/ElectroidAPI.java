package com.tisawesomeness.minecord.mc.external;

import com.tisawesomeness.minecord.mc.player.Player;
import com.tisawesomeness.minecord.mc.player.Profile;
import com.tisawesomeness.minecord.mc.player.SkinType;
import com.tisawesomeness.minecord.mc.player.Username;
import com.tisawesomeness.minecord.util.URLs;
import com.tisawesomeness.minecord.util.UUIDs;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.UUID;

/**
 * A wrapper for the Mojang API that bundles multiple requests into a single request. See
 * <a href="https://github.com/Electroid/mojang-api">the docs</a>
 */
@Slf4j
public abstract class ElectroidAPI {

    /**
     * Requests the player currently associated with a username.
     * @param username A supported username
     * @return The raw JSON response, or empty if the username doesn't <b>currently</b> exist.
     * @throws IOException If an I/O error occurs
     */
    protected abstract Optional<String> requestPlayer(@NonNull Username username) throws IOException;
    /**
     * Gets the player currently associated with a username.
     * @param username A <b>valid</b> username
     * @return The player the username belongs to, or empty if the username doesn't <b>currently</b> exist
     * @throws IOException If an I/O error occurs
     * @throws IllegalArgumentException If the username is invalid
     */
    public Optional<Player> getPlayer(@NonNull Username username) throws IOException {
        if (!username.isValid()) {
            throw new IllegalArgumentException("Invalid usernames are not supported by the Electroid API.");
        }
        Optional<String> responseOpt = requestPlayer(username);
        return responseOpt.flatMap(ElectroidAPI::parseResponse);
    }

    /**
     * Requests the player associated with a UUID.
     * @param uuid The player's UUID
     * @return The raw JSON response, or empty if the UUID doesn't exist
     * @throws IOException If an I/O error occurs
     */
    protected abstract Optional<String> requestPlayer(@NonNull UUID uuid) throws IOException;
    /**
     * Gets a player from a UUID.
     * This will return empty if the account doesn't exist or is PHD. Use the {@link MojangAPI} to find out which.
     * @param uuid The player's UUID
     * @return The player, or empty if the UUID doesn't exist, <strong>or the account is PHD</strong>
     * @throws IOException If an I/O error occurs
     */
    public Optional<Player> getPlayer(@NonNull UUID uuid) throws IOException {
        Optional<String> responseOpt = requestPlayer(uuid);
        return responseOpt.flatMap(ElectroidAPI::parseResponse);
    }

    private static @NonNull Optional<Player> parseResponse(@NonNull String response) {
        JSONObject obj = new JSONObject(response);
        String uuidStr = obj.getString("uuid");
        Optional<UUID> uuidOpt = UUIDs.fromString(uuidStr);
        if (!uuidOpt.isPresent()) {
            log.warn("Mojang API returned invalid UUID: " + uuidStr);
            return Optional.empty();
        }
        UUID uuid = uuidOpt.get();

        Profile profile = parseProfile(obj);
        return Optional.of(new Player(uuid, profile));
    }
    private static @NonNull Profile parseProfile(@NonNull JSONObject obj) {
        Username username = new Username(obj.getString("username"));
        boolean legacy = obj.optBoolean("legacy");
        boolean demo = obj.optBoolean("demo");
        JSONObject textures = obj.getJSONObject("textures");

        URL skinUrl = null;
        SkinType skinType = SkinType.STEVE;
        if (textures.has("skin")) {
            JSONObject skinObj = textures.getJSONObject("skin");
            String link = skinObj.getString("url");
            try {
                skinUrl = new URL(URLs.httpToHttps(link));
                if (textures.optBoolean("slim")) {
                    skinType = SkinType.ALEX;
                }
            } catch (MalformedURLException ignore) {
                log.error("Electroid returned an invalid skin URL: " + link);
            }
        }

        URL capeUrl = null;
        if (textures.has("cape")) {
            JSONObject capeObj = textures.getJSONObject("cape");
            String potentialCape = capeObj.optString("url");
            if (potentialCape != null) {
                try {
                    capeUrl = new URL(URLs.httpToHttps(potentialCape));
                } catch (MalformedURLException ignore) {
                    log.error("Electroid returned an invalid cape URL: " + potentialCape);
                }
            }
        }

        return new Profile(username, legacy, demo, skinType, skinUrl, capeUrl);
    }

}
