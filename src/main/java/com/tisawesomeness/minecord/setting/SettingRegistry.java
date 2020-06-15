package com.tisawesomeness.minecord.setting;

import com.tisawesomeness.minecord.setting.impl.DeleteCommandsSetting;
import com.tisawesomeness.minecord.setting.impl.PrefixSetting;
import com.tisawesomeness.minecord.setting.impl.UseMenusSetting;
import lombok.NonNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SettingRegistry {
    public final @NonNull PrefixSetting prefix;
    public final @NonNull DeleteCommandsSetting deleteCommands;
    public final @NonNull UseMenusSetting useMenus;
    private final List<Setting<?>> settingsList;

    public SettingRegistry() {
        prefix = new PrefixSetting();
        deleteCommands = new DeleteCommandsSetting();
        useMenus = new UseMenusSetting();
        settingsList = Arrays.asList(prefix, deleteCommands, useMenus);
    }

    public Optional<Setting<?>> get(String name) {
        for (Setting<?> setting : settingsList) {
            if (setting.isAlias(name)) {
                return Optional.of(setting);
            }
        }
        return Optional.empty();
    }
}
