package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.command.meta.Result;
import com.tisawesomeness.minecord.mc.player.Player;
import com.tisawesomeness.minecord.mc.player.SkinType;
import com.tisawesomeness.minecord.mc.player.Username;
import com.tisawesomeness.minecord.testutil.PlayerTests;
import com.tisawesomeness.minecord.testutil.Resources;
import com.tisawesomeness.minecord.testutil.mc.TestMCLibrary;
import com.tisawesomeness.minecord.testutil.mc.TestPlayerProvider;
import com.tisawesomeness.minecord.testutil.runner.TestCommandRunner;
import com.tisawesomeness.minecord.util.Strings;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.UUID;

import static com.tisawesomeness.minecord.testutil.runner.CommandAssertions.assertThat;

public class SkinCommandIT {

    private static final Username THROWING_USERNAME = new Username("yeet");
    private static final UUID THROWING_UUID = UUID.fromString("4b03cde5-bfb7-4680-8c0e-a9769f002e1e");

    private static final Username STEVE_USERNAME = new Username("Steve");
    private static final Player STEVE_PLAYER = PlayerTests.initPlayerWithDefaultSkinUrl(
            PlayerTests.TIS_STEVE_UUID, STEVE_USERNAME, SkinType.STEVE);

    private static final Username ALEX_USERNAME = new Username("Alex");
    private static final Player ALEX_PLAYER = PlayerTests.initPlayerWithDefaultSkinUrl(
            PlayerTests.JEB_ALEX_UUID, ALEX_USERNAME, SkinType.ALEX);

    private static final Username CUSTOM_USERNAME = new Username("Custom");
    private static final UUID CUSTOM_UUID = UUID.fromString("9c2ac958-5de9-45a8-8ca1-4122eb4c0b9e");
    private static final Player CUSTOM_PLAYER = PlayerTests.initPlayerWithSkinType(
            CUSTOM_UUID, CUSTOM_USERNAME, SkinType.STEVE);

    private static TestCommandRunner runner;

    @BeforeAll
    private static void initRunner() throws IOException {
        runner = new TestCommandRunner(Resources.config(), new SkinCommand());
        TestMCLibrary library = new TestMCLibrary();
        TestPlayerProvider playerProvider = library.getPlayerProvider();

        playerProvider.throwOnUsername(THROWING_USERNAME);
        playerProvider.throwOnUuid(THROWING_UUID);

        playerProvider.mapUuid(STEVE_USERNAME, PlayerTests.TIS_STEVE_UUID);
        playerProvider.mapPlayer(PlayerTests.TIS_STEVE_UUID, STEVE_PLAYER);
        playerProvider.mapUuid(ALEX_USERNAME, PlayerTests.JEB_ALEX_UUID);
        playerProvider.mapPlayer(PlayerTests.JEB_ALEX_UUID, ALEX_PLAYER);
        playerProvider.mapUuid(CUSTOM_USERNAME, CUSTOM_UUID);
        playerProvider.mapPlayer(CUSTOM_UUID, CUSTOM_PLAYER);

        runner.mcLibrary = library;
    }

    @Test
    @DisplayName("Skin command with no args only requests help")
    public void testNoArgs() {
        assertThat(runner.run()).onlyShowsHelp();
    }

    @Test
    @DisplayName("Skin command with too long username warns the user")
    public void testTooLong() {
        String args = Strings.repeat("A", Username.MAX_LENGTH + 1);
        assertThat(runner.run(args))
                .awaitResult()
                .hasNotTriggeredCooldown()
                .resultIs(Result.WARNING);
    }

    @Test
    @DisplayName("Skin command with non-ascii username warns the user")
    public void testNonAsciiUsername() {
        String args = "ooθoo";
        assertThat(runner.run(args))
                .awaitResult()
                .hasNotTriggeredCooldown()
                .resultIs(Result.WARNING);
    }

    @Test
    @DisplayName("Skin command responds with error when an IOE from username is thrown")
    public void testIOEUsername() {
        String args = THROWING_USERNAME.toString();
        assertThat(runner.run(args))
                .awaitResult()
                .resultIs(Result.ERROR)
                .embedRepliesIsEmpty();
    }

    @Test
    @DisplayName("Skin command responds with error when an IOE from uuid is thrown")
    public void testIOEUuid() {
        String args = THROWING_UUID.toString();
        assertThat(runner.run(args))
                .awaitResult()
                .resultIs(Result.ERROR)
                .embedRepliesIsEmpty();
    }

    @Test
    @DisplayName("Skin command responds with success, even though the username doesn't exist")
    public void testNonExistentUsername() {
        String args = "DoesNotExist";
        assertThat(runner.run(args))
                .awaitResult()
                .hasTriggeredCooldown()
                .isSuccess();
    }

    @Test
    @DisplayName("Skin command works with steve skin")
    public void testSteveSkin() {
        String args = STEVE_USERNAME.toString();
        assertThat(runner.run(args))
                .awaitResult()
                .hasTriggeredCooldown()
                .isSuccess()
                .asEmbedReply()
                .headerContains(STEVE_USERNAME)
                .headerLinksTo(STEVE_PLAYER.getMCSkinHistoryUrl())
                .imageLinksTo(STEVE_PLAYER.getSkinUrl());
    }

    @Test
    @DisplayName("Skin command works with alex skin")
    public void testAlexSkin() {
        String args = ALEX_USERNAME.toString();
        assertThat(runner.run(args))
                .awaitResult()
                .hasTriggeredCooldown()
                .isSuccess()
                .asEmbedReply()
                .headerContains(ALEX_USERNAME)
                .headerLinksTo(ALEX_PLAYER.getMCSkinHistoryUrl())
                .imageLinksTo(ALEX_PLAYER.getSkinUrl());
    }

    @Test
    @DisplayName("Skin command works with custom skin")
    public void testCustomSkin() {
        String args = CUSTOM_USERNAME.toString();
        assertThat(runner.run(args))
                .awaitResult()
                .hasTriggeredCooldown()
                .isSuccess()
                .asEmbedReply()
                .headerContains(CUSTOM_USERNAME)
                .headerLinksTo(CUSTOM_PLAYER.getMCSkinHistoryUrl())
                .imageLinksTo(CUSTOM_PLAYER.getSkinUrl());
    }

}
