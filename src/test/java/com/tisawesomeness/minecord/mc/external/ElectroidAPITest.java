package com.tisawesomeness.minecord.mc.external;

import com.tisawesomeness.minecord.mc.player.*;
import com.tisawesomeness.minecord.testutil.mc.MockElectroidAPI;
import com.tisawesomeness.minecord.util.ListUtils;
import com.tisawesomeness.minecord.util.network.URLUtils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ElectroidAPITest {

    private static final Player TESTING_PLAYER = new Player(
            UUID.fromString("f6489b79-7a9f-49e2-980e-265a05dbc3af"),
            new Username("Tis_awesomeness"),
            ListUtils.of(
                    NameChange.withTimestamp(new Username("Tis_awesomeness"), 1438695830000L),
                    NameChange.original(new Username("tis_awesomeness"))
            ),
            new Profile(
                    false,
                    false,
                    SkinType.STEVE,
                    URLUtils.createUrl("https://textures.minecraft.net/texture/" +
                            "8c38fdb8e126e8416edf8864d6b5f69c072836abbc8d6ebc6b3d72644e48b1bd"),
                    null
            )
    );
    private static final Username FAKE_USERNAME = new Username("DoesNotExist");
    private static final UUID FAKE_UUID = UUID.fromString("f6489b79-7a9f-49e2-980e-265a05dbc3ae");
    private static final Username INVALID_USERNAME = new Username("8");

    @Test
    @DisplayName("Username input is parsed correctly")
    public void testUsername() throws IOException {
        Username username = TESTING_PLAYER.getUsername();
        MockElectroidAPI api = new MockElectroidAPI();
        api.mapUsername(username, playerToJSON(TESTING_PLAYER));
        assertThat(api.getPlayer(username).orElse(null))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(TESTING_PLAYER);
    }

    @Test
    @DisplayName("Username input returns empty when the username doesn't exist")
    public void testNonExistentUsername() throws IOException {
        ElectroidAPI api = new MockElectroidAPI();
        assertThat(api.getPlayer(FAKE_USERNAME)).isEmpty();
    }

    @Test
    @DisplayName("Invalid usernames are rejected and throw IllegalArgumentException")
    public void testInvalidUsername() {
        ElectroidAPI api = new MockElectroidAPI();
        assertThatThrownBy(() -> api.getPlayer(INVALID_USERNAME)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("UUID input is parsed correctly")
    public void testUuid() throws IOException {
        UUID uuid = TESTING_PLAYER.getUuid();
        MockElectroidAPI api = new MockElectroidAPI();
        api.mapUuid(uuid, playerToJSON(TESTING_PLAYER));
        assertThat(api.getPlayer(uuid).orElse(null))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(TESTING_PLAYER);
    }

    @Test
    @DisplayName("UUID input returns empty when the UUID doesn't exist")
    public void testNonExistentUuid() throws IOException {
        ElectroidAPI api = new MockElectroidAPI();
        assertThat(api.getPlayer(FAKE_UUID)).isEmpty();
    }

    private static String playerToJSON(Player player) {
        JSONObject obj = new JSONObject();
        obj.put("uuid", player.getUuid().toString());
        obj.put("username", player.getUsername().toString());
        obj.put("username_history", nameHistoryToJSON(player.getNameHistory()));
        Profile profile = player.getProfile();
        obj.put("textures", profileToTexturesJSON(profile));
        if (profile.isLegacy()) {
            obj.put("legacy", true);
        }
        if (profile.isDemo()) {
            obj.put("demo", true);
        }
        return obj.toString();
    }
    private static JSONArray nameHistoryToJSON(List<NameChange> history) {
        JSONArray arr = new JSONArray();
        for (int i = history.size() - 1; i >= 0; i--) {
            NameChange nc = history.get(i);
            JSONObject obj = new JSONObject();
            obj.put("username", nc.getUsername().toString());
            nc.getTime().ifPresent(instant -> obj.put("changed_at", instant.toString()));
            arr.put(obj);
        }
        return arr;
    }
    private static JSONObject profileToTexturesJSON(Profile profile) {
        JSONObject obj = new JSONObject();
        if (profile.getSkinType() == SkinType.ALEX) {
            obj.put("slim", true);
        }
        Optional<URL> skinUrlOpt = profile.getSkinUrl();
        if (skinUrlOpt.isPresent()) {
            URL skinUrl = skinUrlOpt.get();
            JSONObject skinObj = new JSONObject();
            skinObj.put("url", skinUrl.toString());
            obj.put("skin", skinObj);
        }
        Optional<URL> capeUrlOpt = profile.getCapeUrl();
        if (capeUrlOpt.isPresent()) {
            URL capeUrl = capeUrlOpt.get();
            JSONObject capeObj = new JSONObject();
            capeObj.put("url", capeUrl.toString());
            obj.put("cape", capeObj);
        }
        return obj;
    }

}
