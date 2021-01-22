package com.tisawesomeness.minecord.mc.player;

import com.tisawesomeness.minecord.testutil.PlayerTestUtils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;

public class PlayerTest {

    @Test
    @DisplayName("The default skin type is correct for Steve UUIDs")
    public void testGetDefaultSkinTypeSteve() {
        Player player = PlayerTestUtils.initPlayerWithDefaultSkin(PlayerTestUtils.STEVE_UUID);
        assertThat(player.getDefaultSkinType()).isEqualTo(SkinType.STEVE);
    }

    @Test
    @DisplayName("The default skin type is correct for Alex UUIDs")
    public void testGetDefaultSkinTypeAlex() {
        Player player = PlayerTestUtils.initPlayerWithDefaultSkin(PlayerTestUtils.ALEX_UUID);
        assertThat(player.getDefaultSkinType()).isEqualTo(SkinType.ALEX);
    }

    @Test
    @DisplayName("The skin type is correct for default skins with Steve UUIDs")
    public void testGetSkinTypeDefaultSteve() {
        Player player = PlayerTestUtils.initPlayerWithDefaultSkin(PlayerTestUtils.STEVE_UUID);
        assertThat(player.getSkinType()).isEqualTo(SkinType.STEVE);
    }

    @Test
    @DisplayName("The skin type is correct for default skins with Alex UUIDs")
    public void testGetSkinTypeDefaultAlex() {
        Player player = PlayerTestUtils.initPlayerWithDefaultSkin(PlayerTestUtils.ALEX_UUID);
        assertThat(player.getSkinType()).isEqualTo(SkinType.ALEX);
    }

    @Test
    @DisplayName("The skin type is correct for Steve skins")
    public void testGetSkinTypeSteve() {
        Player player = PlayerTestUtils.initPlayerWithSkinType(PlayerTestUtils.ALEX_UUID, SkinType.STEVE);
        assertThat(player.getSkinType()).isEqualTo(SkinType.STEVE);
    }

    @Test
    @DisplayName("The skin type is correct for Alex skins")
    public void testGetSkinTypeAlex() {
        Player player = PlayerTestUtils.initPlayerWithSkinType(PlayerTestUtils.STEVE_UUID, SkinType.ALEX);
        assertThat(player.getSkinType()).isEqualTo(SkinType.ALEX);
    }

    @Test
    @DisplayName("The skin URL of a custom skin is returned")
    public void testCustomSkin() throws URISyntaxException {
        Player player = PlayerTestUtils.initPlayerWithSkinType(PlayerTestUtils.STEVE_UUID, SkinType.STEVE);
        assertThat(player.getSkinUrl().toURI()).isEqualTo(PlayerTestUtils.CUSTOM_SKIN_URL.toURI());
    }

    @Test
    @DisplayName("Player with non-default skin URL has custom skin")
    public void testHasCustomSkin() {
        Player player = PlayerTestUtils.initPlayerWithSkinType(PlayerTestUtils.STEVE_UUID, SkinType.STEVE);
        assertThat(player.hasCustomSkin()).isTrue();
    }

    @Test
    @DisplayName("Player with steve skin URL does not have custom skin")
    public void testSteveIsNotCustomSkin() {
        Player player = PlayerTestUtils.initPlayerWithDefaultSkinUrl(PlayerTestUtils.STEVE_UUID, SkinType.STEVE);
        assertThat(player.hasCustomSkin()).isFalse();
    }

    @Test
    @DisplayName("Player with alex skin URL does not have custom skin")
    public void testAlexIsNotCustomSkin() {
        Player player = PlayerTestUtils.initPlayerWithDefaultSkinUrl(PlayerTestUtils.ALEX_UUID, SkinType.ALEX);
        assertThat(player.hasCustomSkin()).isFalse();
    }

    @Test
    @DisplayName("Player with steve skin URL but alex UUID has custom skin")
    public void testSteveAlexIsCustomSkin() {
        Player player = PlayerTestUtils.initPlayerWithDefaultSkinUrl(PlayerTestUtils.ALEX_UUID, SkinType.STEVE);
        assertThat(player.hasCustomSkin()).isTrue();
    }

    @Test
    @DisplayName("Player with alex skin URL but skin UUID has custom skin")
    public void testAlexSteveIsCustomSkin() {
        Player player = PlayerTestUtils.initPlayerWithDefaultSkinUrl(PlayerTestUtils.STEVE_UUID, SkinType.ALEX);
        assertThat(player.hasCustomSkin()).isTrue();
    }

    @Test
    @DisplayName("The URLs of the default steve and alex URLs are different")
    public void testDefaultSteveAndAlexSkins() throws URISyntaxException {
        Player steve = PlayerTestUtils.initPlayerWithDefaultSkin(PlayerTestUtils.STEVE_UUID);
        Player alex = PlayerTestUtils.initPlayerWithDefaultSkin(PlayerTestUtils.ALEX_UUID);
        // converting to URI since URL#equals() does a DNS lookup (seriously?)
        URI steveUri = steve.getSkinUrl().toURI();
        URI alexUri = alex.getSkinUrl().toURI();
        assertThat(steveUri).isNotEqualTo(alexUri);
    }

}
