package com.tisawesomeness.minecord.command;

import com.tisawesomeness.minecord.lang.Lang;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

public class CategoryTest {

    @Test
    @DisplayName("No category with the ID 'meta' exists, since that id can conflict")
    public void testNoMetaConflict() {
        for (Category m : Category.values()) {
            assertThat(m.getId()).isNotEqualTo("meta");
        }
    }

    @ParameterizedTest(name = "{index} ==> Category ''{0}'' can be localized")
    @EnumSource
    @DisplayName("All categories can be localized")
    public void testLocalization(Category candidate) {
        assertThat(Lang.getDefault().localize(candidate)).isNotEmpty();
    }

}
