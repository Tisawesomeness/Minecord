package com.tisawesomeness.minecord.setting;

import com.tisawesomeness.minecord.testutil.DummyConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("SettingRegistry")
public class SettingRegistryTest {

    private static SettingRegistry registry;
    private static Setting<?>[] settings;
    @BeforeAll
    static void createSettings() throws IOException {
        registry = new SettingRegistry(new DummyConfig());
        settings = new Setting[]{registry.prefix, registry.deleteCommands, registry.useMenus};
    }

    @Test
    @DisplayName("Searching the setting registry for the display name of a setting returns that setting")
    public void testGet() {
        for (Setting<?> setting : settings) {
            Optional<Setting<?>> search = registry.get(setting.getDisplayName());
            assertTrue(search.isPresent());
            assertEquals(setting, search.get());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"nonsense", "", "0", "null", "preefix"})
    @DisplayName("Searching the setting registry for nonsense returns not found")
    public void testIsAliasNonsense(String candidate) {
        assertFalse(registry.get(candidate).isPresent());
    }

}
