package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.command.meta.Result;
import com.tisawesomeness.minecord.config.config.Config;
import com.tisawesomeness.minecord.mc.player.Player;
import com.tisawesomeness.minecord.mc.player.Username;
import com.tisawesomeness.minecord.testutil.PlayerTests;
import com.tisawesomeness.minecord.testutil.Resources;
import com.tisawesomeness.minecord.testutil.mc.TestMCLibrary;
import com.tisawesomeness.minecord.testutil.mc.TestPlayerProvider;
import com.tisawesomeness.minecord.testutil.runner.TestCommandRunner;
import com.tisawesomeness.minecord.util.Strings;
import com.tisawesomeness.minecord.util.UUIDs;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.net.URL;

import static com.tisawesomeness.minecord.testutil.assertion.CustomAssertions.assertThat;

public class CapeCommandIT {

    private static final Username THROWING_USERNAME = new Username("throw");
    private static Config config;
    private static TestCommandRunner runner;

    @BeforeAll
    private static void initRunner() throws JsonProcessingException {
        config = Resources.config();
        runner = new TestCommandRunner(config, new CapeCommand());
        TestMCLibrary library = new TestMCLibrary();
        TestPlayerProvider playerProvider = library.getPlayerProvider();
        playerProvider.throwOnUsername(THROWING_USERNAME);
        runner.mcLibrary = library;
    }

    @Test
    @DisplayName("Cape command with no args only requests help")
    public void testNoArgs() {
        assertThat(runner.run()).onlyShowsHelp();
    }

    @Test
    @DisplayName("Cape command with too long username warns the user")
    public void testTooLong() {
        String args = Strings.repeat("A", Username.MAX_LENGTH + 1);
        assertThat(runner.run(args))
                .awaitResult()
                .hasNotTriggeredCooldown()
                .resultIs(Result.WARNING);
    }

    @Test
    @DisplayName("Cape command with non-ascii username warns the user")
    public void testNonAsciiUsername() {
        String args = "ooθoo";
        assertThat(runner.run(args))
                .awaitResult()
                .hasNotTriggeredCooldown()
                .resultIs(Result.WARNING);
    }

    @Test
    @DisplayName("Cape command responds with error when an IOE is thrown")
    public void testIOE() {
        String args = THROWING_USERNAME.toString();
        assertThat(runner.run(args))
                .awaitResult()
                .resultIs(Result.ERROR)
                .embedRepliesIsEmpty();
    }

    @Test
    @DisplayName("Cape command responds with success, even though the username doesn't exist")
    public void testNonExistentUsername() {
        String args = "DoesNotExist";
        assertThat(runner.run(args))
                .awaitResult()
                .hasTriggeredCooldown()
                .isSuccess();
    }

    @Test
    @DisplayName("Cape command responds with success when requesting a valid player with no cape")
    public void testNoCape() throws URISyntaxException {
        Player player = PlayerTests.initPlayerWithDefaultSkin(PlayerTests.TIS_STEVE_UUID);
        TestCommandRunner runner = createRunner(player, OptifineCape.NO);
        String args = UUIDs.toShortString(player.getUuid());
        assertThat(runner.run(args))
                .awaitResult()
                .hasTriggeredCooldown()
                .isSuccess()
                .embedRepliesIsEmpty();
    }
    @Test
    @DisplayName("Cape command responds with success when requesting a player with minecraft cape")
    public void testMinecraftCape() throws URISyntaxException {
        Player player = PlayerTests.initPlayerWithCape(PlayerTests.JEB_ALEX_UUID, PlayerTests.MOJANG_CAPE_URL);
        TestCommandRunner runner = createRunner(player, OptifineCape.NO);
        String args = UUIDs.toShortString(player.getUuid());
        assertThat(runner.run(args))
                .awaitEmbedReply()
                .hasTriggeredCooldown()
                .isSuccess()
                .repliesIsEmpty()
                .asEmbedReply()
                .imageLinksTo(PlayerTests.MOJANG_CAPE_URL)
                .headerContains(player.getUsername())
                .headerLinksToAnyOf(getNameMCUrls(player));
    }
    @Test
    @DisplayName("Cape command responds with success when requesting a player with optifine cape")
    public void testOptifineCape() throws URISyntaxException {
        Player player = PlayerTests.initPlayerWithDefaultSkin(PlayerTests.TIS_STEVE_UUID);
        TestCommandRunner runner = createRunner(player, OptifineCape.YES);
        String args = UUIDs.toShortString(player.getUuid());
        assertThat(runner.run(args))
                .awaitEmbedReply()
                .hasTriggeredCooldown()
                .isSuccess()
                .repliesIsEmpty()
                .asEmbedReply()
                .imageLinksTo(player.getOptifineCapeUrl())
                .headerContains(player.getUsername())
                .headerLinksToAnyOf(getNameMCUrls(player));
    }
    @Test
    @DisplayName("Cape command responds with success when requesting a player with minecraft and optifine capes")
    public void testBothCapes() throws URISyntaxException {
        Player player = PlayerTests.initPlayerWithCape(PlayerTests.JEB_ALEX_UUID, PlayerTests.MOJANG_CAPE_URL);
        TestCommandRunner runner = createRunner(player, OptifineCape.YES);
        String args = UUIDs.toShortString(player.getUuid());
        assertThat(runner.run(args))
                .awaitEmbedReplies(2)
                .hasTriggeredCooldown()
                .isSuccess()
                .repliesIsEmpty()
                .embedRepliesSatisfy(emb -> assertThat(emb)
                        .headerContains(player.getUsername())
                        .headerLinksToAnyOf(getNameMCUrls(player)))
                .anyEmbedReplySatisfies(emb -> assertThat(emb)
                        .imageLinksTo(PlayerTests.MOJANG_CAPE_URL))
                .anyEmbedReplySatisfies(emb -> assertThat(emb)
                        .imageLinksTo(player.getOptifineCapeUrl()));
    }
    @Test
    @DisplayName("Cape command responds with optifine failure but general success when optifine IOE fails")
    public void testOptifineCapeThrow() throws URISyntaxException {
        Player player = PlayerTests.initPlayerWithDefaultSkin(PlayerTests.TIS_STEVE_UUID);
        TestCommandRunner runner = createRunner(player, OptifineCape.THROW);
        String args = UUIDs.toShortString(player.getUuid());
        assertThat(runner.run(args))
                .awaitReplies(2)
                .isSuccess()
                .hasTriggeredCooldown()
                .embedRepliesIsEmpty();
    }

    @Test
    @DisplayName("Cape command shows message if player is PHD")
    public void testPHD() throws URISyntaxException {
        Player player = PlayerTests.initPHDPlayer();
        TestCommandRunner runner = createRunner(player, OptifineCape.NO);
        String args = UUIDs.toShortString(player.getUuid());
        assertThat(runner.run(args))
                .awaitReply()
                .hasTriggeredCooldown()
                .isSuccess()
                .embedRepliesIsEmpty()
                .asReply()
                .contains("PHD");
    }

    private static TestCommandRunner createRunner(Player player, OptifineCape capeStatus) throws URISyntaxException {
        TestCommandRunner runner = new TestCommandRunner(config, new CapeCommand());
        TestMCLibrary library = new TestMCLibrary();
        switch (capeStatus) {
            case NO:
                break;
            case YES:
                library.getClient().addUrlThatExists(player.getOptifineCapeUrl());
                break;
            case THROW:
                library.getClient().addThrowingUrl(player.getOptifineCapeUrl());
                break;
        }
        TestPlayerProvider playerProvider = library.getPlayerProvider();
        playerProvider.mapPlayer(player);
        runner.mcLibrary = library;
        return runner;
    }
    private enum OptifineCape {
        NO, YES, THROW
    }

    private static URL[] getNameMCUrls(Player player) {
        return new URL[]{Player.getNameMCUrlFor(player.getUsername()), Player.getNameMCUrlFor(player.getUuid())};
    }

}
