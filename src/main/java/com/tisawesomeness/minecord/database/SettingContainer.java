package com.tisawesomeness.minecord.database;

import com.tisawesomeness.minecord.Lang;
import com.tisawesomeness.minecord.database.dao.DbObject;

import java.util.Optional;

/**
 * An object containing values for each {@link com.tisawesomeness.minecord.setting.Setting}.
 */
public interface SettingContainer extends DbObject {
    // Methods in subclass generated by lombok
    Optional<String> getPrefix();
    SettingContainer withPrefix(Optional<String> prefix);
    Optional<Lang> getLang();
    SettingContainer withLang(Optional<Lang> lang);
    Optional<Boolean> getUseMenu();
    SettingContainer withUseMenu(Optional<Boolean> useMenus);
}
