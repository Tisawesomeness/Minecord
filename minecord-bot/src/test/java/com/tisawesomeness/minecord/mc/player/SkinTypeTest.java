package com.tisawesomeness.minecord.mc.player;

import com.tisawesomeness.minecord.lang.Lang;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

public class SkinTypeTest {

    @ParameterizedTest
    @EnumSource
    @DisplayName("All skin types can be localized")
    public void testLocalization(SkinType candidate) {
        assertThat(Lang.getDefault().localize(candidate)).isNotEmpty();
    }

}
