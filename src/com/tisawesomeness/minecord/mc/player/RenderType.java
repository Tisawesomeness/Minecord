package com.tisawesomeness.minecord.mc.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * An enum of renders supported by {@link Render}.
 * See <a href="https://crafatar.com/">https://crafatar.com/</a>
 */
@RequiredArgsConstructor
public enum RenderType {
    AVATAR("avatar", "Avatar", "avatars", false),
    HEAD("head", "Head", "renders/head", true),
    BODY("body", "Body", "renders/body", true);

    public static final int MAX_SIZE = 512;
    public static final int DEFAULT_SIZE = 160;
    public static final int MAX_SCALE = 10;
    public static final int DEFAULT_SCALE = 6;

    /**
     * The name of the render type
     */
    @Getter private final String id;
    private final String name;
    /**
     * The path to the API endpoint
     */
    @Getter private final String basePath;
    /**
     * Whether Crafatar recognizes this type as a render, and scale should be used instead of size
     */
    @Getter private final boolean isRender;

    /**
     * @return The maximum scale of this render type
     */
    public int getMaxScale() {
        return isRender ? MAX_SCALE : MAX_SIZE;
    }
    /**
     * @return The default scale of this render type
     */
    public int getDefaultScale() {
        return isRender ? DEFAULT_SCALE : DEFAULT_SIZE;
    }

    @Override
    public String toString() {
        return name;
    }

}
