package com.tisawesomeness.minecord.util.dice;

import com.tisawesomeness.minecord.util.Lists;
import com.tisawesomeness.minecord.util.Mth;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigInteger;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.Executors;

import static com.tisawesomeness.minecord.testutil.assertion.CustomAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.atIndex;
import static org.assertj.core.api.Assertions.within;

public class DiceCombinationTest {

    private static final Duration TIMEOUT = Duration.ofMillis(250);

    @Test
    public void testFrom() {
        List<DiceGroup> dice = Lists.of(DiceGroup.from(3, 8).getRight());
        assertThat(DiceCombination.from(dice, 2))
                .asRight()
                .extracting(DiceCombination::getDice, DiceCombination::getConstant)
                .containsExactly(dice, 2L);
    }
    @Test
    public void testFromMultiple() {
        List<DiceGroup> dice = Lists.of(
                DiceGroup.from(3, 8).getRight(),
                DiceGroup.from(8, 4).getRight(),
                DiceGroup.from(20, 20).getRight()
        );
        assertThat(DiceCombination.from(dice, -2))
                .asRight()
                .extracting(DiceCombination::getDice, DiceCombination::getConstant)
                .containsExactly(dice, -2L);
    }
    @Test
    public void testFromNone() {
        List<DiceGroup> dice = Collections.emptyList();
        assertThat(DiceCombination.from(dice))
                .asRight()
                .extracting(DiceCombination::getDice, DiceCombination::getConstant)
                .containsExactly(dice, 0L);
    }
    @Test
    public void testFromNoOverflow() {
        List<DiceGroup> dice = Lists.of(
                DiceGroup.from(Long.MAX_VALUE - 1, 1).getRight(),
                DiceGroup.from(1, 1).getRight()
        );
        assertThat(DiceCombination.from(dice))
                .asRight()
                .extracting(DiceCombination::getDice, DiceCombination::getConstant)
                .containsExactly(dice, 0L);
    }
    @Test
    public void testFromOverflow() {
        List<DiceGroup> dice = Lists.of(
                DiceGroup.from(Long.MAX_VALUE, 1).getRight(),
                DiceGroup.from(1, 1).getRight()
        );
        assertThat(DiceCombination.from(dice))
                .asLeft()
                .extracting(DiceCombination.Error::getType)
                .isEqualTo(DiceCombination.ErrorType.MAX_TOO_HIGH);
    }
    @Test
    public void testFromOverflowNegative() {
        List<DiceGroup> dice = Lists.of(
                DiceGroup.from(Long.MIN_VALUE, 1).getRight(),
                DiceGroup.from(-1, 1).getRight()
        );
        assertThat(DiceCombination.from(dice))
                .asLeft()
                .extracting(DiceCombination.Error::getType)
                .isEqualTo(DiceCombination.ErrorType.MIN_TOO_LOW);
    }
    @Test
    public void testFromOverflowConstant() {
        List<DiceGroup> dice = Lists.of(DiceGroup.from((1L << 62) - 1L, 2).getRight());
        assertThat(DiceCombination.from(dice, 2))
                .asLeft()
                .extracting(DiceCombination.Error::getType)
                .isEqualTo(DiceCombination.ErrorType.MAX_TOO_HIGH);
    }
    @Test
    public void testFromOverflowConstantNegative() {
        List<DiceGroup> dice = Lists.of(DiceGroup.from(-(1L << 62), 2).getRight());
        assertThat(DiceCombination.from(dice, -1))
                .asLeft()
                .extracting(DiceCombination.Error::getType)
                .isEqualTo(DiceCombination.ErrorType.MIN_TOO_LOW);
    }

    @Test
    public void testParse() {
        List<DiceGroup> expected = Lists.of(DiceGroup.from(3, 6).getRight());
        assertThat(DiceCombination.parse("3d6+5"))
                .asRight()
                .extracting(DiceCombination::getDice, DiceCombination::getConstant)
                .containsExactly(expected, 5L);
    }
    @Test
    public void testParse2() {
        List<DiceGroup> expected = Lists.of(DiceGroup.from(1, 6).getRight());
        assertThat(DiceCombination.parse("d6"))
                .asRight()
                .extracting(DiceCombination::getDice, DiceCombination::getConstant)
                .containsExactly(expected, 0L);
    }
    @ParameterizedTest
    @ValueSource(strings = {"-d2", "-1d2", "-01d2"})
    public void testParse3(String input) {
        List<DiceGroup> expected = Lists.of(DiceGroup.from(-1, 2).getRight());
        assertThat(DiceCombination.parse(input))
                .asRight()
                .extracting(DiceCombination::getDice, DiceCombination::getConstant)
                .containsExactly(expected, 0L);
    }
    @ParameterizedTest
    @ValueSource(strings = {"3d6-5", "-5+3d6"})
    public void testParse4(String input) {
        List<DiceGroup> expected = Lists.of(DiceGroup.from(3, 6).getRight());
        assertThat(DiceCombination.parse(input))
                .asRight()
                .extracting(DiceCombination::getDice, DiceCombination::getConstant)
                .containsExactly(expected, -5L);
    }
    @Test
    public void testParse5() {
        List<DiceGroup> expected = Lists.of(
                DiceGroup.from(1, 4).getRight(),
                DiceGroup.from(1, 6).getRight()
        );
        assertThat(DiceCombination.parse("d4+d6"))
                .asRight()
                .extracting(DiceCombination::getDice, DiceCombination::getConstant)
                .containsExactly(expected, 0L);
    }
    @Test
    public void testParse6() {
        List<DiceGroup> expected = Lists.of(
                DiceGroup.from(1, 20).getRight(),
                DiceGroup.from(-2, 6).getRight()
        );
        assertThat(DiceCombination.parse("d20-2d6-1"))
                .asRight()
                .extracting(DiceCombination::getDice, DiceCombination::getConstant)
                .containsExactly(expected, -1L);
    }
    @ParameterizedTest
    @ValueSource(longs = {-5, 0, 5})
    public void testParse7(long input) {
        List<DiceGroup> expected = Collections.emptyList();
        assertThat(DiceCombination.parse(String.valueOf(input)))
                .asRight()
                .extracting(DiceCombination::getDice, DiceCombination::getConstant)
                .containsExactly(expected, input);
    }
    @ParameterizedTest
    @ValueSource(strings = {"2+8", "11-1", "2+2-9+15", "-15+25"})
    public void testParse8(String input) {
        List<DiceGroup> expected = Collections.emptyList();
        assertThat(DiceCombination.parse(String.valueOf(input)))
                .asRight()
                .extracting(DiceCombination::getDice, DiceCombination::getConstant)
                .containsExactly(expected, 10L);
    }
    @Test
    public void testParse9() {
        String input = Long.MAX_VALUE + "+1-1";
        List<DiceGroup> expected = Collections.emptyList();
        assertThat(DiceCombination.parse(input))
                .asRight()
                .extracting(DiceCombination::getDice, DiceCombination::getConstant)
                .containsExactly(expected, Long.MAX_VALUE);
    }
    @Test
    public void testParseEmpty() {
        List<DiceGroup> expected = Collections.emptyList();
        assertThat(DiceCombination.parse(""))
                .asRight()
                .extracting(DiceCombination::getDice, DiceCombination::getConstant)
                .containsExactly(expected, 0L);
    }
    @ParameterizedTest
    @ValueSource(strings = {
            "x", "-x", "+x",
            "4d6+j", "4d6-j",
            "4d6++", "4d6-+", "4d6+-", "4d6--",
            "++4d6", "+-4d6", "-+4d6", "--4d6",
            "4++2d6", "2++2", "2--2", "4--2d6",
            "2+", "2-", "-2+", "-2-",
            "+", "-"
    })
    public void testParseInvalidFail() {
        assertThat(DiceCombination.parse("x"))
                .asLeft()
                .extracting(DiceCombination.Error::getType)
                .isEqualTo(DiceCombination.ErrorType.PARSE_FAILED);
    }
    @ParameterizedTest
    @CsvSource({
            "ad4, ad4, DICE_INVALID",
            "ad4+5, ad4, DICE_INVALID",
            "2d6+0d2-2, 0d2, DICE_ZERO"
    })
    public void testParseInvalidDice(String input, String expectedDiceStr, DiceError diceError) {
        assertThat(DiceCombination.parse(input))
                .asLeft()
                .extracting(DiceCombination.Error::getType, DiceCombination.Error::getFailedDiceString, DiceCombination.Error::getDiceError)
                .containsExactly(DiceCombination.ErrorType.DICE_ERROR, expectedDiceStr, diceError);
    }
    @ParameterizedTest
    @CsvSource({
            "d, d, ?",
            "5d6+4dd2+d20, 4dd2, ?"
    })
    public void testParseInvalidDice(String input, String expectedDiceStr) {
        assertThat(DiceCombination.parse(input))
                .asLeft()
                .extracting(DiceCombination.Error::getType, DiceCombination.Error::getFailedDiceString, DiceCombination.Error::getDiceError)
                .contains(DiceCombination.ErrorType.DICE_ERROR, atIndex(0))
                .contains(expectedDiceStr, atIndex(1))
                .satisfies(diceError -> {
                    assertThat(diceError).isIn(DiceError.DICE_INVALID, DiceError.FACES_INVALID);
                }, atIndex(2));
    }

    @ParameterizedTest
    @CsvSource({
            "d6, 1, 1, 6",
            "d6+d4, 2, 2, 10",
            "d6-d6, 2, -5, 5",
            "d20-2d6-1, 3, -12, 17",
            "2, 0, 2, 2",
            "0, 0, 0, 0"
    })
    public void testProperties(String input, BigInteger numDice, long min, long max) {
        assertThat(DiceCombination.parse(input).getRight())
                .extracting(DiceCombination::getNumberOfDice, DiceCombination::getMin, DiceCombination::getMax)
                .containsExactly(numDice, min, max);
    }
    @ParameterizedTest
    @CsvSource({
            "1d6, 3.5",
            "4d6, 14.0",
            "2, 2.0",
            "0, 0.0",
            "1d6-2, 1.5",
            "d10+d20-d6, 12.5",
            "d10-d10, 0.0"
    })
    public void testMean(String input, double expected) {
        assertThat(DiceCombination.parse(input).getRight().getMean())
                .isCloseTo(expected, within(Mth.EPSILON));
    }
    @Test
    public void testVariance() {
        double expected = 35.0 / 12.0;
        assertThat(DiceCombination.parse("1d6").getRight().getVariance())
                .isCloseTo(expected, within(Mth.EPSILON));
    }
    @Test
    public void testVariance2() {
        double expected = 35.0 / 12.0;
        assertThat(DiceCombination.parse("1d6-2").getRight().getVariance())
                .isCloseTo(expected, within(Mth.EPSILON));
    }
    @Test
    public void testVariance3() {
        double expected = 299.0 / 4.0;
        assertThat(DiceCombination.parse("d10+2d20").getRight().getVariance())
                .isCloseTo(expected, within(Mth.EPSILON));
    }
    @Test
    public void testVariance4() {
        double expected = 35.0 / 6.0;
        assertThat(DiceCombination.parse("d6-d6").getRight().getVariance())
                .isCloseTo(expected, within(Mth.EPSILON));
    }
    @Test
    public void testVariance5() {
        assertThat(DiceCombination.parse("2").getRight().getVariance())
                .isCloseTo(0.0, within(Mth.EPSILON));
    }
    @Test
    public void testVariance6() {
        assertThat(DiceCombination.parse("0").getRight().getVariance())
                .isCloseTo(0.0, within(Mth.EPSILON));
    }

    @Test
    public void testRollNone() {
        DiceCombination dc = DiceCombination.parse("").getRight();
        assertThat(Executors.newSingleThreadExecutor().submit(dc::roll))
                .succeedsWithin(TIMEOUT)
                .isEqualTo(0L);
    }
    @ParameterizedTest
    @ValueSource(longs = {1, 3, Long.MAX_VALUE, -1, -3, Long.MIN_VALUE})
    public void testRollOneFace(long dice) {
        String input = dice + "d1";
        DiceCombination dc = DiceCombination.parse(input).getRight();
        assertThat(Executors.newSingleThreadExecutor().submit(dc::roll))
                .succeedsWithin(TIMEOUT)
                .isEqualTo(dice);
    }
    @Test
    public void testRollMultiple() {
        DiceCombination dc = DiceCombination.parse("2d1+5d1-4d1+2").getRight();
        assertThat(Executors.newSingleThreadExecutor().submit(dc::roll))
                .succeedsWithin(TIMEOUT)
                .isEqualTo(5L);
    }

    @Test
    public void testRollEachNone() {
        List<Map<Long, Long>> expected = new ArrayList<>();
        DiceCombination dc = DiceCombination.parse("").getRight();
        assertThat(Executors.newSingleThreadExecutor().submit(dc::rollEach))
                .succeedsWithin(TIMEOUT)
                .isEqualTo(expected);
    }
    @ParameterizedTest
    @ValueSource(longs = {1, 3, Long.MAX_VALUE, -1, -3, Long.MIN_VALUE})
    public void testRollEachOneFace(long dice) {
        String input = dice + "d1";

        List<Map<Long, Long>> expected = new ArrayList<>();
        Map<Long, Long> frequencies = new HashMap<>();
        frequencies.put(1L, dice);
        expected.add(frequencies);

        DiceCombination dc = DiceCombination.parse(input).getRight();
        assertThat(Executors.newSingleThreadExecutor().submit(dc::rollEach))
                .succeedsWithin(TIMEOUT)
                .isEqualTo(expected);
    }
    @Test
    public void testRollEachMultiple() {
        List<Map<Long, Long>> expected = new ArrayList<>();

        Map<Long, Long> expected1 = new HashMap<>();
        expected1.put(1L, 2L);
        expected.add(expected1);

        Map<Long, Long> expected2 = new HashMap<>();
        expected2.put(1L, 5L);
        expected.add(expected2);

        Map<Long, Long> expected3 = new HashMap<>();
        expected3.put(1L, -4L);
        expected.add(expected3);

        DiceCombination dc = DiceCombination.parse("2d1+5d1-4d1+2").getRight();
        assertThat(Executors.newSingleThreadExecutor().submit(dc::rollEach))
                .succeedsWithin(TIMEOUT)
                .isEqualTo(expected);
    }

    @Test
    public void testRollApprox() {
        long numDice = Long.MAX_VALUE / 12;
        String input = String.format("%dd6-%dd4", numDice, numDice);
        DiceCombination dc = DiceCombination.parse(input).getRight();
        assertThat(Executors.newSingleThreadExecutor().submit(() -> dc.rollApprox()))
                .succeedsWithin(TIMEOUT);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "4d6",
            "d6",
            "-1d2",
            "2d20+5",
            "d4+d5+d6",
            "d20-2d6-1",
            "5",
            "-5",
            "0"
    })
    public void testToString(String input) {
        assertThat(DiceCombination.parse(input))
                .asRight()
                .hasToString(input);
    }

}
