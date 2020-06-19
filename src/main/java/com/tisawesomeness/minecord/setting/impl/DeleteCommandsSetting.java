package com.tisawesomeness.minecord.setting.impl;

import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.database.Database;
import com.tisawesomeness.minecord.util.BooleanUtils;
import com.tisawesomeness.minecord.setting.ResolveResult;
import com.tisawesomeness.minecord.setting.ServerSetting;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.sql.SQLException;
import java.util.Optional;

@RequiredArgsConstructor
public class DeleteCommandsSetting extends ServerSetting<Boolean> {

    private final @NonNull Config config;

    public @NonNull String getDisplayName() {
        return "Delete Commands";
    }
    public boolean isAlias(@NonNull String input) {
        return input.equalsIgnoreCase("delete commands");
    }
    public @NonNull String getDescription(@NonNull String prefix, @NonNull String tag) {
        return "If enabled, the bot will delete command messages to clear up space.\n" +
                "Requires Manage Message permissions.\n" +
                "Possible values: `enabled`, `disabled`";
    }

    public Optional<Boolean> getChannel(long id) {
        return Optional.empty();
    }
    public Optional<Boolean> getGuild(long id) {
        return Optional.of(Database.getDeleteCommands(id));
    }
    public @NonNull Boolean getDefault() {
        return config.shouldDeleteCommandsDefault();
    }

    protected boolean changeChannel(long id, @NonNull Boolean setting) {
        return false; // Not possible right now
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
