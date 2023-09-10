package com.tisawesomeness.minecord.mc.external;

import com.tisawesomeness.minecord.common.util.IO;
import com.tisawesomeness.minecord.mc.player.Profile;
import com.tisawesomeness.minecord.mc.player.ProfileAction;
import com.tisawesomeness.minecord.mc.player.SkinType;
import com.tisawesomeness.minecord.mc.player.Username;
import com.tisawesomeness.minecord.testutil.mc.MockMojangAPI;
import com.tisawesomeness.minecord.util.URLs;
import com.tisawesomeness.minecord.util.UUIDs;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MojangAPITest {

    private static final Username TESTING_USERNAME = new Username("Tis_awesomeness");
    private static final Username FAKE_USERNAME = new Username("DoesNotExist");
    private static final Username NON_ASCII_USERNAME = new Username("ooθoo");
    private static final UUID TESTING_UUID = UUID.fromString("f6489b79-7a9f-49e2-980e-265a05dbc3af");
    private static final UUID FAKE_UUID = UUID.fromString("f6489b79-7a9f-49e2-980e-265a05dbc3ae");
    private static final String PROFILE_ACTIONS_RESPONSE = IO.loadResource("profileActionsTestResponse.json", MojangAPITest.class);

    @Test
    @DisplayName("Username-->uuid endpoint is parsed correctly")
    public void testUsernameToUUID() throws IOException {
        JSONObject response = new JSONObject();
        response.put("id", UUIDs.toShortString(TESTING_UUID));
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
    @DisplayName("Invalid usernames are rejected and throw IllegalArgumentException")
    public void testInvalidUsername() {
        MojangAPI api = new MockMojangAPI();
        assertThatThrownBy(() -> api.getUUID(NON_ASCII_USERNAME)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Profile endpoint is parsed correctly")
    public void testProfile() throws IOException {
        URL skinUrl = URLs.createUrl("https://textures.minecraft.net/texture/" +
                "8c38fdb8e126e8416edf8864d6b5f69c072836abbc8d6ebc6b3d72644e48b1bd");
        Profile profile = new Profile(TESTING_USERNAME, false, false, SkinType.STEVE, skinUrl, null);
        MockMojangAPI api = new MockMojangAPI();
        api.mapProfile(TESTING_UUID, profileToJSON(TESTING_UUID, TESTING_USERNAME, profile));
        assertThat(api.getProfile(TESTING_UUID)).contains(profile);
    }
    private static String profileToJSON(UUID uuid, Username name, Profile profile) {
        JSONObject obj = new JSONObject();
        obj.put("id", UUIDs.toShortString(uuid));
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
        obj.put("profileId", UUIDs.toShortString(uuid));
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

    @Test
    @DisplayName("Profile actions are correctly parsed")
    public void testProfileActions() throws IOException {
        MockMojangAPI api = new MockMojangAPI();
        api.mapProfile(TESTING_UUID, PROFILE_ACTIONS_RESPONSE);
        URL skinUrl = URLs.createUrl("https://textures.minecraft.net/texture/3196c893f1a6131ad5ba78fec26185bc424f408419e237f7b15f8a3bc69a90a0");
        Set<ProfileAction> profileActions = EnumSet.of(ProfileAction.FORCED_NAME_CHANGE, ProfileAction.USING_BANNED_SKIN);
        Profile profile = new Profile(TESTING_USERNAME, false, false, SkinType.STEVE, skinUrl, null, profileActions);
        assertThat(api.getProfile(TESTING_UUID)).contains(profile);
    }

}
