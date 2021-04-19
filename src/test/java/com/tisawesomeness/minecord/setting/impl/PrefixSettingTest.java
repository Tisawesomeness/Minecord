package com.tisawesomeness.minecord.setting.impl;

import com.tisawesomeness.minecord.config.ConfigReader;
import com.tisawesomeness.minecord.config.config.Config;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class PrefixSettingTest {

    private static PrefixSetting prefix;

    @BeforeAll
    private static void initConfig() throws JsonProcessingException {
        Config config = ConfigReader.readFromResources();
        prefix = new PrefixSetting(config.getSettingsConfig());
    }

    @ParameterizedTest(name = "{index} ==> Prefix ''{0}'' is accepted")
    @ValueSource(strings = {
            "&", // used in bot documentation
            ">&", "mc!", // common alternatives
            "abc!", "Abc!", "ABC!", "az!", "Az!", "aZ!", "AZ!", // letter case
            "0", "1", "5", "9", // single digits
            "00", "11", "1234", "55", "99", //multiple digits
            "~test!", "te~st!", // not strikethrough formatting
            "|test!", "te|st!" // not spoiler formatting
    })
    @MethodSource("symbolProvider")
    @DisplayName("Valid prefixes are accepted")
    public void testResolveValid(String candidate) {
        assertThat(prefix.resolve(candidate).isValid()).isTrue();
    }

    @ParameterizedTest(name = "{index} ==> Prefix ''{0}'' is rejected")
    @ValueSource(strings = {
            "abc", "Abc", "ABC", "az", "Az", "aZ", "AZ", "mc", "unset", // ends with letter
            " ", "  ", "   ", "\t", "\t\t", " \t ", "\t \t", // whitespace
            "\n", "\n\n", "\r", "\r\r", "\n\r", "\r\n", // newlines
            "\uD83D\uDE48", "\uD83D\uDE49", "\uD83D\uDE4A", // see no bugs, hear no bugs, write no bugs
            "Ω!", "√!", "¿!", // unicode symbols
            "“!", // smart quotes
            "田!", // two-character unicode
            "１!", // numeric unicode
            " !", // ogham space
            "\u0435!", // cryllic e
            "\uD83E\uDD14!", // :thinking:
            "@Minecord#8617!", "@Minecord!", // bot mentions
            "@Tis_awesomeness#8617!", "@Tis_awesomeness!", "@Tis#8617!", "@Tis!", "hi @Tis!", "hi@Tis!", // user mentions
            "<@!211261249386708992>!", "<@211261249386708992>!", // user mention codes
            "#announcements!", "#rules!", "#a-b!", // channel mentions
            "<#211261249386708992>!", // channel mention code
            "@Bot!", "@Mod!", // role mentions
            "<@&211261249386708992>!", // role mention code
            "@everyone!", "@here!", "hi @everyone!", "hi @here!", "hi@everyone!", "hi@here!", // ping reeeee
            ":joy:!", ":joy: !", ":GWpingSock:!", ":ab:!", // emotes
            "<:joy:211261249386708992>!", "<:GWPingSock:211261249386708992>!", // emote mention codes
            "~~test!", "te~~st!", "test~~", "test~", // strikethrough formatting
            "||test!", "te||st!", "test||", "test|" // spoiler formatting
    })
    @MethodSource("invalidSymbolProvider")
    @DisplayName("Invalid prefixes are rejected")
    public void testResolveInvalid(String candidate) {
        assertThat(prefix.resolve(candidate).isValid()).isFalse();
    }

    private static Stream<String> symbolProvider() {
        return toStream("!$%^&-=+;',./{}\"<>?.");
    }
    private static Stream<String> invalidSymbolProvider() {
        return toStream("@#*()`_[]\\:");
    }
    private static Stream<String> toStream(CharSequence input) {
        return input.chars().mapToObj(i -> String.valueOf((char) i));
    }

}
