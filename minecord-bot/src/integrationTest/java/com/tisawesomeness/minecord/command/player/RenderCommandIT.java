package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.command.meta.Command;
import com.tisawesomeness.minecord.command.meta.Result;
import com.tisawesomeness.minecord.config.config.Config;
import com.tisawesomeness.minecord.mc.player.Player;
import com.tisawesomeness.minecord.mc.player.Render;
import com.tisawesomeness.minecord.mc.player.RenderType;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.EnumMap;
import java.util.Map;

import static com.tisawesomeness.minecord.testutil.assertion.CustomAssertions.assertThat;

public class RenderCommandIT {

    private static final Username THROWING_USERNAME = new Username("throw");
    private static final Map<RenderType, TestCommandRunner> runners = new EnumMap<>(RenderType.class);

    @BeforeAll
    private static void initRunners() throws JsonProcessingException {
        Config config = Resources.config();
        for (RenderType type : RenderType.values()) {
            runners.put(type, initRunner(type, config));
        }
    }
    private static TestCommandRunner initRunner(RenderType type, Config config) {
        Command cmd = new RenderCommand(type);
        TestCommandRunner runner = new TestCommandRunner(config, cmd);
        TestMCLibrary library = new TestMCLibrary();
        TestPlayerProvider playerProvider = library.getPlayerProvider();

        playerProvider.throwOnUsername(THROWING_USERNAME);
        playerProvider.mapUuid(new Username("abc"), PlayerTests.TIS_STEVE_UUID);

        playerProvider.mapPlayer(PlayerTests.initPHDPlayer());

        runner.mcLibrary = library;
        return runner;
    }

    @ParameterizedTest
    @EnumSource
    @DisplayName("Render commands with no args only requests help")
    public void testNoArgs(RenderType type) {
        assertThat(runners.get(type).run()).onlyShowsHelp();
    }

    @ParameterizedTest
    @EnumSource
    @DisplayName("Render commands with too long username warns the user")
    public void testTooLong(RenderType type) {
        String args = Strings.repeat("A", Username.MAX_LENGTH + 1);
        assertThat(runners.get(type).run(args))
                .awaitResult()
                .hasNotTriggeredCooldown()
                .resultIs(Result.WARNING);
    }

    @ParameterizedTest
    @EnumSource
    @DisplayName("Render commands with non-ascii username warns the user")
    public void testNonAsciiUsername(RenderType type) {
        String args = "ooθoo";
        assertThat(runners.get(type).run(args))
                .awaitResult()
                .hasNotTriggeredCooldown()
                .resultIs(Result.WARNING);
    }

    @ParameterizedTest
    @EnumSource
    @DisplayName("Render commands responds with error when an IOE is thrown")
    public void testIOE(RenderType type) {
        String args = THROWING_USERNAME.toString();
        assertThat(runners.get(type).run(args))
                .awaitResult()
                .resultIs(Result.ERROR)
                .embedRepliesIsEmpty();
    }

    @ParameterizedTest
    @EnumSource
    @DisplayName("Render commands responds with success, even though the username doesn't exist")
    public void testNonExistentUsername(RenderType type) {
        String args = "DoesNotExist";
        assertThat(runners.get(type).run(args))
                .awaitResult()
                .hasTriggeredCooldown()
                .isSuccess();
    }

    @ParameterizedTest
    @EnumSource
    @DisplayName("Render commands generates render correctly")
    public void testStandardRender(RenderType type) {
        String args = "abc";
        Render expectedRender = new Render(PlayerTests.TIS_STEVE_UUID, type, RenderCommand.DEFAULT_OVERLAY);
        assertThat(runners.get(type).run(args))
                .awaitResult()
                .hasTriggeredCooldown()
                .isSuccess()
                .asEmbedReply()
                .imageLinksTo(expectedRender.render());
    }
    @ParameterizedTest
    @EnumSource
    @DisplayName("Render commands handles quoted names correctly")
    public void testQuotedName(RenderType type) {
        String args = "\"abc\"";
        Render expectedRender = new Render(PlayerTests.TIS_STEVE_UUID, type, RenderCommand.DEFAULT_OVERLAY);
        assertThat(runners.get(type).run(args))
                .awaitResult()
                .hasTriggeredCooldown()
                .isSuccess()
                .asEmbedReply()
                .imageLinksTo(expectedRender.render());
    }
    @ParameterizedTest
    @EnumSource
    @DisplayName("Render commands generates render with scale correctly")
    public void testScaledRender(RenderType type) {
        String args = "abc 5";
        Render expectedRender = new Render(PlayerTests.TIS_STEVE_UUID, type, RenderCommand.DEFAULT_OVERLAY, 5);
        assertThat(runners.get(type).run(args))
                .awaitResult()
                .hasTriggeredCooldown()
                .isSuccess()
                .asEmbedReply()
                .imageLinksTo(expectedRender.render());
    }
    @ParameterizedTest
    @EnumSource
    @DisplayName("Render commands generates render with overlay correctly")
    public void testOverlayRender(RenderType type) {
        String args = "abc true";
        Render expectedRender = new Render(PlayerTests.TIS_STEVE_UUID, type, true);
        assertThat(runners.get(type).run(args))
                .awaitResult()
                .hasTriggeredCooldown()
                .isSuccess()
                .asEmbedReply()
                .imageLinksTo(expectedRender.render());
    }
    @ParameterizedTest
    @EnumSource
    @DisplayName("Render commands generates render with scale and overlay correctly")
    public void testScaleAndOverlayRender(RenderType type) {
        String args = "abc 5 true";
        Render expectedRender = new Render(PlayerTests.TIS_STEVE_UUID, type, true, 5);
        assertThat(runners.get(type).run(args))
                .awaitResult()
                .hasTriggeredCooldown()
                .isSuccess()
                .asEmbedReply()
                .imageLinksTo(expectedRender.render());
    }
    @ParameterizedTest
    @EnumSource
    @DisplayName("Render commands generates render with overlay and scale correctly")
    public void testOverlayAndScaleRender(RenderType type) {
        String args = "abc true 5";
        Render expectedRender = new Render(PlayerTests.TIS_STEVE_UUID, type, true, 5);
        assertThat(runners.get(type).run(args))
                .awaitResult()
                .hasTriggeredCooldown()
                .isSuccess()
                .asEmbedReply()
                .imageLinksTo(expectedRender.render());
    }
    @ParameterizedTest
    @EnumSource
    @DisplayName("Render commands generates render with no overlay correctly")
    public void testNoOverlayRender(RenderType type) {
        String args = "abc false";
        Render expectedRender = new Render(PlayerTests.TIS_STEVE_UUID, type, false);
        assertThat(runners.get(type).run(args))
                .awaitResult()
                .hasTriggeredCooldown()
                .isSuccess()
                .asEmbedReply()
                .imageLinksTo(expectedRender.render());
    }
    @ParameterizedTest
    @EnumSource
    @DisplayName("Render commands generates render with scale and no overlay correctly")
    public void testScaleAndNoOverlayRender(RenderType type) {
        String args = "abc 5 false";
        Render expectedRender = new Render(PlayerTests.TIS_STEVE_UUID, type, false, 5);
        assertThat(runners.get(type).run(args))
                .awaitResult()
                .hasTriggeredCooldown()
                .isSuccess()
                .asEmbedReply()
                .imageLinksTo(expectedRender.render());
    }
    @ParameterizedTest
    @EnumSource
    @DisplayName("Render commands generates render with no overlay and scale correctly")
    public void testNoOverlayAndScaleRender(RenderType type) {
        String args = "abc false 5";
        Render expectedRender = new Render(PlayerTests.TIS_STEVE_UUID, type, false, 5);
        assertThat(runners.get(type).run(args))
                .awaitResult()
                .hasTriggeredCooldown()
                .isSuccess()
                .asEmbedReply()
                .imageLinksTo(expectedRender.render());
    }
    @ParameterizedTest
    @EnumSource
    @DisplayName("Render commands warns for too many args")
    public void testTooManyArgs(RenderType type) {
        String args = "abc 5 true tooManyArgs";
        assertThat(runners.get(type).run(args))
                .awaitResult()
                .hasNotTriggeredCooldown()
                .resultIs(Result.INVALID_ARGS);
    }
    @ParameterizedTest
    @EnumSource
    @DisplayName("Render commands warns for invalid args")
    public void testInvalidArgs(RenderType type) {
        String args = "abc invalid";
        assertThat(runners.get(type).run(args))
                .awaitResult()
                .hasNotTriggeredCooldown()
                .resultIs(Result.INVALID_ARGS);
    }

    @ParameterizedTest
    @EnumSource
    @DisplayName("Render commands show message if player is PHD")
    public void testPHD(RenderType type) {
        Player player = PlayerTests.initPHDPlayer();
        String args = UUIDs.toShortString(player.getUuid());
        assertThat(runners.get(type).run(args))
                .awaitReply()
                .hasTriggeredCooldown()
                .isSuccess()
                .embedRepliesIsEmpty()
                .asReply()
                .contains("PHD");
    }

}
