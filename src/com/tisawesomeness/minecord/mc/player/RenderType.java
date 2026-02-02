package com.tisawesomeness.minecord.mc.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nullable;

/**
 * An enum of renders supported by {@link Render}.
 * See <a href="https://minotar.net/">https://minotar.net/</a>
 */
@RequiredArgsConstructor
public enum RenderType {
    AVATAR("avatar", "Avatar", "avatar", "helm"),
    HEAD("head", "Head", "cube", null),
    BODY("body", "Body", "body", "armor/body"),
    BUST("bust", "Bust", "bust", "armor/bust");

    /**
     * The name of the render type
     */
    @Getter private final String id;
    private final String name;
    /**
     * The path to the API endpoint
     */
    @Getter private final String basePath;
    @Getter private final @Nullable String overlayPath;

    public boolean supportsOverlay() {
        return overlayPath != null;
    }

    @Override
    public String toString() {
        return name;
    }

}
