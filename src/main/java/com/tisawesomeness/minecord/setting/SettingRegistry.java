package com.tisawesomeness.minecord.setting;

import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.setting.impl.PrefixSetting;
import com.tisawesomeness.minecord.setting.impl.UseMenusSetting;

import lombok.NonNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Keeps a list of loaded settings.
 */
public class SettingRegistry {
    public final @NonNull PrefixSetting prefix;
    public final @NonNull UseMenusSetting useMenus;
    public final List<Setting<?>> settingsList;

    /**
     * Initializes all the settings and makes them searchable from this registry.
     * @param config The loaded config file with the setting defaults.
     */
    public SettingRegistry(@NonNull Config config) {
        prefix = new PrefixSetting(config);
        useMenus = new UseMenusSetting(config);
        settingsList = Collections.unmodifiableList(Arrays.asList(prefix, useMenus));
    }

    /**
     * Gets the setting that should be returned for {@code &setting input ...}.
     * <br>Uses {@link ISetting#isAlias(String name)} to check if the input matches.
     * @param name The name or alias of the setting.
     * @return The setting if found, else empty.
     */
    public Optional<Setting<?>> getSetting(@NonNull String name) {
        return settingsList.stream()
                .filter(s -> s.isAlias(name))
                .findFirst();
    }
}
