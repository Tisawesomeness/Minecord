package com.tisawesomeness.minecord.database;

import java.util.Optional;

public interface SettingContainer extends DMSettingContainer {
    Optional<Boolean> getUseMenu();
    DMSettingContainer withUseMenu(Optional<Boolean> useMenus);
}
