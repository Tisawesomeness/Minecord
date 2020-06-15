package com.tisawesomeness.minecord.setting;

import com.tisawesomeness.minecord.setting.impl.DeleteCommandsSetting;
import com.tisawesomeness.minecord.setting.impl.PrefixSetting;
import com.tisawesomeness.minecord.setting.impl.UseMenusSetting;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("ISetting")
public class ISettingTest {
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
    @DisplayName("Display name is a valid alias")
    public void testIsAlias() {
        for (Setting<?> setting : settings) {
            assertTrue(setting.isAlias(setting.getDisplayName()));
        }
    }
    @Test
    @DisplayName("Every setting supports either users or guilds")
    public void testHasSupport() {
        for (Setting<?> setting : settings) {
            assertTrue(setting.supportsUsers() || setting.supportsGuilds());
        }
    }

}
