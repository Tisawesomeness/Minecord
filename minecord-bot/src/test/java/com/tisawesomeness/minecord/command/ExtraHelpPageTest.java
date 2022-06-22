package com.tisawesomeness.minecord.command;

import com.tisawesomeness.minecord.lang.Lang;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

public class ExtraHelpPageTest {

    @ParameterizedTest(name = "{index} ==> {0} has no whitespace")
    @EnumSource
    @DisplayName("All extra help pages have names and aliases with no spaces")
    public void testNoIdWhitespace(ExtraHelpPage ehp) {
        assertThat(ehp.getId()).doesNotContainAnyWhitespaces();
    }

    @ParameterizedTest(name = "{index} ==> The name and aliases of {0} have no whitespace")
    @EnumSource
    @DisplayName("All extra help pages have names and aliases with no spaces")
    public void testNoNameOrAliasWhitespace(Lang lang) {
        for (ExtraHelpPage ehp : ExtraHelpPage.values()) {
            assertThat(lang.localize(ehp)).doesNotContainAnyWhitespaces();
            assertThat(ehp.getAliases(lang)).allSatisfy(s -> assertThat(s).doesNotContainAnyWhitespaces());
        }
    }

}
