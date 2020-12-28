package com.tisawesomeness.minecord.mc.player;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
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
            " ".repeat(45) + "timmy" + " ".repeat(62) // yes this was real at one point, I am not kidding
    };

    @ParameterizedTest(name = "{index} ==> String \"{0}\" is a supported username")
    @MethodSource({"validNameProvider", "invalidNameProvider"})
    @DisplayName("Creating a username is possible from any 1-max length ASCII string, even if the name is invalid")
    public void testFrom(String candidate) {
        assertThat(Username.from(candidate))
                .isPresent()
                .get().hasToString(candidate);
    }

    @ParameterizedTest(name = "{index} ==> String \"{0}\" is not a supported username")
    @ValueSource(strings = {
            "Sengångaren",
            "Séboutron",
            "kriſtjan144"
    })
    @EmptySource
    @DisplayName("Creating a username from non-ascii and empty strings return empty")
    public void testFromUnsupported(String candidate) {
        assertThat(Username.from(candidate)).isEmpty();
    }

    @Test
    @DisplayName("Only usernames up to the max length can be created")
    public void testMaxLength() {
        String maxLengthName = "A".repeat(Username.MAX_LENGTH);
        assertThat(Username.from(maxLengthName))
                .isPresent()
                .get().hasToString(maxLengthName);
        String overMaxLengthName = maxLengthName + "A";
        assertThat(Username.from(overMaxLengthName)).isEmpty();
    }

    @ParameterizedTest(name = "{index} ==> \"{0}\" is a valid username")
    @MethodSource("validNameProvider")
    @DisplayName("isValid() is true for valid usernames")
    public void testValidNames(String candidate) {
        Username name = Username.fromAny(candidate);
        assertThat(name.isValid()).isTrue();
    }

    @ParameterizedTest(name = "{index} ==> \"{0}\" is an invalid username")
    @MethodSource("invalidNameProvider")
    @DisplayName("isValid() is false for invalid usernames")
    public void testInvalidNames(String candidate) {
        Username name = Username.fromAny(candidate);
        assertThat(name.isValid()).isFalse();
    }

    @Test
    @DisplayName("Ascii only names are supported")
    public void testSupportedName() {
        Username name = Username.fromAny("ascii-only");
        assertThat(name.isSupportedByMojangAPI()).isTrue();
    }

    @Test
    @DisplayName("Non-ascii names are unsupported")
    public void testUnsupportedName() {
        Username name = Username.fromAny("ooθoo");
        assertThat(name.isSupportedByMojangAPI()).isFalse();
    }

    @Test
    @DisplayName("Empty names are unsupported")
    public void testEmptyName() {
        Username name = Username.fromAny("");
        assertThat(name.isSupportedByMojangAPI()).isFalse();
    }

    private static Stream<String> validNameProvider() {
        return Arrays.stream(validNames);
    }
    private static Stream<String> invalidNameProvider() {
        return Arrays.stream(invalidNames);
    }

}
