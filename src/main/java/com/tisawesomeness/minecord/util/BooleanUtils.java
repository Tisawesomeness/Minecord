package com.tisawesomeness.minecord.util;

import com.tisawesomeness.minecord.setting.ResolveResult;

import lombok.NonNull;

import java.util.Arrays;
import java.util.List;

public class BooleanUtils {

    private static final List<String> truthy = Arrays.asList("enabled", "yes", "y", "true", "t", "on", "1");
    private static final List<String> falsy = Arrays.asList("disabled", "no", "n", "false", "f", "off", "0");
    public static ResolveResult<Boolean> resolve(@NonNull String input) {
        if (truthy.contains(input.toLowerCase())) {
            return new ResolveResult<>(true);
        }
        if (falsy.contains(input.toLowerCase())) {
            return new ResolveResult<>(true);
        }
        return new ResolveResult<>("Not a valid value!");
    }

}
