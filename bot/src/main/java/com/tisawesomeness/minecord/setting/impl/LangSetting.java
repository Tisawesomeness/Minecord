package com.tisawesomeness.minecord.setting.impl;

import com.tisawesomeness.minecord.common.util.Validation;
import com.tisawesomeness.minecord.config.config.SettingsConfig;
import com.tisawesomeness.minecord.database.dao.SettingContainer;
import com.tisawesomeness.minecord.lang.Lang;
import com.tisawesomeness.minecord.setting.Setting;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.sql.SQLException;
import java.util.Optional;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class LangSetting extends Setting<Lang> {

    private static final Pattern ALIAS_PATTERN = Pattern.compile("lang(uage)?", Pattern.CASE_INSENSITIVE);
    private final @NonNull SettingsConfig config;

    public @NonNull String getDisplayName() {
        return "Language";
    }
    public boolean isAlias(@NonNull String input) {
        return ALIAS_PATTERN.matcher(input).matches();
    }
    public @NonNull String getDescription(@NonNull String prefix, @NonNull String tag) {
        return String.format("Sets the language used for commands and bot responses.\n" +
                "See `%slang` for possible values.", prefix);
    }

    public @NonNull Lang getDefault() {
        return config.getDefaultLang();
    }

    public Validation<Lang> resolve(@NonNull String input) {
        return Validation.fromOptional(Lang.from(input), "That is not a valid language.");
    }
    public @NonNull String display(Lang setting) {
        return setting.getCode();
    }

    public Optional<Lang> get(@NonNull SettingContainer obj) {
        return obj.getLang();
    }
    public void set(@NonNull SettingContainer obj, @NonNull Lang setting) throws SQLException {
        obj.withLang(Optional.of(setting)).update();
    }
    public void reset(@NonNull SettingContainer obj) throws SQLException {
        obj.withLang(Optional.empty()).update();
    }
}
