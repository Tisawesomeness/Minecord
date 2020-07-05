package com.tisawesomeness.minecord.util;

import com.tisawesomeness.minecord.util.type.Validation;

import lombok.NonNull;

import java.util.Arrays;
import java.util.List;

public class BooleanUtils {

    private static final List<String> truthy = Arrays.asList("enabled", "yes", "y", "true", "t", "on", "1");
    private static final List<String> falsy = Arrays.asList("disabled", "no", "n", "false", "f", "off", "0");
    public static Validation<Boolean> resolve(@NonNull String input) {
        if (truthy.contains(input.toLowerCase())) {
            return Validation.valid(true);
        }
        if (falsy.contains(input.toLowerCase())) {
            return Validation.valid(false);
        }
        return Validation.invalid("Not a valid value!");
    }

}
