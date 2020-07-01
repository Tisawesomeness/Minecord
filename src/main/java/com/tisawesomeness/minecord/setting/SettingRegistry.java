package com.tisawesomeness.minecord.setting;

import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.database.Database;
import com.tisawesomeness.minecord.setting.impl.PrefixSetting;
import com.tisawesomeness.minecord.setting.impl.UseMenusSetting;

import lombok.NonNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SettingRegistry implements Iterable<Setting<?>> {
    public final @NonNull PrefixSetting prefix;
    public final @NonNull UseMenusSetting useMenus;
    private final List<Setting<?>> settingsList;
    public final List<ChannelSetting<?>> channelSettings;
    public final List<ServerSetting<?>> serverSettings;
    public final List<GlobalSetting<?>> globalSettings;

    /**
     * Initializes all the settings and makes them searchable from this registry.
     * @param config The loaded config file with the setting defaults.
     * @param db The loaded database where the settings are stored.
     */
    public SettingRegistry(@NonNull Config config, @NonNull Database db) {
        prefix = new PrefixSetting(config, db);
        useMenus = new UseMenusSetting(config, db);
        settingsList = Arrays.asList(prefix, useMenus);

        channelSettings = Collections.unmodifiableList(settingsList.stream()
                .filter(s -> s instanceof ChannelSetting<?>)
                .map(s -> (ChannelSetting<?>) s)
                .collect(Collectors.toList()));
        serverSettings = Collections.unmodifiableList(settingsList.stream()
                .filter(s -> s instanceof ServerSetting<?>)
                .map(s -> (ServerSetting<?>) s)
                .collect(Collectors.toList()));
        globalSettings = Collections.unmodifiableList(settingsList.stream()
                .filter(s -> s instanceof GlobalSetting<?>)
                .map(s -> (GlobalSetting<?>) s)
                .collect(Collectors.toList()));
    }

    /**
     * <p>Gets the setting that should be returned for {@code &setting input ...}</p>
     * Uses {@link ISetting#isAlias(String name)} to check if the input matches.
     * @param name The name or alias of the setting.
     * @return The setting if found, else empty.
     */
    public Optional<Setting<?>> get(@NonNull String name) {
        for (Setting<?> setting : settingsList) {
            if (setting.isAlias(name)) {
                return Optional.of(setting);
            }
        }
        return Optional.empty();
    }

    @Override
    public Iterator<Setting<?>> iterator() {
        return settingsList.iterator();
    }

}
