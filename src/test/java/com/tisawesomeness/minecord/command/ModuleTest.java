package com.tisawesomeness.minecord.command;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ModuleTest {

    @Test
    @DisplayName("No module with the ID 'meta' exists, since that id can conflict")
    public void testNoMetaConflict() {
        for (Module m : Module.values()) {
            assertThat(m.getId()).isNotEqualTo("meta");
        }
    }

}
