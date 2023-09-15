package com.tisawesomeness.minecord.util.dice;

import com.tisawesomeness.minecord.util.Mth;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import static com.tisawesomeness.minecord.testutil.assertion.CustomAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

public class DiceGroupTest {

    // 2^63-1 = 7*7*73*127*337*92737*649657 = (7*73*127*92737)*(7*337*649657) = 6,018,353,089 * 1,532,540,863
    private static final long LARGE_DICE = 6_018_353_089L;
    private static final long LARGE_FACES = 1_532_540_863L;

    // 2^63+1 = 3*3*3*19*43*5419*77158673929 = (3*3*3*19*43*5419)*77158673929 = 119,537,721 * 77,158,673,929
    private static final long OVER_LARGE_DICE = 119_537_721L;
    private static final long OVER_LARGE_FACES = 77_158_673_929L;

    private static final Duration TIMEOUT = Duration.ofMillis(250);

    @Test
    public void testFrom() {
        assertThat(DiceGroup.from(1, 6))
                .asRight()
                .extracting(DiceGroup::getNumberOfDice, DiceGroup::getNumberOfFaces)
                .containsExactly(1L, 6L);
    }
    @Test
    public void testNegativeDice() {
        assertThat(DiceGroup.from(-1, 6))
                .asRight()
                .extracting(DiceGroup::getNumberOfDice, DiceGroup::getNumberOfFaces)
                .containsExactly(-1L, 6L);
    }
    @Test
    public void testNegativeFaces() {
        assertThat(DiceGroup.from(1, -6))
                .asLeft()
                .isEqualTo(DiceError.FACES_NOT_POSITIVE);
    }
    @Test
    public void testZeroDice() {
        assertThat(DiceGroup.from(0, 6))
                .asLeft()
                .isEqualTo(DiceError.DICE_ZERO);
    }
    @Test
    public void testZeroFaces() {
        assertThat(DiceGroup.from(1, 0))
                .asLeft()
                .isEqualTo(DiceError.FACES_NOT_POSITIVE);
    }
    @Test
    public void testOverflow() {
        long dice = (1L << 32);
        long faces = (1L << 31);
        assertThat(DiceGroup.from(dice, faces))
                .asLeft()
                .isEqualTo(DiceError.MAX_TOO_HIGH);
    }
    @Test
    public void testNoOverflow() {
        assertThat(DiceGroup.from(LARGE_DICE, LARGE_FACES))
                .asRight()
                .extracting(DiceGroup::getNumberOfDice, DiceGroup::getNumberOfFaces)
                .containsExactly(LARGE_DICE, LARGE_FACES);
    }
    @Test
    public void testOverflowNegative() {
        assertThat(DiceGroup.from(-OVER_LARGE_DICE, OVER_LARGE_FACES))
                .asLeft()
                .isEqualTo(DiceError.MIN_TOO_LOW);
    }
    @Test
    public void testNoOverflowNegative() {
        long dice = -(1L << 32);
        long faces = (1L << 31);
        assertThat(DiceGroup.from(dice, faces))
                .asRight()
                .extracting(DiceGroup::getNumberOfDice, DiceGroup::getNumberOfFaces)
                .containsExactly(dice, faces);
    }
    @Test
    public void testOverflowMin() {
        assertThat(DiceGroup.from(Long.MIN_VALUE, 2))
                .asLeft()
                .isEqualTo(DiceError.MIN_TOO_LOW);
    }
    @Test
    public void testNoOverflowMin() {
        assertThat(DiceGroup.from(Long.MIN_VALUE, 1))
                .asRight()
                .extracting(DiceGroup::getNumberOfDice, DiceGroup::getNumberOfFaces)
                .containsExactly(Long.MIN_VALUE, 1L);
    }

    @ParameterizedTest
    @CsvSource({
            "d20, 1, 20",
            "d2, 1, 2",
            "-d20, -1, 20",
            "3d8, 3, 8",
            "5d7, 5, 7",
            "1d1, 1, 1",
            "999d999, 999, 999",
            "-1d4, -1, 4",
            "-17d24, -17, 24"
    })
    public void testParse(String input, long dice, long faces) {
        assertThat(DiceGroup.parse(input))
                .asRight()
                .extracting(DiceGroup::getNumberOfDice, DiceGroup::getNumberOfFaces)
                .containsExactly(dice, faces);
    }
    @ParameterizedTest
    @ValueSource(strings = {
            "abc", "123", ".", ""
    })
    public void testParseNoDelimiter(String input) {
        assertThat(DiceGroup.parse(input))
                .asLeft()
                .isEqualTo(DiceError.NO_DELIMITER);
    }
    @ParameterizedTest
    @ValueSource(strings = {
            "ad4", "&d6", "99999999999999999999999999d12"
    })
    public void testParseDiceInvalid(String input) {
        assertThat(DiceGroup.parse(input))
                .asLeft()
                .isEqualTo(DiceError.DICE_INVALID);
    }
    @ParameterizedTest
    @ValueSource(strings = {
            "4da", "6d&", "12d99999999999999999999999999", "-d-", "d"
    })
    public void testParseFacesInvalid(String input) {
        assertThat(DiceGroup.parse(input))
                .asLeft()
                .isEqualTo(DiceError.FACES_INVALID);
    }
    @ParameterizedTest
    @ValueSource(strings = {
            "4dd4", "abcdefg", "dd"
    })
    public void testEitherInvalid(String input) {
        assertThat(DiceGroup.parse(input))
                .asLeft()
                .isIn(DiceError.DICE_INVALID, DiceError.FACES_INVALID);
    }

    @Test
    public void testMin() {
        assertThat(DiceGroup.from(7, 4).getRight().getMin())
                .isEqualTo(7L);
    }
    @Test
    public void testMax() {
        assertThat(DiceGroup.from(7, 4).getRight().getMax())
                .isEqualTo(28L);
    }
    @Test
    public void testMinNegative() {
        assertThat(DiceGroup.from(-7, 4).getRight().getMin())
                .isEqualTo(-28L);
    }
    @Test
    public void testMaxNegative() {
        assertThat(DiceGroup.from(-7, 4).getRight().getMax())
                .isEqualTo(-7L);
    }

    @ParameterizedTest
    @CsvSource({
            "1, 6, 3.5",
            "4, 6, 14.0",
            "13, 1, 13.0",
            "-3, 6, -10.5",
            "5, 10, 27.5"
    })
    public void testMean(long dice, long faces, double expected) {
        assertThat(DiceGroup.from(dice, faces).getRight().getMean())
                .isCloseTo(expected, within(Mth.EPSILON));
    }
    @Test
    public void testVariance() {
        assertThat(DiceGroup.from(1, 6).getRight().getVariance())
                .isCloseTo(35.0 / 12.0, within(Mth.EPSILON));
    }
    @Test
    public void testVariance2() {
        assertThat(DiceGroup.from(2, 6).getRight().getVariance())
                .isCloseTo(35.0 / 6.0, within(Mth.EPSILON));
    }
    @Test
    public void testVariance3() {
        assertThat(DiceGroup.from(1, 7).getRight().getVariance())
                .isCloseTo(4.0, within(Mth.EPSILON));
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 3, Long.MAX_VALUE, -1, -3, Long.MIN_VALUE})
    public void testRollOneFace(long dice) {
        DiceGroup dg = DiceGroup.from(dice, 1).getRight();
        assertThat(Executors.newSingleThreadExecutor().submit(dg::roll))
                .succeedsWithin(TIMEOUT)
                .isEqualTo(dice);
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 3, Long.MAX_VALUE, -1, -3, Long.MIN_VALUE})
    public void testRollEachOneFace(long dice) {
        Map<Long, Long> expected = new HashMap<>();
        expected.put(1L, dice);
        DiceGroup dg = DiceGroup.from(dice, 1).getRight();
        assertThat(Executors.newSingleThreadExecutor().submit(dg::rollEach))
                .succeedsWithin(TIMEOUT)
                .isEqualTo(expected);
    }

    @Test
    public void testRollApprox() {
        DiceGroup dg = DiceGroup.from(Long.MAX_VALUE / 6, 6).getRight();
        assertThat(Executors.newSingleThreadExecutor().submit(dg::rollApprox))
                .succeedsWithin(TIMEOUT);
    }

    @Test
    public void testToString() {
        assertThat(DiceGroup.from(4, 6).getRight())
                .hasToString("4d6");
    }
    @Test
    public void testToStringSimple() {
        assertThat(DiceGroup.from(1, 6).getRight())
                .hasToString("d6");
    }

}
