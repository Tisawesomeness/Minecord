package com.tisawesomeness.minecord.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MthTest {

    @Test
    @DisplayName("Clamping an int between the bounds returns the same int")
    public void testClampNormal() {
        assertThat(Mth.clamp(2, 1, 3)).isEqualTo(2);
    }
    @Test
    @DisplayName("Clamping an int under low bound returns the low bound")
    public void testClampLow() {
        assertThat(Mth.clamp(0, 1, 3)).isEqualTo(1);
    }
    @Test
    @DisplayName("Clamping an int over high bound returns the high bound")
    public void testClampHigh() {
        assertThat(Mth.clamp(4, 1, 3)).isEqualTo(3);
    }
    @Test
    @DisplayName("IllegalArgumentException is thrown if the low bound is greater than the high bound")
    public void testClampBad() {
        assertThatThrownBy(() -> Mth.clamp(2, 3, 1)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("sha1 hashes as expected")
    public void testSha1() {
        assertThat(Mth.sha1("test")).isEqualTo("a94a8fe5ccb19ba61c4c0873d391e987982fbbd3");
    }
    @Test
    @DisplayName("sha1 accepts an empty string")
    public void testSha1Empty() {
        assertThat(Mth.sha1("")).isEqualTo("da39a3ee5e6b4b0d3255bfef95601890afd80709");
    }

    @ParameterizedTest(name = "{index} ==> Int {0} is safely parsed")
    @DisplayName("Integers are safely parsed")
    @ValueSource(ints = {
            Integer.MIN_VALUE,
            -25565,
            -1,
            0,
            1,
            25565,
            Integer.MAX_VALUE
    })
    public void testSafeParseIntValid(int candidate) {
        assertThat(Mth.safeParseInt(String.valueOf(candidate))).hasValue(candidate);
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
            "12.5",
            "12,5",
            "2147483648", // over int limit
            "-2147483649",
            "9223372036854775808", // over long limit
            "-9223372036854775809"
    })
    @NullAndEmptySource
    public void testSafeParseIntInvalid(String candidate) {
        assertThat(Mth.safeParseInt(candidate)).isEmpty();
    }

    @ParameterizedTest(name = "{index} ==> Long {0} is safely parsed")
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
    public void testSafeParseIntValid(long candidate) {
        assertThat(Mth.safeParseLong(String.valueOf(candidate))).hasValue(candidate);
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
            "123-456",
            "12.5",
            "12,5",
            "9223372036854775808", // over long limit
            "-9223372036854775809"
    })
    @NullAndEmptySource
    public void testSafeParseLongInvalid(String candidate) {
        assertThat(Mth.safeParseLong(candidate)).isEmpty();
    }

}