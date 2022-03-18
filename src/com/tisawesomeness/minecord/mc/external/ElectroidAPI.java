package com.tisawesomeness.minecord.mc.external;

import com.tisawesomeness.minecord.mc.player.*;
import com.tisawesomeness.minecord.util.UrlUtils;
import com.tisawesomeness.minecord.util.UuidUtils;

import lombok.NonNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.util.*;

/**
 * A wrapper for the Mojang API that bundles multiple requests into a single request. See
 * <a href="https://github.com/Electroid/mojang-api">the docs</a>
 */
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
        Optional<UUID> uuidOpt = UuidUtils.fromString(uuidStr);
        if (!uuidOpt.isPresent()) {
            System.out.println("WARN: Mojang API returned invalid UUID: " + uuidStr);
            return Optional.empty();
        }
        UUID uuid = uuidOpt.get();

        List<NameChange> history = parseNameHistory(obj.getJSONArray("username_history"));
        Profile profile = parseProfile(obj);
        return Optional.of(new Player(uuid, history, profile));
    }
    private static List<NameChange> parseNameHistory(JSONArray json) {
        List<NameChange> nameHistoryList = new ArrayList<>();
        for (int i = 0; i < json.length(); i++) {
            JSONObject nameChange = json.getJSONObject(i);
            Username username = new Username(nameChange.getString("username"));
            if (nameChange.has("changed_at") && !nameChange.isNull("changed_at")) {
                Instant time = Instant.parse(nameChange.getString("changed_at"));
                nameHistoryList.add(NameChange.withTime(username, time));
            } else {
                nameHistoryList.add(NameChange.original(username));
            }
        }
        Collections.reverse(nameHistoryList);
        return Collections.unmodifiableList(nameHistoryList);
    }
    private static @NonNull Profile parseProfile(@NonNull JSONObject obj) {
        boolean legacy = obj.optBoolean("legacy");
        boolean demo = obj.optBoolean("demo");
        JSONObject textures = obj.getJSONObject("textures");

        URL skinUrl = null;
        SkinType skinType = SkinType.STEVE;
        if (textures.has("skin")) {
            JSONObject skinObj = textures.getJSONObject("skin");
            String link = skinObj.getString("url");
            try {
                skinUrl = new URL(UrlUtils.httpToHttps(link));
                if (textures.optBoolean("slim")) {
                    skinType = SkinType.ALEX;
                }
            } catch (MalformedURLException ignore) {
                System.err.println("Electroid returned an invalid skin URL: " + link);
            }
        }

        URL capeUrl = null;
        if (textures.has("cape")) {
            JSONObject capeObj = textures.getJSONObject("cape");
            String potentialCape = capeObj.optString("url");
            if (potentialCape != null) {
                try {
                    capeUrl = new URL(UrlUtils.httpToHttps(potentialCape));
                } catch (MalformedURLException ignore) {
                    System.err.println("Electroid returned an invalid cape URL: " + potentialCape);
                }
            }
        }

        return new Profile(legacy, demo, skinType, skinUrl, capeUrl);
    }

}
