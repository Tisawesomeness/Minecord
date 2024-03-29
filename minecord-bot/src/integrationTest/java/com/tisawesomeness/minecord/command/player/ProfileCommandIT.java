package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.command.meta.Result;
import com.tisawesomeness.minecord.mc.player.AccountStatus;
import com.tisawesomeness.minecord.mc.player.Player;
import com.tisawesomeness.minecord.mc.player.Username;
import com.tisawesomeness.minecord.testutil.PlayerTests;
import com.tisawesomeness.minecord.testutil.Resources;
import com.tisawesomeness.minecord.testutil.mc.TestMCLibrary;
import com.tisawesomeness.minecord.testutil.mc.TestPlayerProvider;
import com.tisawesomeness.minecord.testutil.runner.TestCommandRunner;
import com.tisawesomeness.minecord.util.Strings;
import com.tisawesomeness.minecord.util.UUIDs;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.UUID;

import static com.tisawesomeness.minecord.testutil.assertion.CustomAssertions.assertThat;

public class ProfileCommandIT {

    private static final Username THROWING_USERNAME = new Username("yeet");
    private static final UUID THROWING_UUID = UUID.fromString("4b03cde5-bfb7-4680-8c0e-a9769f002e1e");

    private static final Username LONG_HISTORY_NAME = new Username("TeraStella");
    private static final UUID LONG_HISTORY_UUID = UUID.fromString("38550ae0-706e-4bb5-b12e-d00c48e2f482");

    private static final Username SHORT_HISTORY_NAME = new Username("Tis_awesomeness");
    private static final UUID SHORT_HISTORY_UUID = UUID.fromString("f6489b79-7a9f-49e2-980e-265a05dbc3af");

    private static final Username ACCOUNT_STATUS_NAME = new Username("SeeSaw");
    private static final UUID ACCOUNT_STATUS_UUID = UUID.fromString("c7b3d49c-580c-4af2-a824-ca07b37ff2f9");
    private static final AccountStatus ACCOUNT_STATUS = AccountStatus.MIGRATED_MICROSOFT;

    private static final Player PHD_PLAYER = PlayerTests.initPHDPlayer();

    private static TestCommandRunner runner;

    @BeforeAll
    public static void initRunner() throws IOException {
        runner = new TestCommandRunner(Resources.config(), new ProfileCommand());
        TestMCLibrary library = new TestMCLibrary();
        TestPlayerProvider playerProvider = library.getPlayerProvider();

        playerProvider.throwOnUsername(THROWING_USERNAME);
        playerProvider.throwOnUuid(THROWING_UUID);

        Player shortHistoryPlayer = PlayerTests.initPlayerWithDefaultSkin(SHORT_HISTORY_UUID, SHORT_HISTORY_NAME);
        playerProvider.mapUuid(SHORT_HISTORY_NAME, SHORT_HISTORY_UUID);
        playerProvider.mapPlayer(shortHistoryPlayer);

        Player longHistoryPlayer = PlayerTests.initPlayerWithDefaultSkin(LONG_HISTORY_UUID, LONG_HISTORY_NAME);
        playerProvider.mapUuid(LONG_HISTORY_NAME, LONG_HISTORY_UUID);
        playerProvider.mapPlayer(longHistoryPlayer);

        Player accountStatusPlayer = PlayerTests.initPlayerWithDefaultSkin(ACCOUNT_STATUS_UUID, ACCOUNT_STATUS_NAME);
        playerProvider.mapPlayer(accountStatusPlayer);
        playerProvider.mapStatus(ACCOUNT_STATUS_UUID, ACCOUNT_STATUS);

        playerProvider.mapPlayer(PHD_PLAYER);

        runner.mcLibrary = library;
    }

    @Test
    @DisplayName("Profile command with no args only requests help")
    public void testNoArgs() {
        assertThat(runner.run()).onlyShowsHelp();
    }

    @Test
    @DisplayName("Profile command with too long username warns the user")
    public void testTooLong() {
        String args = Strings.repeat("A", Username.MAX_LENGTH + 1);
        assertThat(runner.run(args))
                .awaitResult()
                .hasNotTriggeredCooldown()
                .resultIs(Result.WARNING);
    }

    @Test
    @DisplayName("Profile command with non-ascii username warns the user")
    public void testNonAsciiUsername() {
        String args = "ooθoo";
        assertThat(runner.run(args))
                .awaitResult()
                .hasNotTriggeredCooldown()
                .resultIs(Result.WARNING);
    }

    @Test
    @DisplayName("Profile command responds with error when an IOE from username is thrown")
    public void testIOEUsername() {
        String args = THROWING_USERNAME.toString();
        assertThat(runner.run(args))
                .awaitResult()
                .resultIs(Result.ERROR)
                .embedRepliesIsEmpty();
    }

    @Test
    @DisplayName("Profile command responds with error when an IOE from uuid is thrown")
    public void testIOEUuid() {
        String args = THROWING_UUID.toString();
        assertThat(runner.run(args))
                .awaitResult()
                .resultIs(Result.ERROR)
                .embedRepliesIsEmpty();
    }

    @Test
    @DisplayName("Profile command responds with success, even though the username doesn't exist")
    public void testNonExistentUsername() {
        String args = "DoesNotExist";
        assertThat(runner.run(args))
                .awaitResult()
                .hasTriggeredCooldown()
                .isSuccess();
    }

    @Test
    @DisplayName("Profile command responds with success, even though the uuid doesn't exist")
    public void testNonExistentUuid() {
        String args = UUID.fromString("38550ae0-706e-4bb5-b12e-d00c48e2f483").toString();
        assertThat(runner.run(args))
                .awaitResult()
                .hasTriggeredCooldown()
                .isSuccess();
    }

    @Test
    @DisplayName("Profile command works with short history")
    public void testShortHistory() {
        String args = SHORT_HISTORY_UUID.toString();
        assertThat(runner.run(args))
                .awaitResult()
                .hasTriggeredCooldown()
                .isSuccess()
                .asEmbedReply()
                .headerContains(SHORT_HISTORY_NAME)
                .headerLinksToAnyOf(Player.getNameMCUrlFor(SHORT_HISTORY_NAME), Player.getNameMCUrlFor(SHORT_HISTORY_UUID));
    }

    @Test
    @DisplayName("Profile command works with long history")
    public void testLongHistory() {
        String args = LONG_HISTORY_UUID.toString();
        assertThat(runner.run(args))
                .awaitResult()
                .hasTriggeredCooldown()
                .isSuccess()
                .asEmbedReply()
                .headerContains(LONG_HISTORY_NAME)
                .headerLinksToAnyOf(Player.getNameMCUrlFor(LONG_HISTORY_NAME), Player.getNameMCUrlFor(LONG_HISTORY_UUID));
    }

    @Test
    @DisplayName("Profile command works with account status")
    public void testAccountStatus() {
        String args = ACCOUNT_STATUS_UUID.toString();
        assertThat(runner.run(args))
                .awaitResult()
                .hasTriggeredCooldown()
                .isSuccess()
                .asEmbedReply()
                .hasFieldWithName("Account")
                .fieldsContains("Microsoft", "Migrated");
    }

    @Test
    @DisplayName("Profile command shows message if player is PHD")
    public void testPHD() {
        String args = UUIDs.toShortString(PHD_PLAYER.getUuid());
        assertThat(runner.run(args))
                .awaitReply()
                .hasTriggeredCooldown()
                .isSuccess()
                .embedRepliesIsEmpty()
                .asReply()
                .contains("PHD");
    }

}
