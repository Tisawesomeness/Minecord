package com.tisawesomeness.minecord.mc.player;

import com.tisawesomeness.minecord.testutil.PlayerTests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;

public class PlayerTest {

    @Test
    @DisplayName("The default skin type is correct for Steve UUIDs")
    public void testGetDefaultSkinTypeSteve() {
        Player player = PlayerTests.initPlayerWithDefaultSkin(PlayerTests.TIS_STEVE_UUID);
        assertThat(player.getDefaultSkinType()).isEqualTo(SkinType.STEVE);
    }

    @Test
    @DisplayName("The default skin type is correct for Alex UUIDs")
    public void testGetDefaultSkinTypeAlex() {
        Player player = PlayerTests.initPlayerWithDefaultSkin(PlayerTests.JEB_ALEX_UUID);
        assertThat(player.getDefaultSkinType()).isEqualTo(SkinType.ALEX);
    }

    @Test
    @DisplayName("The skin type is correct for default skins with Steve UUIDs")
    public void testGetSkinTypeDefaultSteve() {
        Player player = PlayerTests.initPlayerWithDefaultSkin(PlayerTests.TIS_STEVE_UUID);
        assertThat(player.getSkinType()).isEqualTo(SkinType.STEVE);
    }

    @Test
    @DisplayName("The skin type is correct for default skins with Alex UUIDs")
    public void testGetSkinTypeDefaultAlex() {
        Player player = PlayerTests.initPlayerWithDefaultSkin(PlayerTests.JEB_ALEX_UUID);
        assertThat(player.getSkinType()).isEqualTo(SkinType.ALEX);
    }

    @Test
    @DisplayName("The skin type is correct for Steve skins")
    public void testGetSkinTypeSteve() {
        Player player = PlayerTests.initPlayerWithSkinType(PlayerTests.JEB_ALEX_UUID, SkinType.STEVE);
        assertThat(player.getSkinType()).isEqualTo(SkinType.STEVE);
    }

    @Test
    @DisplayName("The skin type is correct for Alex skins")
    public void testGetSkinTypeAlex() {
        Player player = PlayerTests.initPlayerWithSkinType(PlayerTests.TIS_STEVE_UUID, SkinType.ALEX);
        assertThat(player.getSkinType()).isEqualTo(SkinType.ALEX);
    }

    @Test
    @DisplayName("The skin URL of a custom skin is returned")
    public void testCustomSkin() throws URISyntaxException {
        Player player = PlayerTests.initPlayerWithSkinType(PlayerTests.TIS_STEVE_UUID, SkinType.STEVE);
        assertThat(player.getSkinUrl().toURI()).isEqualTo(PlayerTests.CUSTOM_SKIN_URL.toURI());
    }

    @Test
    @DisplayName("Player with non-default skin URL has custom skin")
    public void testHasCustomSkin() {
        Player player = PlayerTests.initPlayerWithSkinType(PlayerTests.TIS_STEVE_UUID, SkinType.STEVE);
        assertThat(player.hasCustomSkin()).isTrue();
    }

    @Test
    @DisplayName("Player with steve skin URL does not have custom skin")
    public void testSteveIsNotCustomSkin() {
        Player player = PlayerTests.initPlayerWithDefaultSkinUrl(PlayerTests.TIS_STEVE_UUID, SkinType.STEVE);
        assertThat(player.hasCustomSkin()).isFalse();
    }

    @Test
    @DisplayName("Player with alex skin URL does not have custom skin")
    public void testAlexIsNotCustomSkin() {
        Player player = PlayerTests.initPlayerWithDefaultSkinUrl(PlayerTests.JEB_ALEX_UUID, SkinType.ALEX);
        assertThat(player.hasCustomSkin()).isFalse();
    }

    @Test
    @DisplayName("Player with steve skin URL but alex UUID has custom skin")
    public void testSteveAlexIsCustomSkin() {
        Player player = PlayerTests.initPlayerWithDefaultSkinUrl(PlayerTests.JEB_ALEX_UUID, SkinType.STEVE);
        assertThat(player.hasCustomSkin()).isTrue();
    }

    @Test
    @DisplayName("Player with alex skin URL but skin UUID has custom skin")
    public void testAlexSteveIsCustomSkin() {
        Player player = PlayerTests.initPlayerWithDefaultSkinUrl(PlayerTests.TIS_STEVE_UUID, SkinType.ALEX);
        assertThat(player.hasCustomSkin()).isTrue();
    }

    @Test
    @DisplayName("The URLs of the default steve and alex URLs are different")
    public void testDefaultSteveAndAlexSkins() throws URISyntaxException {
        Player steve = PlayerTests.initPlayerWithDefaultSkin(PlayerTests.TIS_STEVE_UUID);
        Player alex = PlayerTests.initPlayerWithDefaultSkin(PlayerTests.JEB_ALEX_UUID);
        // converting to URI since URL#equals() does a DNS lookup (seriously?)
        URI steveUri = steve.getSkinUrl().toURI();
        URI alexUri = alex.getSkinUrl().toURI();
        assertThat(steveUri).isNotEqualTo(alexUri);
    }

}
