package com.tisawesomeness.minecord.util;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;

public class ColorUtils {

    private static final JSONObject colors = RequestUtils.loadJSONResource("colors.json");

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
     * @param c The color
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
     * Gets the HSL string for the color
     * @param c The color
     * @return A string in hsl(h,s%,l%) format, where 0 <= h < 360 and 0% <= s,l <= 100%
     */
    public static String getHSL(Color c) {
        float[] rgb = c.getColorComponents(null);
        float max = Math.max(rgb[0], Math.max(rgb[1], rgb[2]));
        float min = Math.min(rgb[0], Math.min(rgb[1], rgb[2]));
        float C = max - min;
        float L = (max + min) / 2;
        int h;
        if (C == 0) {
            h = 0;
        } else if (max == rgb[0]) {
            h = (int) (60 * (rgb[1] - rgb[2]) / C);
        } else if (max == rgb[1]) {
            h = (int) (60 * (2 + (rgb[2] - rgb[0]) / C));
        } else {
            h = (int) (60 * (4 + (rgb[0] - rgb[1]) / C));
        }
        if (h < 0) {
            h += 360;
        }
        int s = L % 1 == 0 ? 0 : Math.round(100 * (max - L) / Math.min(L, 1 - L));
        int l = Math.round(100 * L);
        return String.format("`hsl(%d,%d%%,%d%%)`", h, s, l);
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
     * Converts HSV floats 0-1 to an RGB color
     */
    public static Color fromHSV(float h, float s, float v) {
        return Color.getHSBColor(h, s, v);
    }
    /**
     * Converts HSL floats 0-1 to an RGB color
     */
    public static Color fromHSL(float h, float s, float l) {
        float V = l + s * Math.min(l, 1 - l);
        float S = V == 0 ? 0 : 2 * (1 - l / V);
        return fromHSV(h, S, V);
    }
    /**
     * Converts CMYK floats 0-1 to an RGB color
     */
    public static Color fromCMYK(float c, float m, float y, float k) {
        float r = (1 - c) * (1 - k);
        float g = (1 - m) * (1 - k);
        float b = (1 - y) * (1 - k);
        return new Color(r, g, b);
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

    /**
     * Gets a color from a string using many possible color formats
     * @param str The string to search with
     * @param lang The language code to use
     * @return A color, or null if not found
     */
    public static Color parseColor(String str, String lang) {
        String query = str;

        // Random colors
        if (query.equalsIgnoreCase("rand") || query.equalsIgnoreCase("random")) {
            return ColorUtils.randomColor();
        } else if (query.equalsIgnoreCase("very rand") || query.equalsIgnoreCase("very random")) {
            return ColorUtils.veryRandomColor();
        }

        // Parse &2 as 2
        char start = query.charAt(0);
        if (start == '&' || start == '\u00A7') {
            query = query.substring(1);
        }
        // Predefined names
        Color c = ColorUtils.getColor(query, lang);
        if (c != null) {
            return c;
        }

        // 3 or 4 numbers in a color format
        String[] split = query.replace(", ", ",").replace(" ", ",").replace("%", "").split(",");
        try {
            // RGB, HSL, HSV
            if (split.length == 3) {
                // format(a, b, c)
                if (split[0].length() > 4 && split[2].endsWith(")")) {
                    String prefix = split[0].substring(0, 4);
                    split[0] = split[0].substring(4);
                    split[2] = split[2].substring(0, split[2].length() - 1);
                    // rgb(r,g,b)
                    if (prefix.equalsIgnoreCase("rgb(")) {
                        return new Color(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
                    // hsv(h,s,v)
                    } else if (prefix.equalsIgnoreCase("hsv(")) {
                        float[] comps = parseSplit(split, 360, 100, 100);
                        return fromHSV(comps[0], comps[1], comps[2]);
                    // hsv(h,s,v)
                    } else if (prefix.equalsIgnoreCase("hsl(")) {
                        float[] comps = parseSplit(split, 360, 100, 100);
                        return fromHSL(comps[0], comps[1], comps[2]);
                    }
                }
                // R G B
                if (Character.isDigit(split[0].charAt(0))) {
                    return new Color(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
                }
            // CMYK
            } else if (split.length == 4 && split[0].length() > 5 && split[3].endsWith(")")) {
                String prefix = split[0].substring(0, 5);
                split[0] = split[0].substring(5);
                split[3] = split[3].substring(0, split[3].length() - 1);
                // cmyk(c,m,y,k)
                if (prefix.equalsIgnoreCase("cmyk(")) {
                    float[] comps = parseSplit(split, 100, 100, 100, 100);
                    return fromCMYK(comps[0], comps[1], comps[2], comps[3]);
                }
            }
        } catch (NumberFormatException ignore) {}

        // Parse decimal or hex int
        try {
            // Since "3" is interpreted as a color code, "i3" is the integer 3
            if (start == 'i') {
                return new Color(Integer.parseInt(query.substring(1)));
            }
            return Color.decode(query);
        } catch (NumberFormatException ignore) {}

        return null;
    }
    private static float[] parseSplit(String[] split, int... divs) throws NumberFormatException {
        float[] comps = new float[split.length];
        for (int i = 0; i < split.length; i++) {
            comps[i] = Integer.parseInt(split[i]) / (float) divs[i];
        }
        return comps;
    }
    
}