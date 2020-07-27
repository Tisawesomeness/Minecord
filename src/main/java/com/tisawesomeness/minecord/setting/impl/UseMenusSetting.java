package com.tisawesomeness.minecord.setting.impl;

import com.tisawesomeness.minecord.config.serial.SettingsConfig;
import com.tisawesomeness.minecord.database.dao.SettingContainer;
import com.tisawesomeness.minecord.setting.Setting;
import com.tisawesomeness.minecord.util.BooleanUtils;
import com.tisawesomeness.minecord.util.type.Validation;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.sql.SQLException;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Whether or not the {@code &recipe} and {@code &ingredient} should use a {@link com.tisawesomeness.minecord.ReactMenu}.
 */
@RequiredArgsConstructor
public class UseMenusSetting extends Setting<Boolean> {

    private static final Pattern ALIAS_PATTERN = Pattern.compile("use[_ ]?menus?", Pattern.CASE_INSENSITIVE);
    private static final String DESC = "If enabled, the bot will use a reaction menu for `%srecipe` and `%singredient` if possible.\n" +
            "Requires Manage Message and Add Reaction permissions.\n" +
            "Possible values: `enabled`, `disabled`";
    private final @NonNull SettingsConfig config;

    public @NonNull String getDisplayName() {
        return "Use Menus";
    }
    public boolean isAlias(@NonNull String input) {
        return ALIAS_PATTERN.matcher(input).matches();
    }
    public @NonNull String getDescription(@NonNull String prefix, @NonNull String tag) {
        return String.format(DESC, prefix, prefix);
    }

    public @NonNull Boolean getDefault() {
        return config.isUseMenusDefault();
    }

    public Validation<Boolean> resolve(@NonNull String input) {
        return BooleanUtils.resolve(input);
    }
    public @NonNull String display(Boolean setting) {
        return setting ? "enabled" : "disabled";
    }

    public Optional<Boolean> get(@NonNull SettingContainer obj) {
        return obj.getUseMenu();
    }
    public void set(@NonNull SettingContainer obj, @NonNull Boolean setting) throws SQLException {
        obj.withUseMenu(Optional.of(setting)).update();
    }
    public void reset(@NonNull SettingContainer obj) throws SQLException {
        obj.withUseMenu(Optional.empty()).update();
    }

}
