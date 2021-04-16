package com.tisawesomeness.minecord.command;

import com.tisawesomeness.minecord.command.meta.Category;
import com.tisawesomeness.minecord.lang.Lang;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class CategoryTest {

    @Test
    @DisplayName("No category with the ID 'meta' exists, since that id can conflict")
    public void testNoMetaConflict() {
        for (Category cat : Category.values()) {
            assertThat(cat.getId()).isNotEqualTo("meta");
        }
    }

    @ParameterizedTest(name = "{index} ==> Category ''{0}'' can be localized")
    @EnumSource
    @DisplayName("All categories can be localized")
    public void testLocalization(Category candidate) {
        assertThat(Lang.getDefault().localize(candidate)).isNotEmpty();
    }

    @ParameterizedTest(name = "{index} ==> Category ''{0}'' does not conflict with &help extra")
    @EnumSource
    @DisplayName("All categories do not conflict with &help extra")
    public void testExtraConflict(Lang lang) {
        String extraDefault = Lang.getDefault().i18n("command.core.help.extra");
        String extra = lang.i18n("command.core.help.extra");
        for (Category cat : Category.values()) {
            String name = lang.localize(cat);
            if (lang.equalsIgnoreCase(name, extraDefault)) {
                fail("Conflict found in category %s, lang %s, extra %s",
                        cat, lang, extraDefault);
            }
            if (lang.equalsIgnoreCase(name, extra)) {
                fail("Conflict found in category %s, lang %s, extra %s",
                        cat, lang, extra);
            }
        }
    }

}
