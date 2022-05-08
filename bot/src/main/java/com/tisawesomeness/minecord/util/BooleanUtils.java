package com.tisawesomeness.minecord.util;

import com.tisawesomeness.minecord.common.util.Validation;

import lombok.NonNull;

import java.util.Arrays;
import java.util.List;

@Deprecated
public final class BooleanUtils {

    private static final List<String> truthy = Arrays.asList("enabled", "yes", "y", "true", "t", "on", "1");
    private static final List<String> falsy = Arrays.asList("disabled", "no", "n", "false", "f", "off", "0");
    /**
     * Determines if the string input is truthy, falsy, or invalid. Ignores case.
     * @param input A user-provided string such as "true" or "enabled"
     * @return A Validation that is valid only if the input is truthy or falsy
     */
    public static Validation<Boolean> resolve(@NonNull String input) {
        if (truthy.contains(input.toLowerCase())) {
            return Validation.valid(true);
        }
        if (falsy.contains(input.toLowerCase())) {
            return Validation.valid(false);
        }
        return Validation.invalid("Not a valid value!");
    }

    /**
     * Gets an emote associated with true or false. There is no space at the end.
     * @param bool The boolean
     * @return The emote
     */
    public static String getEmote(boolean bool) {
        return bool ? ":white_check_mark:" : ":x:";
    }
}
