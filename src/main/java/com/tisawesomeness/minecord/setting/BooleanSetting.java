package com.tisawesomeness.minecord.setting;

import lombok.NonNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public abstract class BooleanSetting extends Setting<Boolean> {

    private static List<String> truthy = Arrays.asList("enabled", "yes", "y", "true", "t", "on", "1");
    private static List<String> falsy = Arrays.asList("disabled", "no", "n", "false", "f", "off", "0");
    public Optional<Boolean> resolve(@NonNull String input) {
        if (truthy.contains(input.toLowerCase())) {
            return Optional.of(true);
        }
        return falsy.contains(input.toLowerCase()) ? Optional.of(false) : Optional.empty();
    }

}
