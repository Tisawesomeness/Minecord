package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.command.Result;
import com.tisawesomeness.minecord.config.ConfigReader;
import com.tisawesomeness.minecord.mc.player.*;
import com.tisawesomeness.minecord.testutil.mc.MockMojangAPI;
import com.tisawesomeness.minecord.testutil.mc.TestMCLibrary;
import com.tisawesomeness.minecord.testutil.mc.TestPlayerProvider;
import com.tisawesomeness.minecord.testutil.runner.TestCommandRunner;
import com.tisawesomeness.minecord.util.RequestUtils;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.tisawesomeness.minecord.testutil.runner.TestContextAssert.assertThat;

public class HistoryCommandIT {

    private static final Username THROWING_USERNAME = new Username("yeet");
    private static final UUID THROWING_UUID = UUID.fromString("4b03cde5-bfb7-4680-8c0e-a9769f002e1e");

    private static final Username LONG_HISTORY_NAME = new Username("TeraStella");
    private static final UUID LONG_HISTORY_UUID = UUID.fromString("38550ae0-706e-4bb5-b12e-d00c48e2f482");
    private static List<NameChange> longHistory;

    private static final Username SHORT_HISTORY_NAME = new Username("Tis_awesomeness");
    private static final UUID SHORT_HISTORY_UUID = UUID.fromString("f6489b79-7a9f-49e2-980e-265a05dbc3af");
    private static final List<NameChange> SHORT_HISTORY = List.of(
            NameChange.withTimestamp(SHORT_HISTORY_NAME, 1438695830000L),
            NameChange.original(new Username("tis_awesomeness"))
    );

    private static final Profile DUMMY_PROFILE = new Profile(false, false, SkinType.STEVE, null, null);

    private static TestCommandRunner runner;

    @BeforeAll
    private static void initRunner() throws IOException {
        runner = new TestCommandRunner(ConfigReader.readFromResources(), new HistoryCommand());
        TestMCLibrary library = new TestMCLibrary();
        TestPlayerProvider playerProvider = library.getPlayerProvider();

        playerProvider.throwOnUsername(THROWING_USERNAME);
        playerProvider.throwOnUuid(THROWING_UUID);

        Player shortHistoryPlayer = new Player(SHORT_HISTORY_UUID, SHORT_HISTORY_NAME, SHORT_HISTORY, DUMMY_PROFILE);
        playerProvider.mapUuid(SHORT_HISTORY_NAME, SHORT_HISTORY_UUID);
        playerProvider.mapPlayer(SHORT_HISTORY_UUID, shortHistoryPlayer);

        longHistory = getLongHistory();
        Player longHistoryPlayer = new Player(LONG_HISTORY_UUID, LONG_HISTORY_NAME, longHistory, DUMMY_PROFILE);
        playerProvider.mapUuid(LONG_HISTORY_NAME, LONG_HISTORY_UUID);
        playerProvider.mapPlayer(LONG_HISTORY_UUID, longHistoryPlayer);

        runner.mcLibrary = library;
    }
    private static List<NameChange> getLongHistory() throws IOException {
        MockMojangAPI api = new MockMojangAPI();
        String response = RequestUtils.loadResource("TeraStellaHistory.json");
        api.mapNameHistory(LONG_HISTORY_UUID, response);
        return api.getNameHistory(LONG_HISTORY_UUID);
    }

    @Test
    @DisplayName("History command with no args only requests help")
    public void testNoArgs() {
        assertThat(runner.run()).onlyShowsHelp();
    }

    @Test
    @DisplayName("History command with too long username warns the user")
    public void testTooLong() {
        String args = "A".repeat(Username.MAX_LENGTH + 1);
        assertThat(runner.run(args))
                .awaitResult()
                .hasNotTriggeredCooldown()
                .resultIs(Result.WARNING);
    }

    @Test
    @DisplayName("History command with non-ascii username warns the user")
    public void testNonAsciiUsername() {
        String args = "ooθoo";
        assertThat(runner.run(args))
                .awaitResult()
                .hasNotTriggeredCooldown()
                .resultIs(Result.WARNING);
    }

    @Test
    @DisplayName("History command responds with error when an IOE from username is thrown")
    public void testIOEUsername() {
        String args = THROWING_USERNAME.toString();
        assertThat(runner.run(args))
                .awaitResult()
                .resultIs(Result.ERROR)
                .embedRepliesIsEmpty();
    }

    @Test
    @DisplayName("History command responds with error when an IOE from uuid is thrown")
    public void testIOEUuid() {
        String args = THROWING_UUID.toString();
        assertThat(runner.run(args))
                .awaitResult()
                .resultIs(Result.ERROR)
                .embedRepliesIsEmpty();
    }

    @Test
    @DisplayName("History command responds with success, even though the username doesn't exist")
    public void testNonExistentUsername() {
        String args = "DoesNotExist";
        assertThat(runner.run(args))
                .awaitResult()
                .hasTriggeredCooldown()
                .resultIs(Result.SUCCESS);
    }

    @Test
    @DisplayName("History command responds with success, even though the uuid doesn't exist")
    public void testNonExistentUuid() {
        String args = UUID.fromString("38550ae0-706e-4bb5-b12e-d00c48e2f483").toString();
        assertThat(runner.run(args))
                .awaitResult()
                .hasTriggeredCooldown()
                .resultIs(Result.SUCCESS);
    }

    @Test
    @DisplayName("History command responds with success, even though the uuid doesn't exist")
    public void testShortHistory() {
        List<Username> nameOrder = SHORT_HISTORY.stream()
                .map(NameChange::getUsername)
                .collect(Collectors.toList());
        String args = SHORT_HISTORY_UUID.toString();
        assertThat(runner.run(args))
                .awaitResult()
                .hasTriggeredCooldown()
                .resultIs(Result.SUCCESS)
                .asEmbedReply()
                .headerContains(SHORT_HISTORY_NAME)
                .headerLinksToAnyOf(Player.getNameMCUrlFor(SHORT_HISTORY_NAME), Player.getNameMCUrlFor(SHORT_HISTORY_UUID))
                .descriptionContainsSubsequence(nameOrder);
    }

    @Test
    @DisplayName("History command responds with success, even though the uuid doesn't exist")
    public void testLongHistory() {
        List<Username> nameOrder = longHistory.stream()
                .map(NameChange::getUsername)
                .collect(Collectors.toList());
        String args = LONG_HISTORY_UUID.toString();
        assertThat(runner.run(args))
                .awaitResult()
                .hasTriggeredCooldown()
                .resultIs(Result.SUCCESS)
                .asEmbedReply()
                .headerContains(LONG_HISTORY_NAME)
                .headerLinksToAnyOf(Player.getNameMCUrlFor(LONG_HISTORY_NAME), Player.getNameMCUrlFor(LONG_HISTORY_UUID))
                .fieldsContainsSubsequence(nameOrder);
    }

}
