package com.tisawesomeness.minecord.mc.external;

import com.tisawesomeness.minecord.mc.player.Profile;
import com.tisawesomeness.minecord.mc.player.ProfileAction;
import com.tisawesomeness.minecord.mc.player.SkinType;
import com.tisawesomeness.minecord.mc.player.Username;
import com.tisawesomeness.minecord.util.URLs;
import com.tisawesomeness.minecord.util.UUIDs;
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
     * The current ratelimit in seconds for name and profile lookups
     */
    public static final int PROFILE_RATELIMIT = 60;

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
        Optional<UUID> uuidOpt = UUIDs.fromString(uuidStr);
        if (!uuidOpt.isPresent()) {
            log.warn("Mojang API returned invalid UUID: " + uuidStr);
        }
        return uuidOpt;
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
     * @return The profile, or empty if the UUID doesn't exist, <strong>or the account is PHD</strong>
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
        Username username = new Username(valueJson.getString("profileName"));

        JSONObject textures = valueJson.getJSONObject("textures");
        SkinType skinType = SkinType.STEVE;
        URL skinUrl = null;
        if (textures.has("SKIN")) {
            JSONObject skinObj = textures.getJSONObject("SKIN");
            String link = skinObj.getString("url");
            try {
                skinUrl = new URL(URLs.httpToHttps(link));
                skinType = getSkinType(skinObj);
            } catch (MalformedURLException ignore) {
                log.error("Mojang returned an invalid skin URL: " + link);
            }
        }
        URL capeUrl = null;
        if (textures.has("CAPE")) {
            String link = textures.getJSONObject("CAPE").getString("url");
            try {
                capeUrl = new URL(URLs.httpToHttps(link));
            } catch (MalformedURLException ignore) {
                log.error("Mojang returned an invalid cape URL: " + link);
            }
        }

        JSONArray profileActionsArr = json.optJSONArray("profileActions");
        Set<ProfileAction> profileActions = parseProfileActions(profileActionsArr);

        return Optional.of(new Profile(username, legacy, demo, skinType, skinUrl, capeUrl, profileActions));
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

    private static Set<ProfileAction> parseProfileActions(JSONArray arr) {
        if (arr == null) {
            return Collections.emptySet();
        }
        Set<ProfileAction> profileActions = EnumSet.noneOf(ProfileAction.class);
        for (int i = 0; i < arr.length(); i++) {
            String actionStr = arr.getString(i);
            ProfileAction.from(actionStr.toUpperCase(Locale.ROOT)).ifPresent(profileActions::add);
        }
        return profileActions;
    }

}
