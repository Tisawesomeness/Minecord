package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.Result;
import com.tisawesomeness.minecord.config.ConfigReader;
import com.tisawesomeness.minecord.config.serial.Config;
import com.tisawesomeness.minecord.mc.player.Render;
import com.tisawesomeness.minecord.mc.player.RenderType;
import com.tisawesomeness.minecord.mc.player.Username;
import com.tisawesomeness.minecord.testutil.MiscTestUtils;
import com.tisawesomeness.minecord.testutil.PlayerTestUtils;
import com.tisawesomeness.minecord.testutil.mc.TestMCLibrary;
import com.tisawesomeness.minecord.testutil.mc.TestPlayerProvider;
import com.tisawesomeness.minecord.testutil.runner.TestCommandRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tisawesomeness.minecord.testutil.runner.CommandAssertions.assertThat;

public class GeneralRenderCommandIT {

    private static final Username THROWING_USERNAME = new Username("throw");
    private static TestCommandRunner runner;

    @BeforeAll
    private static void initRunner() throws JsonProcessingException {
        Command cmd = new GeneralRenderCommand();
        runner = new TestCommandRunner(ConfigReader.readFromResources(), cmd);
        TestMCLibrary library = new TestMCLibrary();
        TestPlayerProvider playerProvider = library.getPlayerProvider();

        playerProvider.throwOnUsername(THROWING_USERNAME);
        playerProvider.mapUuid(new Username("abc"), PlayerTestUtils.STEVE_UUID);

        runner.mcLibrary = library;
    }

    @Test
    @DisplayName("Render command with no args only requests help")
    public void testNoArgs() {
        assertThat(runner.run()).onlyShowsHelp();
    }
    @Test
    @DisplayName("Render command with one arg only requests help")
    public void testOneArg() {
        String args = "avatar";
        assertThat(runner.run(args)).onlyShowsHelp();
    }

    @Test
    @DisplayName("Render command with too long username warns the user")
    public void testTooLong() {
        String args = "avatar " + MiscTestUtils.repeat("A", Username.MAX_LENGTH + 1);
        assertThat(runner.run(args))
                .awaitResult()
                .hasNotTriggeredCooldown()
                .resultIs(Result.WARNING);
    }

    @Test
    @DisplayName("Render command with non-ascii username warns the user")
    public void testNonAsciiUsername() {
        String args = "head ooθoo";
        assertThat(runner.run(args))
                .awaitResult()
                .hasNotTriggeredCooldown()
                .resultIs(Result.WARNING);
    }

    @Test
    @DisplayName("Render command responds with error when an IOE is thrown")
    public void testIOE() {
        String args = "body " + THROWING_USERNAME;
        assertThat(runner.run(args))
                .awaitResult()
                .resultIs(Result.ERROR)
                .embedRepliesIsEmpty();
    }

    @Test
    @DisplayName("Render command responds with success, even though the username doesn't exist")
    public void testNonExistentUsername() {
        String args = "avatar DoesNotExist";
        assertThat(runner.run(args))
                .awaitResult()
                .hasTriggeredCooldown()
                .isSuccess();
    }

    @Test
    @DisplayName("Render command generates render correctly")
    public void testStandardRender() {
        String args = "head abc";
        Render expectedRender = new Render(PlayerTestUtils.STEVE_UUID, RenderType.HEAD, false);
        assertThat(runner.run(args))
                .awaitResult()
                .hasTriggeredCooldown()
                .isSuccess()
                .asEmbedReply()
                .imageLinksTo(expectedRender.render());
    }
    @Test
    @DisplayName("Render command generates render with scale correctly")
    public void testScaledRender() {
        String args = "body abc 5";
        Render expectedRender = new Render(PlayerTestUtils.STEVE_UUID, RenderType.BODY, false, 5);
        assertThat(runner.run(args))
                .awaitResult()
                .hasTriggeredCooldown()
                .isSuccess()
                .asEmbedReply()
                .imageLinksTo(expectedRender.render());
    }
    @Test
    @DisplayName("Render command generates render with overlay correctly")
    public void testOverlayRender() {
        String args = "avatar abc true";
        Render expectedRender = new Render(PlayerTestUtils.STEVE_UUID, RenderType.AVATAR, true);
        assertThat(runner.run(args))
                .awaitResult()
                .hasTriggeredCooldown()
                .isSuccess()
                .asEmbedReply()
                .imageLinksTo(expectedRender.render());
    }
    @Test
    @DisplayName("Render command generates render with scale and overlay correctly")
    public void testScaleAndOverlayRender() {
        String args = "head abc 5 true";
        Render expectedRender = new Render(PlayerTestUtils.STEVE_UUID, RenderType.HEAD, true, 5);
        assertThat(runner.run(args))
                .awaitResult()
                .hasTriggeredCooldown()
                .isSuccess()
                .asEmbedReply()
                .imageLinksTo(expectedRender.render());
    }
    @Test
    @DisplayName("Render command generates render with overlay and scale correctly")
    public void testOverlayAndScaleRender() {
        String args = "body abc true 5";
        Render expectedRender = new Render(PlayerTestUtils.STEVE_UUID, RenderType.BODY, true, 5);
        assertThat(runner.run(args))
                .awaitResult()
                .hasTriggeredCooldown()
                .isSuccess()
                .asEmbedReply()
                .imageLinksTo(expectedRender.render());
    }
    @Test
    @DisplayName("Render command warns for too many args")
    public void testTooManyArgs() {
        String args = "avatar abc 5 true tooManyArgs";
        assertThat(runner.run(args))
                .awaitResult()
                .resultIs(Result.WARNING);
    }
    @Test
    @DisplayName("Render command warns for invalid args")
    public void testInvalidArgs() {
        String args = "head abc invalid";
        assertThat(runner.run(args))
                .awaitResult()
                .resultIs(Result.WARNING);
    }
    @Test
    @DisplayName("Render command warns for invalid render type")
    public void testInvalidRenderType() {
        String args = "invalid abc";
        assertThat(runner.run(args))
                .awaitResult()
                .hasNotTriggeredCooldown()
                .resultIs(Result.WARNING);
    }

}
