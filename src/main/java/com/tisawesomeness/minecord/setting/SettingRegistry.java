package com.tisawesomeness.minecord.setting;

import com.tisawesomeness.minecord.config.serial.SettingsConfig;
import com.tisawesomeness.minecord.setting.impl.LangSetting;
import com.tisawesomeness.minecord.setting.impl.PrefixSetting;
import com.tisawesomeness.minecord.setting.impl.UseMenusSetting;

import lombok.NonNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Keeps a list of loaded settings.
 */
public class SettingRegistry implements Iterable<Setting<?>> {
    public final @NonNull PrefixSetting prefix;
    public final @NonNull LangSetting lang;
    public final @NonNull UseMenusSetting useMenus;
    private final List<Setting<?>> settingsList;

    /**
     * Initializes all the settings and makes them searchable from this registry.
     * @param config The loaded config file with the setting defaults.
     */
    public SettingRegistry(@NonNull SettingsConfig config) {
        prefix = new PrefixSetting(config);
        lang = new LangSetting(config);
        useMenus = new UseMenusSetting(config);
        settingsList = List.of(prefix, lang, useMenus);
    }

    /**
     * Gets the setting that should be returned for {@code &setting input ...}.
     * <br>Uses {@link ISetting#isAlias(String name)} to check if the input matches.
     * @param name The name or alias of the setting.
     * @return The setting if found, else empty.
     */
    public Optional<Setting<?>> getSetting(@NonNull String name) {
        return stream()
                .filter(s -> s.isAlias(name))
                .findFirst();
    }

    /**
     * Enables the registry to be used in for each loops.
     * @return An iterator over all settings
     */
    public @NonNull Iterator<Setting<?>> iterator() {
        return settingsList.iterator();
    }

    /**
     * Creates a stream for all registered settings.
     * @return A stream over all settings
     */
    public Stream<Setting<?>> stream() {
        return settingsList.stream();
    }
}
