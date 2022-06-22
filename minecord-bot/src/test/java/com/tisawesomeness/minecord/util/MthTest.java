package com.tisawesomeness.minecord.util;

import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.multiset.HashMultiSet;
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
        assertThatThrownBy(() -> Mth.clamp(2, 3, 1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @DisplayName("casting without sign extension is equivalent to java casting for positive numbers")
    @ValueSource(ints = {0, 1, 16, Integer.MAX_VALUE})
    public void testCWSE(int candidate) {
        // isEqualTo() casts to long
        assertThat(Mth.castWithoutSignExtension(candidate)).isEqualTo(candidate);
    }
    @Test
    @DisplayName("casting without sign extension does not sign extend negative numbers")
    public void testCWSENegative() {
        int input = 1 << (Integer.SIZE - 1);
        long expected = 1L << (Integer.SIZE - 1);
        assertThat(Mth.castWithoutSignExtension(input)).isEqualTo(expected);
    }
    @Test
    @DisplayName("casting without sign extension does not sign extend negative numbers")
    public void testCWSENegative2() {
        int input = 0xFFFF_FFFF;
        long expected = 0x0000_0000_FFFF_FFFFL;
        assertThat(Mth.castWithoutSignExtension(input)).isEqualTo(expected);
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

    @Test
    @DisplayName("Weighted random throws IllegalArgumentException on an empty multiset")
    public void testWeightedRandomEmpty() {
        assertThatThrownBy(() -> Mth.weightedRandom(new HashMultiSet<>()))
                .isInstanceOf(IllegalArgumentException.class);
    }
    @Test
    @DisplayName("Weighted random on one unique item selects that item")
    public void testWeightedRandomSingle() {
        String str = "test";
        MultiSet<String> ms = new HashMultiSet<>();
        ms.add(str);
        assertThat(Mth.weightedRandom(ms)).isEqualTo(str);
    }
    @Test
    @DisplayName("Weighted random index throws IllegalArgumentException on an empty array")
    public void testWeightedRandomIndexEmpty() {
        assertThatThrownBy(() -> Mth.weightedRandomIndex(new int[0]))
                .isInstanceOf(IllegalArgumentException.class);
    }
    @Test
    @DisplayName("Weighted random index on one unique item selects that item")
    public void testWeightedRandomIndexSingle() {
        int[] arr = {1};
        assertThat(Mth.weightedRandomIndex(arr)).isEqualTo(0);
    }
    @Test
    @DisplayName("Weighted random unique throws IllegalArgumentException on an empty multiset")
    public void testWeightedRandomUniqueEmpty() {
        assertThatThrownBy(() -> Mth.weightedRandomUnique(new HashMultiSet<>(), null))
                .isInstanceOf(IllegalArgumentException.class);
    }
    @Test
    @DisplayName("Weighted random unique on one unique item selects that item")
    public void testWeightedRandomUniqueSingle() {
        String str = "test";
        MultiSet<String> ms = new HashMultiSet<>();
        ms.add(str);
        assertThat(Mth.weightedRandomUnique(ms, null)).isEqualTo(str);
    }
    @Test
    @DisplayName("Weighted random unique ignoring the only unique item throws IllegalArgumentException")
    public void testWeightedRandomUniqueIgnored() {
        String str = "test";
        MultiSet<String> ms = new HashMultiSet<>();
        ms.add(str);
        assertThatThrownBy(() -> Mth.weightedRandomUnique(ms, str))
                .isInstanceOf(IllegalArgumentException.class);
    }
    @Test
    @DisplayName("Weighted random unique on two items, one ignored, selects the other")
    public void testWeightedRandomUniqueDouble() {
        String str1 = "test1";
        String str2 = "test2";
        MultiSet<String> ms = new HashMultiSet<>();
        ms.add(str1);
        ms.add(str2);
        assertThat(Mth.weightedRandomUnique(ms, str1)).isEqualTo(str2);
        assertThat(Mth.weightedRandomUnique(ms, str2)).isEqualTo(str1);
    }
    @Test
    @DisplayName("Weighted random unique index throws IllegalArgumentException on an empty array")
    public void testWeightedRandomUniqueIndexEmpty() {
        assertThatThrownBy(() -> Mth.weightedRandomUniqueIndex(new int[0], -1))
                .isInstanceOf(IllegalArgumentException.class);
    }
    @Test
    @DisplayName("Weighted random unique on one unique item selects that item")
    public void testWeightedRandomUniqueIndexSingle() {
        int[] arr = {1};
        assertThat(Mth.weightedRandomUniqueIndex(arr, -1)).isEqualTo(0);
    }
    @Test
    @DisplayName("Weighted random unique index ignoring the only nonzero weight throws IllegalArgumentException")
    public void testWeightedRandomUniqueIndexIgnored() {
        int[] arr = {1};
        assertThatThrownBy(() -> Mth.weightedRandomUniqueIndex(arr, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }
    @Test
    @DisplayName("Weighted random unique index on two items, one ignored, selects the other")
    public void testWeightedRandomUniqueIndexDouble() {
        int[] arr = {1, 1};
        assertThat(Mth.weightedRandomUniqueIndex(arr, 0)).isEqualTo(1);
        assertThat(Mth.weightedRandomUniqueIndex(arr, 1)).isEqualTo(0);
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
