package com.tisawesomeness.minecord.util.dice;

import com.tisawesomeness.minecord.util.MathUtils;
import com.tisawesomeness.minecord.util.type.Either;
import lombok.*;

import javax.annotation.Nullable;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Represents a collection of dice which may have different face counts, plus/minus a final constant. Dice combinations
 * support common dice notation "2d20+5" as well as multiple dice "d20-2d6-1".
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class DiceCombination {

    private final List<DiceGroup> dice;
    /**
     * The number added to the final dice roll.
     */
    @Getter private final long constant;

    /**
     * @return an unmodifiable list of the dice in this combination
     */
    public List<DiceGroup> getDice() {
        return Collections.unmodifiableList(dice);
    }

    /**
     * Creates a dice combination from the list of dice.
     * <br>This method can return the following {@link ErrorType ErrorTypes}:
     * <ul>
     *     <li>{@link ErrorType#MIN_TOO_LOW MIN_TOO_LOW}
     *     <br>If the total minimum value of the dice underflows a long integer.
     *     </li>
     *     <li>{@link ErrorType#MAX_TOO_HIGH MAX_TOO_HIGH}
     *     <br>If the total maximum value of the dice overflows a long integer.
     *     </li>
     * </ul>
     * @param dice list of dice to use in this combination
     * @return the dice group, or a {@link Error} if the inputs are invalid
     */
    public static Either<Error, DiceCombination> from(List<? extends DiceGroup> dice) {
        return from(dice, 0);
    }
    /**
     * Creates a dice combination from the list of dice, plus/minus some constant.
     * <br>This method can return the following {@link ErrorType ErrorTypes}:
     * <ul>
     *     <li>{@link ErrorType#MIN_TOO_LOW MIN_TOO_LOW}
     *     <br>If the total minimum value of the dice underflows a long integer.
     *     </li>
     *     <li>{@link ErrorType#MAX_TOO_HIGH MAX_TOO_HIGH}
     *     <br>If the total maximum value of the dice overflows a long integer.
     *     </li>
     * </ul>
     * @param dice list of dice to use in this combination
     * @param constant number added to final roll
     * @return the dice group, or a {@link Error} if the inputs are invalid
     */
    public static Either<Error, DiceCombination> from(List<? extends DiceGroup> dice, long constant) {
        long max = constant;
        long min = constant;
        for (DiceGroup dg : dice) {
            long dgMin = dg.getMin();
            if (MathUtils.additionOverflows(min, dgMin)) {
                // If min overflows MAX_VALUE, then the max also overflows
                ErrorType type = min > 0 ? ErrorType.MAX_TOO_HIGH : ErrorType.MIN_TOO_LOW;
                return Either.left(new Error(type));
            }
            min += dgMin;
            long dgMax = dg.getMax();
            if (MathUtils.additionOverflows(max, dgMax)) {
                // Min overflow checked earlier, must be a max overflow
                return Either.left(new Error(ErrorType.MAX_TOO_HIGH));
            }
            max += dgMax;
        }
        return Either.right(new DiceCombination(new ArrayList<>(dice), constant));
    }

    /**
     * Parses a string in dice notation format (such as "d6" or "3d20+d6-2") into a dice combination. Zero or more
     * dice in {@link DiceGroup#parse(String)} format may be specified, along with one or more constants that will
     * be added or subtracted from the final dice roll.
     * <br>This method can return the following {@link ErrorType ErrorTypes}:
     * <ul>
     *     <li>{@link ErrorType#DICE_ERROR DICE_ERROR}
     *     <br>If one of the dice could not be parsed. {@link Error#getDiceError()} and
     *     {@link Error#getFailedDiceString()} have more information on what caused the error.
     *     <li>{@link ErrorType#PARSE_FAILED PARSE_FAILED}
     *     <br>If the whole expression could not be parsed.
     *     </li>
     *     <li>Any ErrorType returned from {@link #from(List, long)}.
     *     </li>
     * </ul>
     * @param str the dice notation string
     * @return the dice group, or a {@link Error} if the inputs are invalid
     */
    public static Either<Error, DiceCombination> parse(@NonNull String str) {
        List<DiceGroup> dice = new ArrayList<>();
        BigInteger constant = BigInteger.ZERO; // using BigInteger so Long.MAX_VALUE + 1 - 1 doesn't overflow

        // Tokens are either dice (when token contains "d"), or a constant number
        // Tokens are separated by either + or - for add or subtract
        // +/- are returned as tokens since whether the dice/constant are negative is important
        StringTokenizer st = new StringTokenizer(str, "+-", true);
        boolean negative = false;
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if ("+".equals(token)) {
                negative = false;
            } else if ("-".equals(token)) {
                negative = true;
            } else if (token.toLowerCase(Locale.ROOT).contains("d")) {
                String diceStr = negative ? "-" + token : token;
                Either<DiceError, DiceGroup> maybeDiceGroup = DiceGroup.parse(diceStr);
                if (maybeDiceGroup.isLeft()) {
                    return Either.left(new Error(ErrorType.DICE_ERROR, maybeDiceGroup.getLeft(), diceStr));
                }
                dice.add(maybeDiceGroup.getRight());
            } else {
                String constantStr = negative ? "-" + token : token;
                OptionalLong maybeConstant = MathUtils.safeParseLong(constantStr);
                if (!maybeConstant.isPresent()) {
                    return Either.left(new Error(ErrorType.PARSE_FAILED));
                }
                constant = constant.add(BigInteger.valueOf(maybeConstant.getAsLong()));
            }
        }

        // Although -20d1 + Long.MAX_VALUE + 1 won't overflow, the constant is not allowed to overflow
        if (constant.compareTo(MathUtils.LONG_MIN_VALUE) < 0) {
            return Either.left(new Error(ErrorType.MIN_TOO_LOW));
        }
        if (constant.compareTo(MathUtils.LONG_MAX_VALUE) > 0) {
            return Either.left(new Error(ErrorType.MAX_TOO_HIGH));
        }

        return from(dice, constant.longValue());
    }

    /**
     * Gets the total number of dice rolled by this DiceCombination. Negative dice still add to the total.
     * @return number of dice rolled
     */
    public BigInteger getNumberOfDice() {
        return dice.stream()
                .map(DiceGroup::getNumberOfDice)
                .map(BigInteger::valueOf)
                .map(BigInteger::abs) // must abs after converting to BigInteger since abs(Long.MIN_VALUE) < 0 (!!)
                .reduce(BigInteger::add)
                .orElse(BigInteger.ZERO);
    }

    /**
     * @return the minimum possible value this dice combination could roll
     */
    public long getMin() {
        return dice.stream()
                .mapToLong(DiceGroup::getMin)
                .sum() + constant;
    }
    /**
     * @return the maximum possible value this dice combination could roll
     */
    public long getMax() {
        return dice.stream()
                .mapToLong(DiceGroup::getMax)
                .sum() + constant;
    }
    /**
     * @return the average value of the dice
     */
    public double getMean() {
        return dice.stream()
                .mapToDouble(DiceGroup::getMean)
                .sum() + constant;
    }
    /**
     * @return the variance (standard deviation squared) of the dice
     */
    public double getVariance() {
        return dice.stream()
                .mapToDouble(DiceGroup::getVariance)
                .sum();
    }

    /**
     * Rolls all dice and sums the result, adding the constant.
     * <br><strong>WARNING: This method rolls each die individually for 100% accuracy. Rolling very large numbers of
     * dice may take minutes, hours, or even centuries.</strong> Consider using {@link #rollApprox()}.
     * <br>If all dice have one face, the method will complete immediately.
     * @return the total of all rolls
     * @see #rollEach()
     */
    public long roll() {
        return rollApprox(dice -> false);
    }

    /**
     * Rolls all dice, counting the frequency that each value appears in each dice group.
     * <br><strong>WARNING: This method rolls each die individually for 100% accuracy. Rolling very large numbers of
     * dice may take minutes, hours, or even centuries.</strong> Consider using {@link #rollApprox()}.
     * <br>If the dice have one face, the method will complete immediately.
     * @return a list with the same length and order as {@link #getDice()}, each item specifying the frequency that
     * each value appears (see {@link DiceGroup#rollEach()} for details)
     * @see #roll()
     */
    public List<Map<Long, Long>> rollEach() {
        return dice.stream()
                .map(DiceGroup::rollEach)
                .collect(Collectors.toList());
    }

    /**
     * Rolls all dice and sums the result, adding the constant. See {@link DiceGroup#rollApprox()} for approximation
     * details.
     * @return the total of all rolls
     * @see #rollEach()
     */
    public long rollApprox() {
        return MathUtils.clamp(Math.round(MathUtils.randomGaussian(getMean(), Math.sqrt(getVariance()))), getMin(), getMax());
    }
    /**
     * Rolls all dice and sums the result, adding the constant. The {@code shouldApproximate} predicate can be used to
     * approximate rolls for very large numbers of dice while using exact values for small numbers of dice.
     * @param shouldApproximate function that inputs each dice group in this combination, and outputs whether that
     *                          group's roll should be approximated using {@link DiceGroup#rollApprox()}
     * @return the total of all rolls
     * @see #rollEach()
     */
    public long rollApprox(Predicate<? super DiceGroup> shouldApproximate) {
        return dice.stream()
                .mapToLong(dice -> shouldApproximate.test(dice) ? dice.rollApprox() : dice.roll())
                .sum() + constant;
    }

    /**
     * Converts the dice combination to dice notation, such as "4d6+5".
     * Combinations with zero dice will return the constant number, even if it's 0.
     * The returned string will create the same dice combination when passed to {@link #parse(String)}.
     * @return the dice combination in dice notation
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < dice.size(); i++) {
            DiceGroup dg = dice.get(i);
            if (i > 0 && dg.getNumberOfDice() > 0) {
                sb.append('+');
            }
            sb.append(dg.toString());
        }
        if (constant != 0 || dice.isEmpty()) {
            if (!dice.isEmpty() && constant > 0) {
                sb.append('+');
            }
            sb.append(constant);
        }
        return sb.toString();
    }

    /**
     * Error class communicating what went wrong during {@link #parse(String)}.
     */
    @RequiredArgsConstructor
    @EqualsAndHashCode
    public static class Error {
        @Getter private final ErrorType type;
        private final @Nullable DiceError diceError;
        private final @Nullable String failedDiceString;

        private Error(ErrorType type) {
            this(type, null, null);
        }

        /**
         * @return if {@link #getDiceError()} will return a valid dice error
         */
        public boolean isDiceError() {
            return type == ErrorType.DICE_ERROR;
        }
        /**
         * @return the dice error for this error
         * @throws IllegalStateException if {@link #isDiceError()} is false
         */
        public DiceError getDiceError() {
            if (diceError == null) {
                throw new IllegalStateException("This error is not a DICE_ERROR");
            }
            return diceError;
        }
        /**
         * @return the dice group string that failed to parse
         * @throws IllegalStateException if {@link #isDiceError()} is false
         */
        public String getFailedDiceString() {
            if (failedDiceString == null) {
                throw new IllegalStateException("This error is not a DICE_ERROR");
            }
            return failedDiceString;
        }

        @Override
        public String toString() {
            StringJoiner sj = new StringJoiner(", ", Error.class.getSimpleName() + "[", "]")
                    .add("type=" + type);
            if (diceError != null) {
                sj.add("diceError=" + diceError).add("failedDiceString='" + failedDiceString + "'");
            }
            return sj.toString();
        }
    }

    public enum ErrorType {
        DICE_ERROR,
        PARSE_FAILED,
        MIN_TOO_LOW,
        MAX_TOO_HIGH
    }

}
