package com.tisawesomeness.minecord.setting;

import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.database.Database;
import lombok.NonNull;

import java.sql.SQLException;
import java.util.Optional;

public class DeleteCommandsSetting extends BooleanSetting {
    public @NonNull String getDisplayName() {
        return "Delete Commands";
    }
    public boolean isAlias(@NonNull String input) {
        return input.equalsIgnoreCase("delete commands");
    }
    public @NonNull String getDescription(String prefix, String tag) {
        return "If enabled, the bot will delete command messages to clear up space.\n" +
                "Requires Manage Message permissions.\n" +
                "Possible values: `enabled`, `disabled`";
    }

    public Optional<Boolean> getUser(long id) {
        return Optional.of(false); // No permissions in DMs
    }
    public Optional<Boolean> getGuild(long id) {
        return Optional.of(Database.getDeleteCommands(id));
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
