package com.tisawesomeness.minecord.mc.item;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * A container that can hold one or more stacks of Minecraft items.
 */
@Getter
@RequiredArgsConstructor
public enum Container {
    STACK(1),
    CHEST(27),
    DOUBLE_CHEST(2 * 27),
    CHEST_SHULKER(27 * 27),
    DOUBLE_CHEST_SHULKER(2 * 27 * 27);

    private final int slots;
}
