package com.tisawesomeness.minecord.mc.player;

import com.tisawesomeness.minecord.util.network.URLUtils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class PlayerTest {

    // Tis_awesomeness
    private static final UUID STEVE_UUID = UUID.fromString("f6489b79-7a9f-49e2-980e-265a05dbc3af");
    private static final URL SKIN_URL = URLUtils.createUrl("http://textures.minecraft.net/texture/" +
            "8c38fdb8e126e8416edf8864d6b5f69c072836abbc8d6ebc6b3d72644e48b1bd");
    // jeb_
    private static final UUID ALEX_UUID = UUID.fromString("853c80ef-3c37-49fd-aa49-938b674adae6");

    private static Player initPlayerWithDefaultSkin(UUID uuid) {
        Username name = Username.fromAny("SamplePlayer");
        List<NameChange> history = Collections.singletonList(NameChange.original(name));
        // skin type does not matter if both URLs are null
        Profile profile = new Profile(false, false, SkinType.STEVE, null, null);
        return new Player(uuid, name, history, profile);
    }
    private static Player initPlayerWithSkinType(UUID uuid, SkinType skinType) {
        Username name = Username.fromAny("SamplePlayer");
        List<NameChange> history = Collections.singletonList(NameChange.original(name));
        Profile profile = new Profile(false, false, skinType, SKIN_URL, null);
        return new Player(uuid, name, history, profile);
    }

    @Test
    @DisplayName("The default skin type is correct for Steve UUIDs")
    public void testGetDefaultSkinTypeSteve() {
        Player player = initPlayerWithDefaultSkin(STEVE_UUID);
        assertThat(player.getDefaultSkinType()).isEqualTo(SkinType.STEVE);
    }

    @Test
    @DisplayName("The default skin type is correct for Alex UUIDs")
    public void testGetDefaultSkinTypeAlex() {
        Player player = initPlayerWithDefaultSkin(ALEX_UUID);
        assertThat(player.getDefaultSkinType()).isEqualTo(SkinType.ALEX);
    }

    @Test
    @DisplayName("The skin type is correct for default skins with Steve UUIDs")
    public void testGetSkinTypeDefaultSteve() {
        Player player = initPlayerWithDefaultSkin(STEVE_UUID);
        assertThat(player.getSkinType()).isEqualTo(SkinType.STEVE);
    }

    @Test
    @DisplayName("The skin type is correct for default skins with Alex UUIDs")
    public void testGetSkinTypeDefaultAlex() {
        Player player = initPlayerWithDefaultSkin(ALEX_UUID);
        assertThat(player.getSkinType()).isEqualTo(SkinType.ALEX);
    }

    @Test
    @DisplayName("The skin type is correct for Steve skins")
    public void testGetSkinTypeSteve() {
        Player player = initPlayerWithSkinType(ALEX_UUID, SkinType.STEVE);
        assertThat(player.getSkinType()).isEqualTo(SkinType.STEVE);
    }

    @Test
    @DisplayName("The skin type is correct for Alex skins")
    public void testGetSkinTypeAlex() {
        Player player = initPlayerWithSkinType(STEVE_UUID, SkinType.ALEX);
        assertThat(player.getSkinType()).isEqualTo(SkinType.ALEX);
    }

    @Test
    @DisplayName("The skin URL of a custom skin is returned")
    public void testCustomSkin() throws URISyntaxException {
        Player player = initPlayerWithSkinType(STEVE_UUID, SkinType.STEVE);
        assertThat(player.getSkinUrl().toURI()).isEqualTo(SKIN_URL.toURI());
    }

    @Test
    @DisplayName("The URLs of the default steve and alex URLs are different")
    public void testDefaultSteveAndAlexSkins() throws URISyntaxException {
        Player steve = initPlayerWithDefaultSkin(STEVE_UUID);
        Player alex = initPlayerWithDefaultSkin(ALEX_UUID);
        // converting to URI since URL#equals() does a DNS lookup (seriously?)
        URI steveUri = steve.getSkinUrl().toURI();
        URI alexUri = alex.getSkinUrl().toURI();
        assertThat(steveUri).isNotEqualTo(alexUri);
    }

}
