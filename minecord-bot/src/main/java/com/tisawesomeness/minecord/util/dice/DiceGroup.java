package com.tisawesomeness.minecord.util.dice;

import com.tisawesomeness.minecord.common.util.Either;
import com.tisawesomeness.minecord.util.Mth;
import lombok.*;
import org.checkerframework.checker.index.qual.Positive;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.OptionalLong;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Represents a group of fair dice, all of which have the same number of faces.
 * Negative numbers of dice are supported and will negate the rolled values.
 */
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class DiceGroup {

    private final long numberOfDice; // n
    @Positive
    private final long numberOfFaces; // s

    /**
     * Creates a dice group from the given inputs.
     * <br>This method can return the following {@link DiceError DiceErrors}:
     * <ul>
     *     <li>{@link DiceError#DICE_ZERO DICE_ZERO}
     *     <br>If the number of dice is zero.
     *     </li>
     *     <li>{@link DiceError#FACES_NOT_POSITIVE FACES_NOT_POSITIVE}
     *     <br>If the number of faces is not positive.
     *     </li>
     *     <li>{@link DiceError#MAX_TOO_HIGH MAX_TOO_HIGH}
     *     <br>If the maximum rolled value ({@code numberOfDice * numberOfFaces}) would overflow a long integer.
     *     </li>
     *     <li>{@link DiceError#MIN_TOO_LOW MIN_TOO_LOW}
     *     <br>If the maximum rolled value ({@code numberOfDice * numberOfFaces}) would underflow a long integer.
     *     </li>
     * </ul>
     * @param numberOfDice the number of dice
     * @param numberOfFaces the number of sides on each die
     * @return the dice group, or a {@link DiceError} if the inputs are invalid
     */
    public static Either<DiceError, DiceGroup> from(long numberOfDice, long numberOfFaces) {
        if (numberOfDice == 0) {
            return Either.left(DiceError.DICE_ZERO);
        }
        if (numberOfFaces <= 0) {
            return Either.left(DiceError.FACES_NOT_POSITIVE);
        }
        if (Mth.multiplicationOverflows(numberOfDice, numberOfFaces)) {
            return Either.left(numberOfDice > 0 ? DiceError.MAX_TOO_HIGH : DiceError.MIN_TOO_LOW);
        }
        return Either.right(new DiceGroup(numberOfDice, numberOfFaces));
    }

    /**
     * Parses a string in dice notation format (such as "d6" or "3d20") into a dice group. The format is the number
     * of dice, then 'd', then the number of faces.
     * <br>Negative values for the number of dice are allowed, but not zero.
     * <br>Whitespaces is <strong>not</strong> ignored.
     * <br>This method can return the following {@link DiceError DiceErrors}:
     * <ul>
     *     <li>{@link DiceError#NO_DELIMITER NO_DELIMITER}
     *     <br>If the 'd' character is not found in the lowercased string.
     *     </li>
     *     <li>{@link DiceError#DICE_INVALID DICE_INVALID} or {@link DiceError#FACES_INVALID FACES_INVALID}
     *     <br>If the dice or faces could not be parsed as a number, respectively.
     *     </li>
     *     <li>Any DiceError returned from {@link #from(long, long)}.
     *     </li>
     * </ul>
     * @param str the dice notation string
     * @return the dice group, or a {@link DiceError} if the string is invalid
     */
    public static Either<DiceError, DiceGroup> parse(@NonNull String str) {
        int dIdx = str.toLowerCase(Locale.ROOT).indexOf('d');
        if (dIdx == -1) {
            return Either.left(DiceError.NO_DELIMITER);
        }

        String diceStr = str.substring(0, dIdx);
        OptionalLong diceOpt = parseDice(diceStr);
        if (!diceOpt.isPresent()) {
            return Either.left(DiceError.DICE_INVALID);
        }

        String facesStr = str.substring(dIdx + 1);
        OptionalLong facesOpt = Mth.safeParseLong(facesStr);
        if (!facesOpt.isPresent()) {
            return Either.left(DiceError.FACES_INVALID);
        }

        return from(diceOpt.getAsLong(), facesOpt.getAsLong());
    }
    private static OptionalLong parseDice(String diceStr) {
        if (diceStr.isEmpty()) {
            return OptionalLong.of(1);
        }
        if ("-".equals(diceStr)) {
            return OptionalLong.of(-1);
        }
        return Mth.safeParseLong(diceStr);
    }

    /**
     * @return the minimum possible value this dice group could roll
     */
    public long getMin() {
        return numberOfDice > 0 ? numberOfDice : numberOfDice * numberOfFaces;
    }
    /**
     * @return the maximum possible value this dice group could roll
     */
    public long getMax() {
        return numberOfDice > 0 ? numberOfDice * numberOfFaces : numberOfDice;
    }
    /**
     * @return the average value of the dice
     */
    public double getMean() {
        // n(s + 1) / 2
        return numberOfDice * (numberOfFaces + 1.0) / 2.0;
    }
    /**
     * @return the variance (standard deviation squared) of the dice
     */
    public double getVariance() {
        // n(s^2 - 1) / 12, must convert to double early to avoid overflow
        return Math.abs((double) numberOfDice) * (((double) numberOfFaces) * numberOfFaces - 1.0) / 12.0;
    }

    /**
     * Rolls each die in the group and sums the result. If the number of dice is negative, the result will be negative.
     * <br><strong>WARNING: This method rolls each die individually for 100% accuracy. Rolling very large numbers of
     * dice may take minutes, hours, or even centuries.</strong> Consider using {@link #rollApprox()}.
     * <br>If the dice have one face, the method will complete immediately.
     * @return the total of all rolls
     * @see #rollEach()
     */
    public long roll() {
        if (numberOfFaces == 1) {
            return numberOfDice;
        }
        long total = 0;
        for (long i = 0; i < Math.abs(numberOfDice); i++) {
            // Roll from 0..s-1 instead of usual 1..s
            total += ThreadLocalRandom.current().nextLong(numberOfFaces);
        }
        // Adding number of dice (n) changes rolls from n(0..s-1) to n(1..s)
        // If number of dice is negative, need to negate total sum
        return (numberOfDice > 0 ? total : -total) + numberOfDice;
    }

    /**
     * Rolls each die in the group, counting the frequency that each value appears.
     * <br><strong>WARNING: This method rolls each die individually for 100% accuracy. Rolling very large numbers of
     * dice may take minutes, hours, or even centuries.</strong> Consider using {@link #rollApprox()}.
     * <br>If the dice have one face, the method will complete immediately.
     * @return a map with each die value as the key and the number of times it appears as the value
     * @see #roll()
     */
    public Map<Long, Long> rollEach() {
        Map<Long, Long> rollsToCounts = new HashMap<>();
        if (numberOfFaces == 1) {
            rollsToCounts.put(1L, numberOfDice);
            return rollsToCounts;
        }
        for (long i = 0; i < Math.abs(numberOfDice); i++) {
            long roll = ThreadLocalRandom.current().nextLong(numberOfFaces) + 1;
            long negatedRoll = numberOfDice > 0 ? roll : -roll;
            long currentCount = rollsToCounts.getOrDefault(negatedRoll, 0L);
            rollsToCounts.put(negatedRoll, currentCount + 1);
        }
        return rollsToCounts;
    }

    /**
     * Calculates an approximation for {@link #roll()} using a Gaussian distribution with the parameters
     * {@link #getMean()} and {@link #getVariance()}. The result is clamped to within {@link #getMin()} and
     * {@link #getMax()}.
     * <br>For sufficiently large numbers of dice, the approximation is highly accurate. Otherwise, {@link #roll()}
     * is recommended.
     * <br>If dice have one face, this method returns the exact value.
     * <br>This method always returns in constant time.
     * @return the total of all rolls, approximated
     */
    public long rollApprox() {
        if (numberOfFaces == 1) {
            return numberOfFaces;
        }
        return Mth.clamp(Math.round(Mth.randomGaussian(getMean(), Math.sqrt(getVariance()))), getMin(), getMax());
    }

    /**
     * Converts the dice group to dice notation, such as "4d6". If the number of dice is 1, then it is omitted from
     * the string. The returned string will create the same dice group when passed to {@link #parse(String)}.
     * @return the dice group in dice notation
     */
    @Override
    public String toString() {
        if (numberOfDice == 1) {
            return "d" + numberOfFaces;
        }
        return numberOfDice + "d" + numberOfFaces;
    }

}
