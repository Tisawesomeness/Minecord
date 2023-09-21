package com.tisawesomeness.minecord.mc.item;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * A container that can hold one or more stacks of Minecraft items.
 */
@RequiredArgsConstructor
public enum Container {
    STACK(1, "stacks"),
    CHEST(27, "chests"),
    DOUBLE_CHEST(2 * 27, "double chests"),
    CHEST_SHULKER(27 * 27, "chests full of shulkers"),
    DOUBLE_CHEST_SHULKER(2 * 27 * 27, "double chests full of shulkers");

    @Getter private final int slots;
    private final String description;

    /**
     * Gets the description of this container, taking into account whether the number of containers is plural.
     * @param count number of containers
     * @return description
     */
    public String getDescription(double count) {
        return count == 1.0 ? description.substring(0, description.length() - 1) : description;
    }

}
