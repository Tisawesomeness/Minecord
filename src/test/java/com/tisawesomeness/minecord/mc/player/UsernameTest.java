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
            MiscTestUtils.repeat(" ", 45) + "timmy" + MiscTestUtils.repeat(" ", 62)
            // yes this was real at one point, I am not kidding
    };

    @ParameterizedTest(name = "{index} ==> String \"{0}\" is a supported username")
    @MethodSource({"validNameProvider", "invalidNameProvider"})
    @DisplayName("Any ASCII username from 1 to max characters is supported")
    public void testFrom(String candidate) {
        assertThat(new Username(candidate).isSupportedByMojangAPI()).isTrue();
    }

    @ParameterizedTest(name = "{index} ==> String \"{0}\" is not a supported username")
    @ValueSource(strings = {
            "Sengångaren",
            "Séboutron",
            "kriſtjan144"
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

    @ParameterizedTest(name = "{index} ==> username `{0}` escapes to `{1}`")
    @CsvSource({
            "Tis_awesomeness, Tis_awesomeness",
            "jeb_, jeb_",
            "Will Wall, Will Wall",
            "ab\"cd, ab\\\"cd",
            "ab\\cd, ab\\\\cd",
            "hmm\", hmm\\\"",
            "hmm\\, hmm\\\\"
    })
    public void testEscape(CharSequence candidate, CharSequence expected) {
        assertThat(Username.escape(candidate)).isEqualTo(expected);
    }

    private static Stream<String> validNameProvider() {
        return Arrays.stream(validNames);
    }
    private static Stream<String> invalidNameProvider() {
        return Arrays.stream(invalidNames);
    }

}
