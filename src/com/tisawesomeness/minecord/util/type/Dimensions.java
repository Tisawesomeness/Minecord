package com.tisawesomeness.minecord.util.type;

import lombok.Value;

@Value
public class Dimensions {
    int width;
    int height;

    /**
     * Creates a new Dimensions object
     * @param width width
     * @param height height
     * @throws IllegalArgumentException if width or height are negative
     */
    public Dimensions(int width, int height) {
        if (width < 0) {
            throw new IllegalArgumentException("width cannot be negative but was " + width);
        }
        if (height < 0) {
            throw new IllegalArgumentException("height cannot be negative but was " + height);
        }
        this.width = width;
        this.height = height;
    }
}
