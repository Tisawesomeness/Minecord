package com.tisawesomeness.minecord.mc.external;

import com.tisawesomeness.minecord.mc.player.NameChange;
import com.tisawesomeness.minecord.mc.player.Profile;
import com.tisawesomeness.minecord.mc.player.SkinType;
import com.tisawesomeness.minecord.mc.player.Username;
import com.tisawesomeness.minecord.util.UUIDUtils;
import com.tisawesomeness.minecord.util.network.URLUtils;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * A wrapper for the Mojang API. See <a href="https://wiki.vg/Mojang_API">the docs</a>
 */
@Slf4j
public abstract class MojangAPI {

    /**
     * Requests the UUID currently associated with a username.
     * @param username A supported username
     * @return The raw JSON response, or empty if the username doesn't <b>currently</b> exist
     * @throws IOException If an I/O error occurs
     */
    protected abstract Optional<String> requestUUID(@NonNull Username username) throws IOException;
    /**
     * Gets the UUID currently associated with a username.
     * @param username The player's username
     * @return The UUID the username belongs to, or empty if the username doesn't <b>currently</b> exist
     * @throws IOException If an I/O error occurs
     * @throws IllegalArgumentException If the username is not supported by the Mojang API
     */
    public Optional<UUID> getUUID(@NonNull Username username) throws IOException {
        if (!username.isSupportedByMojangAPI()) {
            throw new IllegalArgumentException(username + " is not supported by the Mojang API.");
        }
        Optional<String> responseOpt = requestUUID(username);
        if (!responseOpt.isPresent()) {
            return Optional.empty();
        }
        JSONObject json = new JSONObject(responseOpt.get());
        String uuidStr = json.getString("id");
        Optional<UUID> uuidOpt = UUIDUtils.fromString(uuidStr);
        if (!uuidOpt.isPresent()) {
            log.warn("Mojang API returned invalid UUID: " + uuidStr);
        }
        return uuidOpt;
    }

    /**
     * Requests the name history of a player, ranging from their original name to their current name.
     * @param uuid The player's UUID
     * @return The raw JSON response, or empty if the UUID doesn't exist
     * @throws IOException If an I/O error occurs
     */
    protected abstract Optional<String> requestNameHistory(@NonNull UUID uuid) throws IOException;
    /**
     * Gets the name history of a player, ranging from their original name to their current name.
     * @param uuid The player's UUID
     * @return An immutable list of name changes, which can contain one item (the original username) if the player has
     * no name changes, and will be empty if there is no player with the given UUID
     * @throws IOException If an I/O error occurs
     */
    public List<NameChange> getNameHistory(@NonNull UUID uuid) throws IOException {
        Optional<String> responseOpt = requestNameHistory(uuid);
        if (!responseOpt.isPresent()) {
            return Collections.emptyList();
        }
        JSONArray json = new JSONArray(responseOpt.get());
        List<NameChange> nameHistoryList = new ArrayList<>();
        for (int i = 0; i < json.length(); i++) {
            JSONObject nameChange = json.getJSONObject(i);
            Username username = new Username(nameChange.getString("name"));
            if (nameChange.has("changedToAt")) {
                long timestamp = nameChange.getLong("changedToAt");
                nameHistoryList.add(NameChange.withTimestamp(username, timestamp));
            } else {
                nameHistoryList.add(NameChange.original(username));
            }
        }
        Collections.reverse(nameHistoryList);
        return Collections.unmodifiableList(nameHistoryList);
    }

    /**
     * Requests the profile of a player.
     * @param uuid The player's UUID
     * @return The raw JSON response, or empty if the UUID doesn't exist
     * @throws IOException If an I/O error occurs
     */
    protected abstract Optional<String> requestProfile(@NonNull UUID uuid) throws IOException;
    /**
     * Gets the profile of a player.
     * @param uuid The player's UUID
     * @return The profile, or empty if the UUID doesn't exist
     * @throws IOException If an I/O error occurs
     */
    public Optional<Profile> getProfile(@NonNull UUID uuid) throws IOException {
        Optional<String> responseOpt = requestProfile(uuid);
        if (!responseOpt.isPresent()) {
            return Optional.empty();
        }
        JSONObject json = new JSONObject(responseOpt.get());
        boolean legacy = json.optBoolean("legacy");
        boolean demo = json.optBoolean("demo");

        String b64String = json.getJSONArray("properties").getJSONObject(0).getString("value");
        String value = decodeBase64(b64String);
        JSONObject valueJson = new JSONObject(value);
        JSONObject textures = valueJson.getJSONObject("textures");

        SkinType skinType = SkinType.STEVE;
        URL skinUrl = null;
        if (textures.has("SKIN")) {
            JSONObject skinObj = textures.getJSONObject("SKIN");
            String link = skinObj.getString("url");
            try {
                skinUrl = new URL(URLUtils.httpToHttps(link));
                skinType = getSkinType(skinObj);
            } catch (MalformedURLException ex) {
                log.error("Mojang returned an invalid skin URL: " + link, ex);
            }
        }
        URL capeUrl = null;
        if (textures.has("CAPE")) {
            String link = textures.getJSONObject("CAPE").getString("url");
            try {
                capeUrl = new URL(URLUtils.httpToHttps(link));
            } catch (MalformedURLException ex) {
                log.error("Mojang returned an invalid cape URL: " + link, ex);
            }
        }
        return Optional.of(new Profile(legacy, demo, skinType, skinUrl, capeUrl));
    }

    private static SkinType getSkinType(@NonNull JSONObject skinObj) {
        if (skinObj.has("metadata")) {
            String model = skinObj.getJSONObject("metadata").getString("model");
            if ("slim".equalsIgnoreCase(model)) {
                return SkinType.ALEX;
            }
        }
        return SkinType.STEVE;
    }

    private static String decodeBase64(@NonNull String base64String) {
        return new String(Base64.getDecoder().decode(base64String), StandardCharsets.UTF_8);
    }

}
