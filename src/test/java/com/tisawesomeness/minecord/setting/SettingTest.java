package com.tisawesomeness.minecord.setting;

import com.tisawesomeness.minecord.setting.impl.DeleteCommandsSetting;
import com.tisawesomeness.minecord.setting.impl.PrefixSetting;
import com.tisawesomeness.minecord.setting.impl.UseMenusSetting;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("Settings")
public class SettingTest {
    private static Setting<?>[] settings;

    @BeforeAll
    static void createSettings() {
        settings = new Setting[]{
                new PrefixSetting(),
                new DeleteCommandsSetting(),
                new UseMenusSetting()
        };
    }

    @Test
    @DisplayName("Settings that don't support users won't let you set for users")
    public void testSetUserSupport() {
        for (Setting<?> setting : settings) {
            if (!setting.supportsUsers()) {
                String from = setting.getDefault().toString();
                String to = "dummy";
                String expected = SetStatus.UNSUPPORTED.getMsg(setting.getDisplayName(), from, to);
                assertEquals(expected, setting.setUser(0, to));
            }
        }
    }
    @Test
    @DisplayName("Settings that don't support guilds won't let you set for guilds")
    public void testSetGuildSupport() {
        for (Setting<?> setting : settings) {
            if (!setting.supportsGuilds()) {
                String from = setting.getDefault().toString();
                String to = "dummy";
                String expected = SetStatus.UNSUPPORTED.getMsg(setting.getDisplayName(), from, to);
                assertEquals(expected, setting.setGuild(0, to));
            }
        }
    }
    @Test
    @DisplayName("Settings that don't support users won't let you reset for users")
    public void testResetUserSupport() {
        for (Setting<?> setting : settings) {
            if (!setting.supportsUsers()) {
                String from = setting.getDefault().toString();
                String expected = SetStatus.UNSUPPORTED.getMsg(setting.getDisplayName(), from, "");
                assertEquals(expected, setting.resetUser(0));
            }
        }
    }
    @Test
    @DisplayName("Settings that don't support guilds won't let you reset for guilds")
    public void testResetGuildSupport() {
        for (Setting<?> setting : settings) {
            if (!setting.supportsGuilds()) {
                String from = setting.getDefault().toString();
                String expected = SetStatus.UNSUPPORTED.getMsg(setting.getDisplayName(), from, "");
                assertEquals(expected, setting.resetGuild(0));
            }
        }
    }

}
