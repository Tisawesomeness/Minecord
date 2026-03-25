package com.tisawesomeness.minecord.mc.item;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;

public enum DyeColor {
    WHITE(0xF9FFFE),
    ORANGE(0xF9801D),
    MAGENTA(0xC74EBD),
    LIGHT_BLUE(0x3AB3DA),
    YELLOW(0xFED83D),
    LIME(0x80C71F),
    PINK(0xF38BAA),
    GRAY(0x474F52),
    LIGHT_GRAY(0x9D9D97),
    CYAN(0x169C9C),
    PURPLE(0x8932B8),
    BLUE(0x3C44AA),
    BROWN(0x835432),
    GREEN(0x5E7C16),
    RED(0xB02E26),
    BLACK(0x1D1D21);

    private final Color color;

    DyeColor(int color) {
        this.color = new Color(color);
    }

    public static Color mix(@Nullable DyeColor currentDye, List<DyeColor> dyes) {
        int redTotal = 0;
        int greenTotal = 0;
        int blueTotal = 0;
        int intensityTotal = 0;
        int colorCount = 0;
        if (currentDye != null) {
            int red = currentDye.color.getRed();
            int green = currentDye.color.getGreen();
            int blue = currentDye.color.getBlue();
            intensityTotal += Math.max(red, Math.max(green, blue));
            redTotal += red;
            greenTotal += green;
            blueTotal += blue;
            colorCount++;
        }

        for (DyeColor dye : dyes) {
            int red = dye.color.getRed();
            int green = dye.color.getGreen();
            int blue = dye.color.getBlue();
            intensityTotal += Math.max(red, Math.max(green, blue));
            redTotal += red;
            greenTotal += green;
            blueTotal += blue;
            colorCount++;
        }

        int red = redTotal / colorCount;
        int green = greenTotal / colorCount;
        int blue = blueTotal / colorCount;
        float averageIntensity = (float) intensityTotal / colorCount;
        float resultIntensity = Math.max(red, Math.max(green, blue));
        red = (int) (red * averageIntensity / resultIntensity);
        green = (int) (green * averageIntensity / resultIntensity);
        blue = (int) (blue * averageIntensity / resultIntensity);
        return new Color(red, green, blue);
    }
}
