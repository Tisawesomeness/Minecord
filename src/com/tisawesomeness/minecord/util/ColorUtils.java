package com.tisawesomeness.minecord.util;

import com.tisawesomeness.minecord.util.type.HumanDecimalFormat;
import lombok.Value;
import org.json.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

public class ColorUtils {

    private static final HumanDecimalFormat FRACTIONAL_PART_FORMAT = HumanDecimalFormat.builder()
            .minimumFractionDigits(0)
            .maximumFractionDigits(3)
            .roundingMode(RoundingMode.HALF_UP)
            .build();

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
     * @param c The color
     * @return The hex code in #ffffff format
     */
    public static String getHexCode(Color c) {
        return String.format("#%06x", getInt(c));
    }
    /**
     * Gets the hex code for a color, with alpha
     * @param c The color
     * @return The hex code in #ffffffff format
     */
    public static String getHexCodeWithAlpha(Color c) {
        return String.format("#%08x", getIntWithAlpha(c));
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
     * Gets the integer RGB value representing a color, with alpha
     * @param c The color
     * @return The integer
     */
    public static int getIntWithAlpha(Color c) {
        return c.getRGB();
    }

    /**
     * Gets the RGB string for the color
     * @param c The color
     * @return A string in rgb(r,g,b) format, where 0 <= r,g,b < 256
     */
    public static String getRGB(Color c) {
        return String.format("`rgb(%d,%d,%d)`", c.getRed(), c.getGreen(), c.getBlue());
    }
    /**
     * Gets the RGBA string for the color
     * @param c The color
     * @return A string in rgba(r,g,b,a) format, where 0 <= r,g,b < 256 and 0 <= a <= 1
     */
    public static String getRGBA(Color c) {
        String alpha = FRACTIONAL_PART_FORMAT.format(c.getAlpha() / 255.0);
        return String.format("`rgba(%d,%d,%d,%s)`", c.getRed(), c.getGreen(), c.getBlue(), alpha);
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

    private static HSLA computeHSL(Color c) {
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
        float a = c.getAlpha() / 255.0f;
        return new HSLA(h, s, l, a);
    }
    /**
     * Gets the HSL string for the color
     * @param c The color
     * @return A string in hsl(h,s%,l%) format, where 0 <= h < 360 and 0% <= s,l <= 100%
     */
    public static String getHSL(Color c) {
        HSLA hsla = computeHSL(c);
        return String.format("`hsl(%d,%d%%,%d%%)`", hsla.getH(), hsla.getS(), hsla.getL());
    }
    /**
     * Gets the HSLA string for the color
     * @param c The color
     * @return A string in hsla(h,s%,l%,a) format, where 0 <= h < 360 and 0% <= s,l <= 100% and 0 <= a <= 1
     */
    public static String getHSLA(Color c) {
        HSLA hsla = computeHSL(c);
        String alpha = FRACTIONAL_PART_FORMAT.format(hsla.getA());
        return String.format("`hsla(%d,%d%%,%d%%,%s)`", hsla.getH(), hsla.getS(), hsla.getL(), alpha);
    }
    @Value
    private static class HSLA {
        int h;
        int s;
        int l;
        float a;
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
     * Converts HSLA floats 0-1 to an RGB color
     */
    public static Color fromHSLA(float h, float s, float l, float a) {
        Color c = fromHSL(h, s, l);
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), (int) (a * 255f));
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
        String[] split = query.replace(", ", ",").replace(" ", ",").split(",");
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
                        return new Color(
                                parseIntOrPercent(split[0], 255),
                                parseIntOrPercent(split[1], 255),
                                parseIntOrPercent(split[2], 255)
                        );
                        // hsv(h,s,v)
                    } else if (prefix.equalsIgnoreCase("hsv(")) {
                        return fromHSV(
                                Integer.parseInt(split[0]) / 360f,
                                parseFloatOrPercent(split[1]),
                                parseFloatOrPercent(split[2])
                        );
                        // hsl(h,s,l)
                    } else if (prefix.equalsIgnoreCase("hsl(")) {
                        return fromHSL(
                                Integer.parseInt(split[0]) / 360f,
                                parseFloatOrPercent(split[1]),
                                parseFloatOrPercent(split[2])
                        );
                    }
                }
                // R G B
                if (Character.isDigit(split[0].charAt(0))) {
                    return new Color(
                            parseIntOrPercent(split[0], 255),
                            parseIntOrPercent(split[1], 255),
                            parseIntOrPercent(split[2], 255)
                    );
                }
                // RGBA, HSLA, CMYK
            } else if (split.length == 4) {
                // format(a, b, c, d)
                if (split[0].length() > 5 && split[3].endsWith(")")) {
                    String prefix = split[0].substring(0, 5);
                    split[0] = split[0].substring(5);
                    split[3] = split[3].substring(0, split[3].length() - 1);
                    // rgba(r,g,b,a)
                    if (prefix.equalsIgnoreCase("rgba(")) {
                        return new Color(
                                parseIntOrPercent(split[0], 255),
                                parseIntOrPercent(split[1], 255),
                                parseIntOrPercent(split[2], 255),
                                (int) (parseFloatOrPercent(split[3]) * 255f)
                        );
                    }
                    // hsla(h,s,l,a)
                    if (prefix.equalsIgnoreCase("hsla(")) {
                        return fromHSLA(
                                Integer.parseInt(split[0]) / 360f,
                                parseFloatOrPercent(split[1]),
                                parseFloatOrPercent(split[2]),
                                parseFloatOrPercent(split[3])
                        );
                    }
                    // cmyk(c,m,y,k)
                    if (prefix.equalsIgnoreCase("cmyk(")) {
                        return fromCMYK(
                                parseFloatOrPercent(split[0]),
                                parseFloatOrPercent(split[1]),
                                parseFloatOrPercent(split[2]),
                                parseFloatOrPercent(split[3])
                        );
                    }
                }
                // R G B A
                if (Character.isDigit(split[0].charAt(0))) {
                    return new Color(
                            parseIntOrPercent(split[0], 255),
                            parseIntOrPercent(split[1], 255),
                            parseIntOrPercent(split[2], 255),
                            (int) (parseFloatOrPercent(split[3]) * 255f)
                    );
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
    private static int parseIntOrPercent(String str, int max) throws NumberFormatException {
        int i = parseIntOrPercentInner(str, max);
        if (i < 0 || i > max) {
            throw new NumberFormatException("Value must be between 0 and " + max);
        }
        return i;
    }
    private static int parseIntOrPercentInner(String str, int max) {
        if (str.endsWith("%")) {
            str = str.substring(0, str.length() - 1);
            return (int) (Float.parseFloat(str) * max / 100f);
        } else {
            return Integer.parseInt(str);
        }
    }
    private static float parseFloatOrPercent(String str) throws NumberFormatException {
        float f = parseFloatOrPercentInner(str);
        if (f < 0 || f > 1) {
            throw new NumberFormatException("Value must be between 0 and 1");
        }
        return f;
    }
    private static float parseFloatOrPercentInner(String str) {
        if (str.endsWith("%")) {
            str = str.substring(0, str.length() - 1);
            return Float.parseFloat(str) / 100f;
        } else {
            return Float.parseFloat(str);
        }
    }

    /**
     * Finds the closest ANSI color to the given color.
     * The distance is measured by converting to the CEILAB color space and finding the distance between the two points
     * (CIE76 formula).
     * @param color a color
     * @return an ANSI color code that can be used in a terminal or a Discord ANSI codeblock
     */
    public static String nearestAnsiColorCode(Color color) {
        double[] lab = xyzToLab(colorToXyz(color));
        return Arrays.stream(AnsiColor.values())
                .min((c1, c2) -> compareDist(lab, c1, c2))
                .orElseThrow(AssertionError::new)
                .getAnsiCode();
    }
    private static int compareDist(double[] colorLab, AnsiColor a, AnsiColor b) {
        return Double.compare(distanceSqr(colorLab, a), distanceSqr(colorLab, b));
    }
    private static double distanceSqr(double[] aLab, AnsiColor b) {
        double ld = aLab[0] - b.lab[0];
        double ad = aLab[1] - b.lab[1];
        double bd = aLab[2] - b.lab[2];
        return ld * ld + ad * ad + bd * bd;
    }

    private enum AnsiColor {
        BLACK(0x4f545c, 30),
        RED(0xdc322f, 31),
        GREEN(0x859900, 32),
        YELLOW(0xb58900, 33),
        BLUE(0x268bd2, 34),
        PURPLE(0xd33682, 35),
        CYAN(0x2aa198, 36),
        WHITE(0xffffff, 37),
        BACKGROUND_BLACK(0x002b36, 40),
        BACKGROUND_RED(0xcb4b16, 41),
        BACKGROUND_GREEN(0x586e75, 42),
        BACKGROUND_YELLOW(0x657b83, 43),
        BACKGROUND_BLUE(0x839496, 44),
        BACKGROUND_PURPLE(0x6c71c4, 45),
        BACKGROUND_CYAN(0x93a1a1, 46),
        BACKGROUND_WHITE(0xfdf6e3, 47);

        private final int num;
        private final double[] lab;
        AnsiColor(int rgb, int num) {
            this.num = num;
            lab = xyzToLab(colorToXyz(new Color(rgb)));
        }

        public String getAnsiCode() {
            String displayChars = isBackground() ? "  " : "██";
            return String.format("\u001b[%dm%s", num, displayChars);
        }
        private boolean isBackground() {
            return num >= 40;
        }
    }

    /*
     * The code below is adapted from ColorMine with the license below.
     * https://github.com/colormine/colormine/blob/master/colormine/src/main/org/colormine/colorspace/ColorSpaceConverter.java
     *
     * The MIT License (MIT)
     * Copyright (c) 2012 ColorMine.org
     *
     * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
     *
     * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
     *
     * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
     */

    private static double[] colorToXyz(Color color) {
        double r = pivotRgb(color.getRed() / 255.0);
        double g = pivotRgb(color.getGreen() / 255.0);
        double b = pivotRgb(color.getBlue() / 255.0);

        // Observer = 2°, Illuminant = D65
        return new double[]{
                r * 0.4124 + g * 0.3576 + b * 0.1805,
                r * 0.2126 + g * 0.7152 + b * 0.0722,
                r * 0.0193 + g * 0.1192 + b * 0.9505
        };
    }
    private static double pivotRgb(double n) {
        return (n > 0.04045 ? Math.pow((n + 0.055) / 1.055, 2.4) : n / 12.92) * 100;
    }
    private static double[] xyzToLab(double[] xyz) {
        // Observer = 2°, Illuminant = D65
        double xp = pivotXyz(xyz[0] / 95.047);
        double yp = pivotXyz(xyz[1] / 100.000);
        double zp = pivotXyz(xyz[2] / 108.883);

        return new double[]{
                116 * yp - 16,
                500 * (xp - yp),
                200 * (yp - zp)
        };
    }
    private static double pivotXyz(double n) {
        return n > 0.008856 ? Math.cbrt(n) : 7.787 * n + 16.0 / 116.0;
    }

}
