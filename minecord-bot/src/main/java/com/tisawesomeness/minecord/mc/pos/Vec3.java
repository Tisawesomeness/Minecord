package com.tisawesomeness.minecord.mc.pos;

import com.tisawesomeness.minecord.util.Mth;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class Vec3 {

    private static final String LEFT_BRACKETS = "([{<";
    private static final String RIGHT_BRACKETS = ")]}>";

    // Regex used to parse a coordinate
    // prefix: `([xyz] *[=:]? *)`, matches "x", "x:", "x=", with optional spaces
    // num: `([+-]?\d*\.?\d+)`, matches a number with optional decimal part, ".9" matches but "9." doesn't
    // sep: `(?: +| *[,/] *|(?=[xyz]))`, matches a separator, which can be either:
    // - 1+ spaces
    // - ',' or '/' with optional spaces
    // - nothing, as long as the next number begins with a prefix (next char is in [xyz])
    // Complete pattern is `{prefix}?{num}{sep}{prefix}?{num}{sep}{prefix}?{num}`
    // Prefixes are groups 1, 3, 5, numbers are groups 4, 5, 6, separator is non-capturing
    private static final Pattern PARSE_REGEX = Pattern.compile("([xyz] *[=:]? *)?([+-]?\\d*\\.?\\d+)(?: +| *[,/] *|(?=[xyz]))([xyz] *[=:]? *)?([+-]?\\d*\\.?\\d+)(?: +| *[,/] *|(?=[xyz]))([xyz] *[=:]? *)?([+-]?\\d*\\.?\\d+)", Pattern.CASE_INSENSITIVE);

    private final double x;
    private final double y;
    private final double z;

    public static Optional<Vec3> parse(String str) {
        // Shortest possible input is "1 2 3", reject shorter strings
        if (str.length() < 5) {
            return Optional.empty();
        }

        // Regex doesn't take into account brackets, any extra chars will cause match to fail
        Optional<String> trimmedOpt = trimBrackets(str);
        if (!trimmedOpt.isPresent()) {
            return Optional.empty();
        }
        String trimmed = trimmedOpt.get().trim(); // This trim is necessary

        // Entire trimmed string must match to prevent "a3, 4, 5" from parsing
        Matcher matcher = PARSE_REGEX.matcher(trimmed);
        if (!matcher.matches()) {
            return Optional.empty();
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
        for (int i = 0; i < 3; i++) {
            String prefix = matcher.group(2 * i + 1);
            if (prefix == null) {
                return Optional.empty();
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
        return parse(xStr, yStr, zStr);
    }

    private static Optional<Vec3> parse(@Nullable String xStr, @Nullable String yStr, @Nullable String zStr) {
        OptionalDouble xOpt = Mth.safeParseDouble(xStr);
        if (!xOpt.isPresent()) {
            return Optional.empty();
        }
        OptionalDouble yOpt = Mth.safeParseDouble(yStr);
        if (!yOpt.isPresent()) {
            return Optional.empty();
        }
        OptionalDouble zOpt = Mth.safeParseDouble(zStr);
        if (!zOpt.isPresent()) {
            return Optional.empty();
        }
        return Optional.of(new Vec3(xOpt.getAsDouble(), yOpt.getAsDouble(), zOpt.getAsDouble()));
    }

    // Trims brackets in the first/last character if they exist, returning empty if brackets are mismatched
    private static Optional<String> trimBrackets(String str) {
        assert str.length() >= 2;
        char first = str.charAt(0);
        char last = str.charAt(str.length() - 1);

        // If first character isn't a bracket, no need to trim brackets
        int bracketId = LEFT_BRACKETS.indexOf(first);
        if (bracketId == -1) {
            return Optional.of(str);
        }
        // Brackets must match
        if (last == RIGHT_BRACKETS.charAt(bracketId)) {
            return Optional.of(str.substring(1, str.length() - 1));
        } else {
            return Optional.empty();
        }
    }

    public Vec3i round() {
        return new Vec3i((int) Math.round(x), (int) Math.round(y), (int) Math.round(z));
    }

    @Override
    public String toString() {
        return x + ", " + y + ", " + z;
    }

}
