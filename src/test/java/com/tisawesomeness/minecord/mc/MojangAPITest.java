package com.tisawesomeness.minecord.mc;

import com.tisawesomeness.minecord.mc.external.MojangAPI;
import com.tisawesomeness.minecord.mc.player.NameChange;
import com.tisawesomeness.minecord.mc.player.Profile;
import com.tisawesomeness.minecord.mc.player.SkinType;
import com.tisawesomeness.minecord.mc.player.Username;
import com.tisawesomeness.minecord.util.UUIDUtils;
import com.tisawesomeness.minecord.util.network.URLUtils;

import lombok.NonNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class MojangAPITest {

    private static final Username TESTING_USERNAME = Username.from("Tis_awesomeness").orElseThrow();
    private static final Username ORIGINAL_USERNAME = Username.from("tis_awesomeness").orElseThrow();
    private static final Username FAKE_USERNAME = Username.from("DoesNotExist").orElseThrow();
    private static final UUID TESTING_UUID = UUID.fromString("f6489b79-7a9f-49e2-980e-265a05dbc3af");
    private static final UUID FAKE_UUID = UUID.fromString("f6489b79-7a9f-49e2-980e-265a05dbc3ae");
    private static final long TESTING_TIMESTAMP = 1438695830000L;

    @Test
    @DisplayName("Username-->uuid endpoint is parsed correctly")
    public void testUsernameToUUID() throws IOException {
        JSONObject response = new JSONObject();
        response.put("id", UUIDUtils.toShortString(TESTING_UUID));
        response.put("name", TESTING_USERNAME.toString());
        MockMojangAPI api = new MockMojangAPI();
        api.mapUUID(TESTING_USERNAME, response.toString());
        assertThat(api.getUUID(TESTING_USERNAME)).contains(TESTING_UUID);
    }

    @Test
    @DisplayName("Username-->uuid endpoint returns no UUID when the name doesn't exist")
    public void testNonExistentUsername() throws IOException {
        MojangAPI api = new MockMojangAPI();
        assertThat(api.getUUID(FAKE_USERNAME)).isEmpty();
    }

    @Test
    @DisplayName("Name history endpoint is parsed correctly")
    public void testNameHistory() throws IOException {
        NameChange original = NameChange.original(ORIGINAL_USERNAME);
        NameChange changed = NameChange.withTimestamp(TESTING_USERNAME, TESTING_TIMESTAMP);
        List<NameChange> history = List.of(changed, original);
        MockMojangAPI api = new MockMojangAPI();
        api.mapNameHistory(TESTING_UUID, nameHistoryToJSON(history));
        assertThat(api.getNameHistory(TESTING_UUID)).isEqualTo(history);
    }
    private static String nameHistoryToJSON(List<NameChange> history) {
        JSONArray arr = new JSONArray();
        for (int i = history.size() - 1; i >= 0; i--) {
            NameChange nc = history.get(i);
            JSONObject obj = new JSONObject();
            obj.put("name", nc.getUsername().toString());
            nc.getTime().ifPresent(instant -> obj.put("changedToAt", instant.toEpochMilli()));
            arr.put(obj);
        }
        return arr.toString();
    }

    @Test
    @DisplayName("Name history endpoint returns an empty list when the UUID doesn't exist")
    public void testNonExistentNameHistory() throws IOException {
        MojangAPI api = new MockMojangAPI();
        assertThat(api.getNameHistory(FAKE_UUID)).isEmpty();
    }

    @Test
    @DisplayName("Profile endpoint is parsed correctly")
    public void testProfile() throws IOException {
        URL skinUrl = URLUtils.createUrl("https://textures.minecraft.net/texture/" +
                "8c38fdb8e126e8416edf8864d6b5f69c072836abbc8d6ebc6b3d72644e48b1bd");
        Profile profile = new Profile(false, false, SkinType.STEVE, skinUrl, null);
        MockMojangAPI api = new MockMojangAPI();
        api.mapProfile(TESTING_UUID, profileToJSON(TESTING_UUID, TESTING_USERNAME, profile));
        assertThat(api.getProfile(TESTING_UUID)).contains(profile);
    }
    private static String profileToJSON(UUID uuid, Username name, Profile profile) {
        JSONObject obj = new JSONObject();
        obj.put("id", UUIDUtils.toShortString(uuid));
        obj.put("name", name.toString());
        obj.put("properties", getPropertiesArr(uuid, name, profile));
        if (profile.isLegacy()) {
            obj.put("legacy", true);
        }
        if (profile.isDemo()) {
            obj.put("demo", true);
        }
        return obj.toString();
    }
    private static JSONArray getPropertiesArr(UUID uuid, Username name, Profile profile) {
        JSONArray arr = new JSONArray();
        JSONObject obj = new JSONObject();
        obj.put("name", "textures");
        obj.put("value", getEncodedValue(uuid, name, profile));
        arr.put(obj);
        return arr;
    }
    private static String getEncodedValue(UUID uuid, Username name, Profile profile) {
        JSONObject obj = getValueObj(uuid, name, profile);
        return Base64.getEncoder().encodeToString(obj.toString().getBytes(StandardCharsets.UTF_8));
    }
    private static JSONObject getValueObj(UUID uuid, Username name, Profile profile) {
        JSONObject obj = new JSONObject();
        obj.put("profileId", UUIDUtils.toShortString(uuid));
        obj.put("profileName", name.toString());
        obj.put("textures", getTexturesObj(profile));
        return obj;
    }
    private static JSONObject getTexturesObj(Profile profile) {
        JSONObject obj = new JSONObject();
        Optional<URL> skinUrlOpt = profile.getSkinUrl();
        if (skinUrlOpt.isPresent()) {
            JSONObject skinObj = new JSONObject();
            skinObj.put("url", skinUrlOpt.get().toString());
            if (profile.getSkinType() == SkinType.ALEX) {
                JSONObject metadataObj = new JSONObject();
                metadataObj.put("model", "slim");
                skinObj.put("metadata", metadataObj);
            }
            obj.put("SKIN", skinObj);
        }
        Optional<URL> capeUrlOpt = profile.getCapeUrl();
        if (capeUrlOpt.isPresent()) {
            JSONObject capeObj = new JSONObject();
            capeObj.put("url", capeUrlOpt.get().toString());
            obj.put("CAPE", capeObj);
        }
        return obj;
    }

    @Test
    @DisplayName("Profile endpoint returns no profile when the UUID doesn't exist")
    public void testNonExistentProfile() throws IOException {
        MojangAPI api = new MockMojangAPI();
        assertThat(api.getProfile(FAKE_UUID)).isEmpty();
    }

    private static class MockMojangAPI extends MojangAPI {

        private final Map<Username, String> uuidMap = new HashMap<>();
        public void mapUUID(@NonNull Username username, @NonNull String response) {
            uuidMap.put(username, response);
        }
        protected Optional<String> requestUUID(@NonNull Username username) {
            return Optional.ofNullable(uuidMap.get(username));
        }

        private final Map<UUID, String> nameHistoryMap = new HashMap<>();
        public void mapNameHistory(@NonNull UUID uuid, @NonNull String response) {
            nameHistoryMap.put(uuid, response);
        }
        protected Optional<String> requestNameHistory(@NonNull UUID uuid) {
            return Optional.ofNullable(nameHistoryMap.get(uuid));
        }

        private final Map<UUID, String> profileMap = new HashMap<>();
        public void mapProfile(@NonNull UUID uuid, @NonNull String response) {
            profileMap.put(uuid, response);
        }
        protected Optional<String> requestProfile(@NonNull UUID uuid) {
            return Optional.ofNullable(profileMap.get(uuid));
        }

    }

}
