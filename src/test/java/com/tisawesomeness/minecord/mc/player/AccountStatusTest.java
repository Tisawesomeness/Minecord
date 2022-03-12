package com.tisawesomeness.minecord.mc.player;

import com.tisawesomeness.minecord.lang.Lang;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

public class AccountStatusTest {

    @ParameterizedTest(name = "Account status {0} can be localized")
    @EnumSource(AccountStatus.class)
    @DisplayName("All account statuses can be localized")
    public void testLocalization(AccountStatus candidate) {
        assertThat(Lang.getDefault().localize(candidate)).isNotEmpty();
    }

    @ParameterizedTest(name = "Account status {0} can be looked up by key")
    @EnumSource(AccountStatus.class)
    @DisplayName("All account statuses can be looked up by key")
    public void testFrom(AccountStatus candidate) {
        assertThat(AccountStatus.from(candidate.getKey())).contains(candidate);
    }

}