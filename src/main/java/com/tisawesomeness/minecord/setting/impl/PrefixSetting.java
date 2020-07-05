package com.tisawesomeness.minecord.setting.impl;

import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.database.DMSettingContainer;
import com.tisawesomeness.minecord.setting.DMSetting;
import com.tisawesomeness.minecord.util.type.Validation;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.sql.SQLException;
import java.util.Optional;

@RequiredArgsConstructor
public class PrefixSetting extends DMSetting<String> {

    private static final String desc = "The prefix used before every command.\n" +
            "`@%s command` will work regardless of prefix.\n" +
            "Possible values: Any text between 1-8 characters.";
    private final @NonNull Config config;

    public @NonNull String getDisplayName() {
        return "Prefix";
    }
    public boolean isAlias(@NonNull String input) {
        return input.equalsIgnoreCase("prefix");
    }
    public @NonNull String getDescription(@NonNull String prefix, @NonNull String tag) {
        return String.format(desc, tag);
    }

    public String getDefault() {
        return config.prefixDefault;
    }

    public Validation<String> resolve(@NonNull String input) {
        if (input.length() > 8) {
            return Validation.invalid("The prefix is too long!");
        }
        return Validation.valid(input);
    }

    public Optional<String> get(@NonNull DMSettingContainer obj) {
        return obj.getPrefix();
    }
    public void set(@NonNull DMSettingContainer obj, @NonNull String setting) throws SQLException {
        obj.withPrefix(Optional.of(setting)).update();
    }
    public void reset(@NonNull DMSettingContainer obj) throws SQLException {
        obj.withPrefix(Optional.empty()).update();
    }
}
