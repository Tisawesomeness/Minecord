package com.tisawesomeness.minecord.database;

import com.tisawesomeness.minecord.Lang;

import java.sql.SQLException;
import java.util.Optional;

public interface DMSettingContainer {
    long getId();
    boolean isBanned();

    Optional<String> getPrefix();
    SettingContainer withPrefix(Optional<String> prefix);
    Optional<Lang> getLang();
    SettingContainer withLang(Optional<Lang> lang);

    void update() throws SQLException;
}
