package com.tisawesomeness.minecord.setting;

import com.tisawesomeness.minecord.setting.impl.DeleteCommandsSetting;
import com.tisawesomeness.minecord.setting.impl.PrefixSetting;
import com.tisawesomeness.minecord.setting.impl.UseMenusSetting;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
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
    @DisplayName("Display name is a valid setting alias")
    public void testIsAlias() {
        for (Setting<?> setting : settings) {
            assertTrue(setting.isAlias(setting.getDisplayName()));
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"nonsense", "", "0", "null", "preefix"})
    @DisplayName("Nonsense is not a valid setting alias")
    public void testIsAliasNonsense(String candidate) {
        for (Setting<?> setting : settings) {
            assertFalse(setting.isAlias(candidate));
        }
    }

}
