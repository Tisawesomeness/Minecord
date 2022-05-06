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
        assertThat(Lang.getDefault().getFeatures().isInDevelopment()).isFalse();
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

    @Test
    @DisplayName("Booleans without a format can be displayed")
    public void testDisplayBool() {
        Lang lang = Lang.getDefault();
        assertThat(lang.displayBool(true)).isNotEmpty();
        assertThat(lang.displayBool(false)).isNotEmpty();
    }

    @ParameterizedTest(name = "{index} ==> Boolean format ''{0}'' has a value for true and false")
    @EnumSource
    @DisplayName("All boolean formats can be displayed")
    public void testDisplayBool(BoolFormat format) {
        Lang lang = Lang.getDefault();
        assertThat(lang.displayBool(true, format)).isNotEmpty();
        assertThat(lang.displayBool(false, format)).isNotEmpty();
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

    @ParameterizedTest(name = "{index} ==> String {0} is a valid int")
    @DisplayName("Ints are safely parsed")
    @ValueSource(ints = {
            Integer.MIN_VALUE,
            -25565,
            -1,
            0,
            1,
            25565,
            Integer.MAX_VALUE
    })
    public void testParseIntValid(int candidate) {
        for (Lang lang : Lang.values()) {
            assertThat(lang.parseInt(String.valueOf(candidate))).hasValue(candidate);
        }
    }
    @ParameterizedTest(name = "{index} ==> String {0} is not an int")
    @DisplayName("Invalid int strings return empty")
    @ValueSource(strings = {
            "letters",
            "-",
            " ",
            "   ",
            "1\n2",
            "3\r4",
            "5\r\n6",
            "7\t8",
            "9 10",
            "123-456",
    })
    @EmptySource
    public void testParseIntInvalid(String candidate) {
        for (Lang lang : Lang.values()) {
            assertThat(lang.parseInt(candidate)).isEmpty();
        }
    }
    @Test
    @DisplayName("parseInt() caps instead of overflowing")
    public void testParseIntOverflow() {
        for (Lang lang : Lang.values()) {
            assertThat(lang.parseInt("2147483648")).hasValue(Integer.MAX_VALUE);
        }
    }
    @Test
    @DisplayName("parseInt() caps instead of underflowing")
    public void testParseIntUnderflow() {
        for (Lang lang : Lang.values()) {
            assertThat(lang.parseInt("-2147483649")).hasValue(Integer.MIN_VALUE);
        }
    }

    @Test
    @DisplayName("parseInt() follows English rules")
    public void testParseIntEnglish() {
        assertThat(Lang.EN_US.parseInt("123,456")).hasValue(123456);
        assertThat(Lang.EN_US.parseInt("123.456")).isEmpty();
    }

    @ParameterizedTest(name = "{index} ==> String {0} is a valid long")
    @DisplayName("Longs are safely parsed")
    @ValueSource(longs = {
            Long.MIN_VALUE,
            Integer.MIN_VALUE,
            -25565,
            -1,
            0,
            1,
            25565,
            Integer.MAX_VALUE,
            Long.MAX_VALUE
    })
    public void testParseIntValid(long candidate) {
        for (Lang lang : Lang.values()) {
            assertThat(lang.parseLong(String.valueOf(candidate))).hasValue(candidate);
        }
    }
    @ParameterizedTest(name = "{index} ==> String {0} is not a long")
    @DisplayName("Invalid long strings return empty")
    @ValueSource(strings = {
            "letters",
            "-",
            " ",
            "   ",
            "1\n2",
            "3\r4",
            "5\r\n6",
            "7\t8",
            "9 10",
            "123-456"
    })
    @EmptySource
    public void testParseLongInvalid(String candidate) {
        for (Lang lang : Lang.values()) {
            assertThat(lang.parseLong(candidate)).isEmpty();
        }
    }
    @Test
    @DisplayName("parseLong() caps instead of overflowing")
    public void testParseLongOverflow() {
        for (Lang lang : Lang.values()) {
            assertThat(lang.parseLong("9223372036854775808")).hasValue(Long.MAX_VALUE);
        }
    }
    @Test
    @DisplayName("parseLong() caps instead of underflowing")
    public void testParseLongUnderflow() {
        for (Lang lang : Lang.values()) {
            assertThat(lang.parseLong("-9223372036854775809")).hasValue(Long.MIN_VALUE);
        }
    }

    @Test
    @DisplayName("parseLong() follows English rules")
    public void testParseLongEnglish() {
        assertThat(Lang.EN_US.parseLong("123,456")).hasValue(123456L);
        assertThat(Lang.EN_US.parseLong("123.456")).isEmpty();
    }

    @Test
    @DisplayName("isTruthy() is true for 'true' in all languages")
    public void testIsTruthyTrue() {
        for (Lang lang : Lang.values()) {
            assertThat(lang.isTruthy("true")).isTrue();
        }
    }
    @Test
    @DisplayName("isTruthy() is false for 'false' in all languages")
    public void testIsTruthyFalse() {
        for (Lang lang : Lang.values()) {
            assertThat(lang.isTruthy("false")).isFalse();
        }
    }
    @Test
    @DisplayName("isFalsy() is false for 'true' in all languages")
    public void testIsFalsyTrue() {
        for (Lang lang : Lang.values()) {
            assertThat(lang.isFalsy("true")).isFalse();
        }
    }
    @Test
    @DisplayName("isFalsy() is true for 'false' in all languages")
    public void testIsFalsyFalse() {
        for (Lang lang : Lang.values()) {
            assertThat(lang.isFalsy("false")).isTrue();
        }
    }

}
