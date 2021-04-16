package com.tisawesomeness.minecord.mc.player;

import com.tisawesomeness.minecord.testutil.PlayerTests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RenderTest {

    @Test
    @DisplayName("Avatar renders generate as expected")
    public void testRenderAvatar() {
        RenderType type = RenderType.AVATAR;
        Render render = new Render(PlayerTests.STEVE_UUID, type, false);
        String expected = "https://crafatar.com/avatars/f6489b79-7a9f-49e2-980e-265a05dbc3af?size="
                + type.getDefaultScale();
        assertThat(render.render()).hasToString(expected);
    }
    @Test
    @DisplayName("Head renders generate as expected")
    public void testRenderHead() {
        RenderType type = RenderType.HEAD;
        Render render = new Render(PlayerTests.STEVE_UUID, type, false);
        String expected = "https://crafatar.com/renders/head/f6489b79-7a9f-49e2-980e-265a05dbc3af?scale="
                + type.getDefaultScale();
        assertThat(render.render()).hasToString(expected);
    }
    @Test
    @DisplayName("Body renders generate as expected")
    public void testRenderBody() {
        RenderType type = RenderType.BODY;
        Render render = new Render(PlayerTests.STEVE_UUID, type, false);
        String expected = "https://crafatar.com/renders/body/f6489b79-7a9f-49e2-980e-265a05dbc3af?scale="
                + type.getDefaultScale();
        assertThat(render.render()).hasToString(expected);
    }

    @Test
    @DisplayName("Avatar renders with overlay generate as expected")
    public void testRenderAvatarOverlay() {
        RenderType type = RenderType.AVATAR;
        Render render = new Render(PlayerTests.STEVE_UUID, type, true);
        String expected = String.format("https://crafatar.com/avatars/" +
                "f6489b79-7a9f-49e2-980e-265a05dbc3af?size=%d&overlay", type.getDefaultScale());
        assertThat(render.render()).hasToString(expected);
    }
    @Test
    @DisplayName("Head renders with overlay generate as expected")
    public void testRenderHeadOverlay() {
        RenderType type = RenderType.HEAD;
        Render render = new Render(PlayerTests.STEVE_UUID, type, true);
        String expected = String.format("https://crafatar.com/renders/head/" +
                "f6489b79-7a9f-49e2-980e-265a05dbc3af?scale=%d&overlay", type.getDefaultScale());
        assertThat(render.render()).hasToString(expected);
    }
    @Test
    @DisplayName("Body renders with overlay generate as expected")
    public void testRenderBodyOverlay() {
        RenderType type = RenderType.BODY;
        Render render = new Render(PlayerTests.STEVE_UUID, type, true);
        String expected = String.format("https://crafatar.com/renders/body/" +
                "f6489b79-7a9f-49e2-980e-265a05dbc3af?scale=%d&overlay", type.getDefaultScale());
        assertThat(render.render()).hasToString(expected);
    }

    @Test
    @DisplayName("Avatar renders with custom size generate as expected")
    public void testRenderAvatarSize() {
        int size = 256;
        RenderType type = RenderType.AVATAR;
        Render render = new Render(PlayerTests.STEVE_UUID, type, false, size);
        String expected = "https://crafatar.com/avatars/f6489b79-7a9f-49e2-980e-265a05dbc3af?size=" + size;
        assertThat(render.render()).hasToString(expected);
    }
    @Test
    @DisplayName("Head renders with custom size generate as expected")
    public void testRenderHeadSize() {
        int scale = 3;
        RenderType type = RenderType.HEAD;
        Render render = new Render(PlayerTests.STEVE_UUID, type, false, scale);
        String expected = "https://crafatar.com/renders/head/f6489b79-7a9f-49e2-980e-265a05dbc3af?scale=" + scale;
        assertThat(render.render()).hasToString(expected);
    }
    @Test
    @DisplayName("Body renders with custom size generate as expected")
    public void testRenderBodySize() {
        int scale = 3;
        RenderType type = RenderType.BODY;
        Render render = new Render(PlayerTests.STEVE_UUID, type, false, scale);
        String expected = "https://crafatar.com/renders/body/f6489b79-7a9f-49e2-980e-265a05dbc3af?scale=" + scale;
        assertThat(render.render()).hasToString(expected);
    }

    @Test
    @DisplayName("Avatar renders with size above max use max size")
    public void testRenderAvatarOverflow() {
        RenderType type = RenderType.AVATAR;
        Render render = new Render(PlayerTests.STEVE_UUID, type, false, type.getMaxScale() + 1);
        String expected = "https://crafatar.com/avatars/f6489b79-7a9f-49e2-980e-265a05dbc3af?size="
                + type.getMaxScale();
        assertThat(render.render()).hasToString(expected);
    }
    @Test
    @DisplayName("Body renders with size above max use max size")
    public void testRenderHeadOverflow() {
        RenderType type = RenderType.HEAD;
        Render render = new Render(PlayerTests.STEVE_UUID, type, false, type.getMaxScale() + 1);
        String expected = "https://crafatar.com/renders/head/f6489b79-7a9f-49e2-980e-265a05dbc3af?scale="
                + type.getMaxScale();
        assertThat(render.render()).hasToString(expected);
    }
    @Test
    @DisplayName("Head renders with size above max use max size")
    public void testRenderBodyOverflow() {
        RenderType type = RenderType.BODY;
        Render render = new Render(PlayerTests.STEVE_UUID, type, false, type.getMaxScale() + 1);
        String expected = "https://crafatar.com/renders/body/f6489b79-7a9f-49e2-980e-265a05dbc3af?scale="
                + type.getMaxScale();
        assertThat(render.render()).hasToString(expected);
    }

}
