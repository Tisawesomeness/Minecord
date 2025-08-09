package com.tisawesomeness.minecord.mc.pos;

import com.tisawesomeness.minecord.util.MathUtils;
import lombok.NonNull;

import javax.annotation.Nullable;
import java.util.OptionalDouble;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a vector. Can be a 2D ({@link Vec2}) or 3D ({@link Vec3}) vector.
 */
public abstract class Vec {

    private static final String LEFT_BRACKETS = "([{<";
    private static final String RIGHT_BRACKETS = ")]}>";

    // Regex used to parse a coordinate
    // prefix: `([xyz] *[=:]? *)`, matches "x", "x:", "x=", with optional spaces
    // num: `([+-]?\d*\.?\d+)`, matches a number with optional decimal part, ".9" matches but "9." doesn't
    // sep: `(?: +| *[,/] *|(?=[xyz]))`, matches a separator, which can be either:
    // - 1+ spaces
    // - ',' or '/' with optional spaces
    // - nothing, as long as the next number begins with a prefix (next char is in [xyz])
    // Complete pattern is `{prefix}?{num}{sep}{prefix}?{num}({sep}{prefix}?{num})?`
    // Prefixes are groups 1, 3, 5, numbers are groups 4, 5, 6, separator is non-capturing, groups 5 and 6 are optional
    private static final Pattern PARSE_REGEX = Pattern.compile("([xyz] *[=:]? *)?([+-]?\\d*\\.?\\d+)(?: +| *[,/] *|(?=[xyz]))([xyz] *[=:]? *)?([+-]?\\d*\\.?\\d+)(?:(?: +| *[,/] *|(?=[xyz]))([xyz] *[=:]? *)?([+-]?\\d*\\.?\\d+))?", Pattern.CASE_INSENSITIVE);

    /**
     * Parses a Vec2 or Vec3 from a string. The string must contain two/three <strong>numbers</strong>, each number
     * optionally starting with a <strong>prefix</strong>, separated by a <strong>separator</strong>.
     * Optionally, the input can be surrounded by one matching pair of <strong>brackets</strong>.
     * <h4>Number</h4>
     * A decimal number that can be parsed by {@link Double#parseDouble(String)}.
     * Leading periods (".9") are allowed, but trailing periods ("9.") are not. A leading plus ("+") sign is allowed.
     * <h4>Prefix</h4>
     * One of "x=", "x:", or "x" for any of x, y, or z, case-insensitive. Spaces are ignored.
     * Prefixes may be used to specify numbers in any order, such as "y=1, z=2, x=7".
     * However, coordinates must be used for all numbers, or none at all.
     * If no prefixes are used, then Vec3 coordinates are specified in x, y, z order, and Vec2 coordinates are
     * specified in x, z order.
     * <h4>Separator</h4>
     * Numbers can be separated by spaces, commas, or forward slashes. Extra spaces are ignored.
     * If all numbers have a prefix, then a separator is not needed, such as in "x5y2z-3".
     * <h4>Brackets</h4>
     * The input string may start and end with one pair of matching brackets: "()", "[]", "{}", or "<>".
     * @param str input string
     * @return parsed vec, or null if the string could not be parsed
     */
    public static @Nullable Vec parse(@NonNull String str) {
        // Shortest possible input is "1 2", reject shorter strings
        if (str.length() < 3) {
            return null;
        }

        // Regex doesn't take into account brackets, any extra chars will cause match to fail
        String bracketTrimmed = trimBrackets(str);
        if (bracketTrimmed == null) {
            return null;
        }
        String trimmed = bracketTrimmed.trim(); // This trim is necessary

        // Entire trimmed string must match to prevent "a3, 4, 5" from parsing
        Matcher matcher = PARSE_REGEX.matcher(trimmed);
        if (!matcher.matches()) {
            return null;
        }

        // Case 1: no prefixes used (ex: "73, 4, -5")
        String prefix1 = matcher.group(1);
        String prefix2 = matcher.group(3);
        String prefix3 = matcher.group(5);
        if (prefix1 == null && prefix2 == null && prefix3 == null) {
            String numStr1 = matcher.group(2);
            String numStr2 = matcher.group(4);
            String numStr3 = matcher.group(6);
            return parse(numStr1, numStr2, numStr3);
        }

        // Case 2: all prefixes used (ex: "x=73, y=4, z=-5")
        // If prefixes only used on some numbers, input is ambiguous and rejected
        // x, y, and z can be processed in any order, but if any strings are left null, they'll fail in parse(String...)
        String xStr = null;
        String yStr = null;
        String zStr = null;
        int vecLength = matcher.group(6) == null ? 2 : 3;
        for (int i = 0; i < vecLength; i++) {
            String prefix = matcher.group(2 * i + 1);
            if (prefix == null) {
                return null;
            }
            String numStr = matcher.group(2 * i + 2);
            // [xyz] is always the first character matched in the prefix group
            char maybeXyz = Character.toLowerCase(prefix.charAt(0));
            switch (maybeXyz) {
                case 'x':
                    xStr = numStr;
                    break;
                case 'y':
                    yStr = numStr;
                    break;
                case 'z':
                    zStr = numStr;
                    break;
                default:
                    throw new AssertionError("unreachable");
            }
        }
        if (vecLength == 2) {
            if (yStr != null) {
                return null;
            }
            return parse(xStr, zStr, null);
        } else {
            return parse(xStr, yStr, zStr);
        }
    }

    private static @Nullable Vec parse(@Nullable String xStr, @Nullable String yStr, @Nullable String zStr) {
        OptionalDouble xOpt = MathUtils.safeParseDouble(xStr);
        if (!xOpt.isPresent()) {
            return null;
        }
        OptionalDouble yOpt = MathUtils.safeParseDouble(yStr);
        if (!yOpt.isPresent()) {
            return null;
        }
        if (zStr == null) {
            return new Vec2(xOpt.getAsDouble(), yOpt.getAsDouble());
        } else {
            OptionalDouble zOpt = MathUtils.safeParseDouble(zStr);
            if (!zOpt.isPresent()) {
                return null;
            }
            return new Vec3(xOpt.getAsDouble(), yOpt.getAsDouble(), zOpt.getAsDouble());
        }
    }

    // Trims brackets in the first/last character if they exist, returning empty if brackets are mismatched
    private static @Nullable String trimBrackets(String str) {
        assert str.length() >= 2;
        char first = str.charAt(0);
        char last = str.charAt(str.length() - 1);

        // If first character isn't a bracket, no need to trim brackets
        int bracketId = LEFT_BRACKETS.indexOf(first);
        if (bracketId == -1) {
            return str;
        }
        // Brackets must match
        if (last == RIGHT_BRACKETS.charAt(bracketId)) {
            return str.substring(1, str.length() - 1);
        } else {
            return null;
        }
    }

}
