package com.tisawesomeness.minecord.setting.impl;

import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.database.Database;
import com.tisawesomeness.minecord.setting.GlobalSetting;
import com.tisawesomeness.minecord.setting.ResolveResult;
import com.tisawesomeness.minecord.setting.ServerSetting;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.sql.SQLException;
import java.util.Optional;

// TODO add DM support
@RequiredArgsConstructor
public class PrefixSetting extends ServerSetting<String> {
//public class PrefixSetting extends GlobalSetting<String> {

    private final @NonNull Config config;
    private final @NonNull Database db;

    public @NonNull String getDisplayName() {
        return "Prefix";
    }
    public boolean isAlias(@NonNull String input) {
        return input.equalsIgnoreCase("prefix");
    }
    private static final String desc = "The prefix used before every command.\n" +
            "`@%s command` will work regardless of prefix.\n" +
            "Possible values: Any text between 1-8 characters.";
    public String getDescription(@NonNull String prefix, @NonNull String tag) {
        return String.format(desc, tag);
    }

    public Optional<String> getChannel(long id) {
        return Optional.empty();
    }
//    public Optional<String> getUser(long id) {
//        return Optional.empty();
//    }
    public Optional<String> getGuild(long id) {
        return Optional.ofNullable(db.getPrefix(id));
    }
    public @NonNull String getDefault() {
        return config.getPrefixDefault();
    }

    protected boolean changeChannel(long id, @NonNull String setting) {
        return false; // Not possible right now
    }
//    protected boolean changeUser(long id, @NonNull String setting) {
//        return false; // Not possible right now
//    }
    protected boolean changeGuild(long id, @NonNull String setting) {
        try {
            db.changePrefix(id, setting);
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    protected boolean clearChannel(long id) {
        return false; // Not possible right now
    }
//    protected boolean clearUser(long id) {
//        return false; // Not possible right now
//    }
    protected boolean clearGuild(long id) {
        return false; // Not possible right now
    }

    public ResolveResult<String> resolve(String input) {
        if (input.length() > 8) {
            return new ResolveResult<>(Optional.empty(), "The prefix is too long!");
        }
        return new ResolveResult<>(Optional.of(input), null);
    }

}
