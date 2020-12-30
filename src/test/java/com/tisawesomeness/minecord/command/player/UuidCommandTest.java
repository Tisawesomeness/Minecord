package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.command.Result;
import com.tisawesomeness.minecord.command.runner.TestCommandRunner;
import com.tisawesomeness.minecord.command.runner.mc.TestMCLibrary;
import com.tisawesomeness.minecord.command.runner.mc.TestPlayerProvider;
import com.tisawesomeness.minecord.config.ConfigReader;
import com.tisawesomeness.minecord.mc.player.Username;
import com.tisawesomeness.minecord.util.UUIDUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.tisawesomeness.minecord.command.runner.TestContextAssert.assertThat;

public class UuidCommandTest {

    private static final Username THROWING_USERNAME = Username.fromAny("throw");
    private static final Username TESTING_USERNAME = Username.fromAny("Tis_awesomeness");
    private static final UUID TESTING_UUID = UUID.fromString("f6489b79-7a9f-49e2-980e-265a05dbc3af");
    private static final Username SPACES_USERNAME = Username.fromAny("Will Wall");
    private static final UUID SPACES_UUID = UUID.fromString("661f3371-809f-4935-95ed-28351d9fe5d8");
    private static TestCommandRunner runner;

    @BeforeAll
    private static void initRunner() throws JsonProcessingException {
        runner = new TestCommandRunner(ConfigReader.readFromResources(), new UuidCommand());
        TestMCLibrary library = new TestMCLibrary();
        TestPlayerProvider playerProvider = library.getPlayerProvider();
        playerProvider.mapUuid(TESTING_USERNAME, TESTING_UUID);
        playerProvider.mapUuid(SPACES_USERNAME, SPACES_UUID);
        playerProvider.throwOnUsername(THROWING_USERNAME);
        runner.library = library;
    }

    @Test
    @DisplayName("Uuid command with no args only requests help")
    public void testNoArgs() {
        assertThat(runner.run()).onlyShowsHelp();
    }

    @Test
    @DisplayName("Uuid command with too long username warns the user")
    public void testTooLong() {
        String args = "A".repeat(Username.MAX_LENGTH + 1);
        assertThat(runner.run(args)).resultIs(Result.WARNING);
    }

    @Test
    @DisplayName("Uuid command with non-ascii username warns the user")
    public void testNonAsciiUsername() {
        String args = "ooθoo";
        assertThat(runner.run(args)).resultIs(Result.WARNING);
    }

    @Test
    @DisplayName("Uuid command responds with error when an IOE is thrown")
    public void testIOE() {
        String args = THROWING_USERNAME.toString();
        assertThat(runner.run(args))
                .resultIs(Result.ERROR)
                .embedRepliesIsEmpty();
    }

    @Test
    @DisplayName("Uuid command responds with success, even though the username doesn't exist")
    public void testNonExistentUsername() {
        String args = "DoesNotExist";
        assertThat(runner.run(args)).resultIs(Result.SUCCESS);
    }

    @Test
    @DisplayName("Uuid command has username in the title and both long and short UUIDs in the description")
    public void testUsername() {
        String args = TESTING_USERNAME.toString();
        assertThat(runner.run(args))
                .resultIs(Result.SUCCESS)
                .hasTriggeredCooldown()
                .asEmbedReply()
                .headerContains(TESTING_USERNAME)
                .descriptionContains(UUIDUtils.toShortString(TESTING_UUID), UUIDUtils.toLongString(TESTING_UUID));
    }

    @Test
    @DisplayName("Uuid command still works for usernames with spaces")
    public void testUsernameWithSpaces() {
        String args = SPACES_USERNAME.toString();
        assertThat(runner.run(args))
                .resultIs(Result.SUCCESS)
                .hasTriggeredCooldown()
                .asEmbedReply()
                .headerContains(SPACES_USERNAME)
                .descriptionContains(UUIDUtils.toShortString(SPACES_UUID), UUIDUtils.toLongString(SPACES_UUID));
    }

}
