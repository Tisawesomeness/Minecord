package com.tisawesomeness.minecord.setting.impl;

import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.database.Database;
import com.tisawesomeness.minecord.setting.BooleanSetting;
import lombok.NonNull;

import java.sql.SQLException;
import java.util.Optional;

public class UseMenusSetting extends BooleanSetting {
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

    public boolean supportsUsers() {
        return false;
    }
    public boolean supportsGuilds() {
        return true;
    }

    public Optional<Boolean> getUser(long id) {
        return Optional.of(false); // No permissions in DMs
    }
    public Optional<Boolean> getGuild(long id) {
        return Optional.of(Database.getUseMenu(id));
    }
    public Boolean getDefault() {
        return Config.getDeleteCommands();
    }

    protected boolean changeUser(long id, @NonNull Boolean setting) {
        return false; // No permissions in DMs
    }
    protected boolean changeGuild(long id, @NonNull Boolean setting) {
        try {
            Database.changeDeleteCommands(id, setting);
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }
    protected boolean clearUser(long id) {
        return false; // No permissions in DMs
    }
    protected boolean clearGuild(long id) {
        return false; // Not possible right now
    }
}
