package com.tisawesomeness.minecord.mc.player;

import lombok.RequiredArgsConstructor;

/**
 * An enum with every possible default skin types: Steve, Alex, and the 7 new skins.
 */
@RequiredArgsConstructor
public enum DefaultSkinType {
    ALEX("Alex"),
    ARI("Ari"),
    EFE("Efe"),
    KAI("Kai"),
    MAKENA("Makena"),
    NOOR("Noor"),
    STEVE("Steve"),
    SUNNY("Sunny"),
    ZURI("Zuri");

    private final String label;

    @Override
    public String toString() {
        return label;
    }
}
