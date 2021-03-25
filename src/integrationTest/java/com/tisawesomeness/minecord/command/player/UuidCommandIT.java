package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.command.Result;
import com.tisawesomeness.minecord.config.ConfigReader;
import com.tisawesomeness.minecord.mc.player.Player;
import com.tisawesomeness.minecord.mc.player.Username;
import com.tisawesomeness.minecord.testutil.MiscTestUtils;
import com.tisawesomeness.minecord.testutil.mc.TestMCLibrary;
import com.tisawesomeness.minecord.testutil.mc.TestPlayerProvider;
import com.tisawesomeness.minecord.testutil.runner.TestCommandRunner;
import com.tisawesomeness.minecord.util.UUIDUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.tisawesomeness.minecord.testutil.runner.CommandAssertions.assertThat;

public class UuidCommandIT {

    private static final Username THROWING_USERNAME = new Username("throw");
    private static final Username TESTING_USERNAME = new Username("Tis_awesomeness");
    private static final UUID TESTING_UUID = UUID.fromString("f6489b79-7a9f-49e2-980e-265a05dbc3af");
    private static final Username SPACES_USERNAME = new Username("Will Wall");
    private static final UUID SPACES_UUID = UUID.fromString("661f3371-809f-4935-95ed-28351d9fe5d8");
    private static final UUID INVALID_UUID = UUID.fromString("f6489b79-7a9f-59e2-980e-265a05dbc3af");
    private static TestCommandRunner runner;

    @BeforeAll
    private static void initRunner() throws JsonProcessingException {
        runner = new TestCommandRunner(ConfigReader.readFromResources(), new UuidCommand());
        TestMCLibrary library = new TestMCLibrary();
        TestPlayerProvider playerProvider = library.getPlayerProvider();
        playerProvider.mapUuid(TESTING_USERNAME, TESTING_UUID);
        playerProvider.mapUuid(SPACES_USERNAME, SPACES_UUID);
        playerProvider.throwOnUsername(THROWING_USERNAME);
        runner.mcLibrary = library;
    }

    @Test
    @DisplayName("Uuid command with no args only requests help")
    public void testNoArgs() {
        assertThat(runner.run()).onlyShowsHelp();
    }

    @Test
    @DisplayName("Uuid command with too long username warns the user")
    public void testTooLong() {
        String args = MiscTestUtils.repeat("A", Username.MAX_LENGTH + 1);
        assertThat(runner.run(args))
                .awaitResult()
                .hasNotTriggeredCooldown()
                .resultIs(Result.WARNING);
    }

    @Test
    @DisplayName("Uuid command with non-ascii username warns the user")
    public void testNonAsciiUsername() {
        String args = "ooθoo";
        assertThat(runner.run(args))
                .awaitResult()
                .hasNotTriggeredCooldown()
                .resultIs(Result.WARNING);
    }

    @Test
    @DisplayName("Uuid command responds with error when an IOE is thrown")
    public void testIOE() {
        String args = THROWING_USERNAME.toString();
        assertThat(runner.run(args))
                .awaitResult()
                .resultIs(Result.ERROR)
                .embedRepliesIsEmpty();
    }

    @Test
    @DisplayName("Uuid command responds with success, even though the username doesn't exist")
    public void testNonExistentUsername() {
        String args = "DoesNotExist";
        assertThat(runner.run(args))
                .awaitResult()
                .hasTriggeredCooldown()
                .isSuccess();
    }

    @Test
    @DisplayName("Uuid command responds with success since an invalid UUID is treated as a username " +
            "(you never know, some troll could register the name if a glitch is found)")
    public void testInvalidUuid() {
        String args = INVALID_UUID.toString();
        assertThat(runner.run(args))
                .awaitResult()
                .hasNotTriggeredCooldown()
                .resultIs(Result.WARNING);
    }

    @Test
    @DisplayName("Uuid command has username in the title and both long and short UUIDs in the description")
    public void testUsername() {
        String args = TESTING_USERNAME.toString();
        assertThat(runner.run(args))
                .awaitResult()
                .isSuccess()
                .hasTriggeredCooldown()
                .asEmbedReply()
                .headerContains(TESTING_USERNAME)
                .headerLinksToAnyOf(Player.getNameMCUrlFor(TESTING_USERNAME), Player.getNameMCUrlFor(TESTING_UUID))
                .descriptionContains(
                        UUIDUtils.toShortString(TESTING_UUID),
                        UUIDUtils.toLongString(TESTING_UUID),
                        UUIDUtils.toIntArrayString(TESTING_UUID),
                        UUIDUtils.toMostLeastString(TESTING_UUID)
                );
    }

    @Test
    @DisplayName("Uuid command still works for usernames with spaces")
    public void testUsernameWithSpaces() {
        String args = SPACES_USERNAME.toString();
        assertThat(runner.run(args))
                .awaitResult()
                .isSuccess()
                .hasTriggeredCooldown()
                .asEmbedReply()
                .headerContains(SPACES_USERNAME)
                .headerLinksToAnyOf(Player.getNameMCUrlFor(SPACES_USERNAME), Player.getNameMCUrlFor(SPACES_UUID))
                .descriptionContains(
                        UUIDUtils.toShortString(SPACES_UUID),
                        UUIDUtils.toLongString(SPACES_UUID),
                        UUIDUtils.toIntArrayString(SPACES_UUID),
                        UUIDUtils.toMostLeastString(SPACES_UUID)
                );
    }

    @Test
    @DisplayName("Uuid command has uuid in the title and both long and short UUIDs in the description")
    public void testUuid() {
        String args = TESTING_UUID.toString();
        assertThat(runner.run(args))
                .awaitResult()
                .isSuccess()
                .hasTriggeredCooldown()
                .asEmbedReply()
                .headerContains(TESTING_UUID.toString())
                .headerLinksToAnyOf(Player.getNameMCUrlFor(TESTING_USERNAME), Player.getNameMCUrlFor(TESTING_UUID))
                .descriptionContains(
                        UUIDUtils.toShortString(TESTING_UUID),
                        UUIDUtils.toLongString(TESTING_UUID),
                        UUIDUtils.toIntArrayString(TESTING_UUID),
                        UUIDUtils.toMostLeastString(TESTING_UUID)
                );
    }

}
