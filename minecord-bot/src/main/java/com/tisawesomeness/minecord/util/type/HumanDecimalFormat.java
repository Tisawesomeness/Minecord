package com.tisawesomeness.minecord.util.type;

import com.tisawesomeness.minecord.util.Mth;
import lombok.Builder;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Formats decimal numbers in a human-readable format.
 */
@Builder
public class HumanDecimalFormat {

    /**
     * Minimum number of digits allowed in the fractional portion of a number.
     * <br>Default 0, must be >= 0 and <= {@code maximumFractionDigits}.
     */
    @Builder.Default
    private final int minimumFractionDigits = 0;
    /**
     * Maximum number of digits allowed in the fractional portion of a number.
     * <br>Default {@link Integer#MAX_VALUE MAX_VALUE}, must be >= 0 and >= {@code minimumFractionDigits}.
     */
    @Builder.Default
    private final int maximumFractionDigits = Integer.MAX_VALUE;
    /**
     * If the number is between {@code 10^minimumExponentForExactValues} and {@code 10^maximumExponentForExactValues},
     * then the number is formatted exactly. Otherwise, the number is formatted in scientific notation.
     * <br>Default {@link Integer#MIN_VALUE MIN_VALUE}, must be <= {@code maximumExponentForExactValues}.
     */
    @Builder.Default
    private final int minimumExponentForExactValues = Integer.MIN_VALUE;
    /**
     * If the number is between {@code 10^minimumExponentForExactValues} and {@code 10^maximumExponentForExactValues},
     * then the number is formatted exactly. Otherwise, the number is formatted in scientific notation.
     * <br>Default {@link Integer#MAX_VALUE MAX_VALUE}, must be >= {@code minimumExponentForExactValues}.
     */
    @Builder.Default
    private final int maximumExponentForExactValues = Integer.MAX_VALUE;
    /**
     * Whether to include grouping separators, such as in "1,234,567.89". Grouping separators are not used in
     * scientific notation.
     * <br>Default false.
     */
    @Builder.Default
    private final boolean includeGroupingCommas = false;
    /**
     * Whether to prefix the number with '~' when the formatter rounds the number before formatting. This ONLY accounts
     * for rounding due to the maximum fractional digits, not roundoff error.
     * <br>Default false.
     */
    @Builder.Default
    private final boolean includeApproximationSymbol = false;
    /**
     * The rounding behavior of this formatter. Note that the rounding modes are designed around rounding to integers,
     * and may not work as expected when rounding to a specific number of fractional digits.
     * <br>Default {@link RoundingMode#DOWN}.
     */
    @Builder.Default
    private final RoundingMode roundingMode = RoundingMode.DOWN;

    // Overrides generated bulider() from @Builder
    // Only way to check arguments before building is to return a subclass of the generated builder
    public static HumanDecimalFormatBuilder builder() {
        return new HumanDecimalFormatBuilderInternal();
    }

    private static class HumanDecimalFormatBuilderInternal extends HumanDecimalFormatBuilder {

        @Override
        public HumanDecimalFormatBuilder minimumFractionDigits(int minimumFractionDigits) {
            if (minimumFractionDigits < 0) {
                throw new IllegalArgumentException("minimumFractionDigits cannot be negative");
            }
            return super.minimumFractionDigits(minimumFractionDigits);
        }

        @Override
        public HumanDecimalFormatBuilder maximumFractionDigits(int maximumFractionDigits) {
            if (maximumFractionDigits < 0) {
                throw new IllegalArgumentException("maximumFractionDigits cannot be negative");
            }
            return super.maximumFractionDigits(maximumFractionDigits);
        }

        @Override
        public HumanDecimalFormat build() {
            HumanDecimalFormat format = super.build();
            if (format.minimumFractionDigits > format.maximumFractionDigits) {
                throw new IllegalArgumentException(String.format(
                        "minimumFractionDigits %d cannot be greater than maximumFractionDigits %d",
                        format.minimumFractionDigits, format.maximumFractionDigits
                ));
            }
            if (format.minimumExponentForExactValues > format.maximumExponentForExactValues) {
                throw new IllegalArgumentException(String.format(
                        "minimumExponentForExactValues %d cannot be greater than maximumExponentForExactValues %d",
                        format.minimumExponentForExactValues, format.maximumExponentForExactValues
                ));
            }
            return format;
        }

    }

    /**
     * Formats a number according to this formatter.
     * See the builder methods for a description of what each argument does.
     * @param d input number
     * @return formatted string
     */
    public String format(double d) {
        int exponent = Mth.mantissa(d);
        if (minimumExponentForExactValues <= exponent && exponent <= maximumExponentForExactValues) {
            NumberFormat decimalFormat = createFormat(createPattern(false));
            return addApproxSymbolIfNotExact(decimalFormat, d);
        }
        NumberFormat scientificNotationFormat = createFormat(createPattern(true));
        return addApproxSymbolIfNotExact(scientificNotationFormat, d).toLowerCase(Locale.ROOT);
    }
    private String createPattern(boolean scientificNotation) {
        String integralPart = includeGroupingCommas ? "#,##0" : "0";
        String mantissaPart = scientificNotation ? "E0" : "";
        return String.format("%s.0%s", integralPart, mantissaPart);
    }
    private NumberFormat createFormat(String pattern) {
        NumberFormat format = new DecimalFormat(pattern);
        format.setMinimumFractionDigits(minimumFractionDigits);
        format.setMaximumFractionDigits(maximumFractionDigits);
        format.setRoundingMode(roundingMode);
        return format;
    }
    // Modifies format
    private String addApproxSymbolIfNotExact(NumberFormat format, double d) {
        String original = format.format(d);
        if (!includeApproximationSymbol || format.getMaximumFractionDigits() > -Double.MIN_EXPONENT) {
            return original;
        }
        format.setMaximumFractionDigits(-Double.MIN_EXPONENT);
        String withAllDigits = format.format(d);
        return original.equals(withAllDigits) ? original : "~" + original;
    }

}
