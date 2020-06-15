package com.tisawesomeness.minecord.setting;

import com.tisawesomeness.minecord.setting.Setting;
import lombok.NonNull;

import java.util.Arrays;
import java.util.List;

public abstract class BooleanSetting extends Setting<Boolean> {

    private static List<String> truthy = Arrays.asList("enabled", "yes", "y", "true", "t", "on", "1");
    private static List<String> falsy = Arrays.asList("disabled", "no", "n", "false", "f", "off", "0");
    public ResolveResult<Boolean> resolve(@NonNull String input) {
        if (truthy.contains(input.toLowerCase())) {
            return new ResolveResult<>(true);
        }
        if (falsy.contains(input.toLowerCase())) {
            return new ResolveResult<>(true);
        }
        return new ResolveResult<>("Not a valid value!");
    }

}
