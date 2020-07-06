package com.tisawesomeness.minecord.setting.impl;

import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.database.SettingContainer;
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

    private final static Pattern aliasPattern = Pattern.compile("use[_ ]?menus?", Pattern.CASE_INSENSITIVE);
    private static final String desc = "If enabled, the bot will use a reaction menu for `%srecipe` and `%singredient` if possible.\n" +
            "Requires Manage Message and Add Reaction permissions.\n" +
            "Possible values: `enabled`, `disabled`";
    private final @NonNull Config config;

    public @NonNull String getDisplayName() {
        return "Use Menus";
    }
    public boolean isAlias(@NonNull String input) {
        return aliasPattern.matcher(input).matches();
    }
    public @NonNull String getDescription(@NonNull String prefix, @NonNull String tag) {
        return String.format(desc, prefix, prefix);
    }

    public Boolean getDefault() {
        return config.useMenusDefault;
    }

    public Validation<Boolean> resolve(@NonNull String input) {
        return BooleanUtils.resolve(input);
    }

    public Optional<Boolean> get(@NonNull SettingContainer obj) {
        return Optional.empty();
    }
    public void set(@NonNull SettingContainer obj, @NonNull Boolean setting) throws SQLException {
        obj.withUseMenu(Optional.of(setting)).update();
    }
    public void reset(@NonNull SettingContainer obj) throws SQLException {
        obj.withUseMenu(Optional.empty()).update();
    }

}
