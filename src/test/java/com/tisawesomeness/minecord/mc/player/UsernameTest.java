package com.tisawesomeness.minecord.mc.player;

import com.tisawesomeness.minecord.testutil.MiscTestUtils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class UsernameTest {

    private static final String[] validNames = {
            "Tis_awesomeness",
            "DJElectro",
            "lordjbs",
            "Electromaster_",
            "jeb_",
            "NoWeDont"
    };
    private static final String[] invalidNames = {
            "8",
            "$",
            "crazybitingturtle", // 17 chars
            "michael.selvaggio",
            "Cool.J",
            "Awesomeness!!!",
            "Din-ex",
            "sample@email.com", // this specific username isn't real but email usernames do exist
            "Will Wall",
//            MiscTestUtils.repeat(" ", 45) + "timmy" + MiscTestUtils.repeat(" ", 62)
            // yes this was real at one point, I am not kidding
            // disabled since mojang api seems to limit names to 25 chars
    };

    @ParameterizedTest(name = "{index} ==> String \"{0}\" is a supported username")
    @MethodSource({"validNameProvider", "invalidNameProvider"})
    @DisplayName("Any ASCII username from 1 to max characters is supported")
    public void testFrom(String candidate) {
        assertThat(new Username(candidate).isSupportedByMojangAPI())
                .withFailMessage("Expected %s to be supported by Mojang API but was not", candidate)
                .isTrue();
    }

    @ParameterizedTest(name = "{index} ==> String \"{0}\" is not a supported username")
    @ValueSource(strings = {
            "Sengångaren",
            "Séboutron",
            "kriſtjan144",
            // shoutouts to HxLiquid for these
            "' OR 1=1",
            "http://a/%%30%30",
            "https://cdn.discordapp.com/"
    })
    @EmptySource
    @DisplayName("Usernames from non-ascii and empty strings are not supported by Mojang")
    public void testFromUnsupported(String candidate) {
        assertThat(new Username(candidate).isSupportedByMojangAPI()).isFalse();
    }

    @Test
    @DisplayName("Only usernames up to the max length are supported")
    public void testMaxLength() {
        String maxLengthName = MiscTestUtils.repeat("A", Username.MAX_LENGTH);
        assertThat(new Username(maxLengthName).isSupportedByMojangAPI()).isTrue();
        String overMaxLengthName = maxLengthName + "A";
        assertThat(new Username(overMaxLengthName).isSupportedByMojangAPI()).isFalse();
    }

    @ParameterizedTest(name = "{index} ==> \"{0}\" is a valid username")
    @MethodSource("validNameProvider")
    @DisplayName("isValid() is true for valid usernames")
    public void testValidNames(String candidate) {
        Username name = new Username(candidate);
        assertThat(name.isValid()).isTrue();
    }

    @ParameterizedTest(name = "{index} ==> \"{0}\" is an invalid username")
    @MethodSource("invalidNameProvider")
    @DisplayName("isValid() is false for invalid usernames")
    public void testInvalidNames(String candidate) {
        Username name = new Username(candidate);
        assertThat(name.isValid()).isFalse();
    }

    @ParameterizedTest(name = "{index} ==> <{0}> is parsed to <{1}>")
    @CsvSource({
            "name, name",
            "\"tis\", tis",
            "`tis`, tis",
            "\"tis, \"tis",
            "tis\", tis\"",
            "`tis, `tis",
            "tis`, tis`",
            "\"tis`, \"tis`",
            "`tis\", `tis\"",
            "12\\\\34, 12\\\\34",
            "ab\\\\\\cd, ab\\\\\\cd",
            "ij`kl, ij`kl",
            "\"ij\"kl\", ij",
            "\"ij`kl\", ij`kl",
            "\"ij\\`kl\", ij\\`kl",
            "`ij`kl`, ij"
    })
    @DisplayName("Username parsing works with quoted names and escaped characters")
    public void testParse(String candidate, String expected) {
        String parsedName = Username.parse(candidate).toString();
        assertThat(parsedName)
                .withFailMessage("Expecting string <%s> to be parsed to <%s> but was instead <%s>.",
                        candidate, expected, parsedName)
                .isEqualTo(expected);
    }

    @ParameterizedTest(name = "{index} ==> <{0}> is parsed to itself")
    @ValueSource(strings = {
            "Will Wall",
            "alex 99",
            "amjacobs ",
            " toon",
            " hmmm "
    })
    @DisplayName("Username parsing works with names with spaces in them")
    public void testParseSpaces(String candidate) {
        String parsedName = Username.parse(candidate).toString();
        assertThat(parsedName)
                .withFailMessage("Expecting string <%s> to be parsed to itself but was instead <%s>.",
                        candidate, parsedName)
                .isEqualTo(candidate);
    }

    private static Stream<String> validNameProvider() {
        return Arrays.stream(validNames);
    }
    private static Stream<String> invalidNameProvider() {
        return Arrays.stream(invalidNames);
    }

}
