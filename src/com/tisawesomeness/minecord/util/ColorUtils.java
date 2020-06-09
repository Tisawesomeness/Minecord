package com.tisawesomeness.minecord.util;

import java.awt.Color;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;

public class ColorUtils {

    private static JSONObject colors;

    private static final List<Color> mcColors = Arrays.asList(
        new Color(0, 0, 0),
        new Color(0, 0, 170),
        new Color(0, 170, 0),
        new Color(0, 170, 170),
        new Color(170, 0, 0),
        new Color(170, 0, 170),
        new Color(255, 170, 0),
        new Color(170, 170, 170),
        new Color(85, 85, 85),
        new Color(85, 85, 255),
        new Color(85, 255, 85),
        new Color(85, 255, 255),
        new Color(255, 85, 85),
        new Color(255, 85, 255),
        new Color(255, 255, 85),
        new Color(255, 255, 255)
    );
    private static final List<String> mcColorNames = Arrays.asList(
        "Black", "Dark Blue", "Dark Green", "Dark Aqua",
        "Dark Red", "Dark Purple", "Gold", "Gray",
        "Dark Gray", "Blue", "Green", "Aqua",
        "Red", "Light Purple", "Yellow", "White"
    );
    private static final List<Color> mcBackgroundColors = Arrays.asList(
        new Color(0, 0, 0),
        new Color(0, 0, 42),
        new Color(0, 42, 0),
        new Color(0, 42, 42),
        new Color(42, 0, 0),
        new Color(42, 0, 42),
        new Color(42, 42, 0),
        new Color(42, 42, 42),
        new Color(21, 21, 21),
        new Color(21, 21, 63),
        new Color(21, 63, 21),
        new Color(21, 63, 63),
        new Color(63, 21, 21),
        new Color(63, 21, 63),
        new Color(63, 63, 21),
        new Color(63, 63, 63)
    );

    /**
     * Initializes the color database by reading from file
     * @param path The path to read from
     * @throws IOException when the file isn't found
     */
    public static void init(String path) throws IOException {
        colors = RequestUtils.loadJSON(path + "/colors.json");
    }
    
    /**
     * Searches for a Minecraft color in colors.json
     * @param color The string to look up
     * @param lang The language code
     * @return An integer from 0-15 representing the Minecraft color, or -1 if not found
     */
    public static Color getColor(String color, String lang) {
        int colorID = colors.getJSONObject(lang).optInt(color.toLowerCase().replace(" ", "_").replace("-", "_"), -1);
        return colorID == -1 ? null : mcColors.get(colorID);
    }
	
	/**
     * Picks a random Minecraft color
     */
	public static Color randomColor() {
        return mcColors.get((int) (Math.random()*16));
    }
    /**
     * Picks a random color out of all possible
     */
    public static Color veryRandomColor() {
        return new Color((int) (Math.random()*0x00FFFFFF));
    }

    /**
     * Gets the hex code for a color
     * @param raw The raw integer RGB value of the color from color.getRGB()
     * @return The hex code in #ffffff format
     */
    public static String getHexCode(int raw) {
        return String.format("#%06x", raw & 0x00FFFFFF);
    }
    /**
     * Gets the hex code for a color
     * @param raw The color
     * @return The hex code in #ffffff format
     */
    public static String getHexCode(Color c) {
        return String.format("#%06x", getInt(c));
    }
    /**
     * Gets the integer RGB value representing a color
     * @param c The color
     * @return The integer, from 0 to 2^24 - 1, or 0x000000 to 0xFFFFFF
     */
    public static int getInt(Color c) {
        return c.getRGB() & 0x00FFFFFF;
    }
    /**
     * Gets the RGB string for the color
     * @param c The color
     * @return A string in rgb(r,g,b) format, where 0 <= r,g,b < 256
     */
    public static String getRGB(Color c) {
        return String.format("`rgb(%s,%s,%s)`", c.getRed(), c.getGreen(), c.getBlue());
    }
    /**
     * Gets the HSV string for the color
     * @param c The color
     * @return A string in hsv(h,s%,v%) format, where 0 <= h < 360 and 0% <= s,v <= 100%
     */
	public static String getHSV(Color c) {
        float[] hsv = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
        return String.format("`hsv(%d,%d%%,%d%%)`", (int) (360*hsv[0]), Math.round(100*hsv[1]), Math.round(100*hsv[2]));
    }
    /**
     * Gets the CMYK string for the color
     * @param c The color
     * @return A string in cmyk(c%,m%,y%,k%) format, where 0% <= c,m,y,k <= 100%
     */
    public static String getCMYK(Color c) {
        float[] rgb = c.getColorComponents(null);
        float k = 1 - Math.max(rgb[0], Math.max(rgb[1], rgb[2]));
        int C, M, Y;
        if (k == 1.0) {
            C = 0;
            M = 0;
            Y = 0;
        } else {
            C = Math.round(100 * (1 - rgb[0] - k) / (1 - k));
            M = Math.round(100 * (1 - rgb[1] - k) / (1 - k));
            Y = Math.round(100 * (1 - rgb[2] - k) / (1 - k));
        }
        int K = Math.round(100 * k);
        return String.format("`cmyk(%d%%,%d%%,%d%%,%d%%)`", C, M, Y, K);
    }
    
    /**
     * Finds the index corresponding to a Minecraft color
     * @param c The color
     * @return The index of the Minecraft color from 0-15, or -1 if not found
     */
    public static int getMCIndex(Color c) {
        return mcColors.indexOf(c);
    }
    /**
     * Gets the background color for a Minecraft color
     * @param index The index of Minecraft color from 0-15
     * @return The hex code of the background color
     */
    public static String getBackgroundHex(int index) {
        return getHexCode(mcBackgroundColors.get(index));
    }
    /**
     * Gets the color code for a Minecraft color
     * @param index The index of the Minecraft color from 0-15
     * @return The color code in "§c" format
     */
    public static String getColorCode(int index) {
        return String.format("`\u00A7%01x`", index);
    }
    /**
     * Gets the official name of a Minecraft color
     * @param index The index of the Minecraft color from 0-15
     * @return The color name
     */
    public static String getName(int index) {
        return mcColorNames.get(index);
    }
    
}