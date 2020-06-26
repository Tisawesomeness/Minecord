package com.tisawesomeness.minecord.setting.impl;

import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.database.Database;
import com.tisawesomeness.minecord.setting.ResolveResult;
import com.tisawesomeness.minecord.setting.ServerSetting;
import com.tisawesomeness.minecord.util.BooleanUtils;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.sql.SQLException;
import java.util.Optional;

@RequiredArgsConstructor
public class UseMenusSetting extends ServerSetting<Boolean> {

    private final @NonNull Config config;
    private final @NonNull Database db;

    public @NonNull String getDisplayName() {
        return "Use Menus";
    }
    public boolean isAlias(@NonNull String input) {
        return input.equalsIgnoreCase("use menus");
    }
    private static final String desc = "If enabled, the bot will use a reaction menu for `%srecipe` and `%singredient` if possible.\n" +
            "Requires Manage Message and Add Reaction permissions.\n" +
            "Possible values: `enabled`, `disabled`";
    public @NonNull String getDescription(String prefix, String tag) {
        return String.format(desc, prefix, prefix);
    }

    public Optional<Boolean> getChannel(long id) {
        return Optional.empty();
    }
    public Optional<Boolean> getGuild(long id) {
        return Optional.of(db.getUseMenu(id));
    }
    public @NonNull Boolean getDefault() {
        return config.shouldUseMenusDefault();
    }

    protected boolean changeChannel(long id, @NonNull Boolean setting) {
        return false; // Not possible right now
    }
    protected boolean changeGuild(long id, @NonNull Boolean setting) {
        try {
            db.changeUseMenu(id, setting);
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }
    protected boolean clearChannel(long id) {
        return false; // Not possible right now
    }
    protected boolean clearGuild(long id) {
        return false; // Not possible right now
    }

    public ResolveResult<Boolean> resolve(@NonNull String input) {
        return BooleanUtils.resolve(input);
    }

}
