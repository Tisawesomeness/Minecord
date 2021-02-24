package com.tisawesomeness.minecord.lang;

import net.dv8tion.jda.api.Permission;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class LangTest {

    @Test
    @DisplayName("The default lang is not in development")
    public void testDefaultLang() {
        assertThat(Lang.getDefault().isInDevelopment()).isFalse();
    }

    @Test
    @DisplayName("Putting the lang's code in Lang.from() returns the same lang")
    public void testFromCode() {
        for (Lang lang : Lang.values()) {
            Optional<Lang> langFromCode = Lang.from(lang.getCode());
            assertThat(langFromCode).contains(lang);
        }
    }

    @ParameterizedTest(name = "{index} ==> Registering lang from nonsense ''{0}''")
    @EmptySource
    @ValueSource(strings = {"nonsense", "en", "EN", "us", "US", " ", "_", "lang"})
    @DisplayName("Trying to get a lang from nonsense fails")
    public void testFromNonsense(String candidate) {
        assertThat(Lang.from(candidate)).isNotPresent();
    }

    @ParameterizedTest(name = "{index} ==> Permission ''{0}'' has a localized name")
    @EnumSource
    @DisplayName("All permissions can be localized")
    public void testLocalizePermission(Permission candidate) {
        String unknown = Lang.getDefault().localize(Permission.UNKNOWN);
        if (candidate == Permission.UNKNOWN) {
            assertThat(unknown).isNotEmpty();
        } else {
            assertThat(Lang.getDefault().localize(candidate)).isNotEqualTo(unknown);
        }
    }

    @ParameterizedTest(name = "{index} ==> {0} equals() is true for two identical strings")
    @EnumSource
    @DisplayName("equals() is true for identical strings")
    public void testEquals(Lang candidate) {
        assertThat(candidate.equals("abc", "abc")).isTrue();
    }
    @ParameterizedTest(name = "{index} ==> {0} equals() is false for different strings")
    @EnumSource
    @DisplayName("equals() is false for for unambiguous different strings")
    public void testNotEquals(Lang candidate) {
        assertThat(candidate.equals("abc", "def")).isFalse();
    }

    @ParameterizedTest(name = "{index} ==> {0} equalsIgnoreCase() is true for different cased strings")
    @EnumSource
    @DisplayName("equalsIgnoreCase() is true for different cased strings")
    public void testEqualsIgnoreCase(Lang candidate) {
        assertThat(candidate.equalsIgnoreCase("abc", "ABC")).isTrue();
    }

    @ParameterizedTest(name = "{index} ==> {0} compare(a, b) is -1 when a < b")
    @EnumSource
    @DisplayName("compare(a, b) is -1 when a < b")
    public void testCompareLess(Lang candidate) {
        assertThat(candidate.compare("a", "z")).isEqualTo(-1);
    }
    @ParameterizedTest(name = "{index} ==> {0} compare(a, b) is 0 when a == b")
    @EnumSource
    @DisplayName("compare(a, b) is 0 when a == b")
    public void testCompare(Lang candidate) {
        assertThat(candidate.compare("a", "a")).isEqualTo(0);
    }
    @ParameterizedTest(name = "{index} ==> {0} compare(a, b) is 1 when a > b")
    @EnumSource
    @DisplayName("compare(a, b) is 1 when a > b")
    public void testCompareMore(Lang candidate) {
        assertThat(candidate.compare("z", "a")).isEqualTo(1);
    }

    @ParameterizedTest(name = "{index} ==> {0} compareIgnoreCase(a, b) is -1 when a < b")
    @EnumSource
    @DisplayName("compareIgnoreCase(a, b) is -1 when a < b")
    public void testCompareLessIgnoreCase(Lang candidate) {
        assertThat(candidate.compareIgnoreCase("a", "Z")).isEqualTo(-1);
    }
    @ParameterizedTest(name = "{index} ==> {0} compareIgnoreCase(a, b) is 0 when a == b")
    @EnumSource
    @DisplayName("compareIgnoreCase(a, b) is 0 when a == b")
    public void testCompareIgnoreCase(Lang candidate) {
        assertThat(candidate.compareIgnoreCase("A", "a")).isEqualTo(0);
    }
    @ParameterizedTest(name = "{index} ==> {0} compareIgnoreCase(a, b) is 1 when a > b")
    @EnumSource
    @DisplayName("compareIgnoreCase(a, b) is 1 when a > b")
    public void testCompareMoreIgnoreCase(Lang candidate) {
        assertThat(candidate.compareIgnoreCase("z", "A")).isEqualTo(1);
    }

    @Test
    @DisplayName("Comparisons with ß --> SS are correctly handled")
    public void testUppercaseTrickery() {
        assertThat(Lang.DE_DE.equals("ß", "SS")).isFalse();
        assertThat(Lang.DE_DE.equalsIgnoreCase("ß", "SS")).isTrue();
    }

}
