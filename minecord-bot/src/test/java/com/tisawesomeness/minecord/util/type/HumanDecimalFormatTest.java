package com.tisawesomeness.minecord.util.type;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.RoundingMode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class HumanDecimalFormatTest {

    @Test
    public void testMinFractionDigitsError() {
        assertThatThrownBy(() -> HumanDecimalFormat.builder().minimumFractionDigits(-1))
                .isInstanceOf(IllegalArgumentException.class);
    }
    @Test
    public void testMaxFractionDigitsError() {
        assertThatThrownBy(() -> HumanDecimalFormat.builder().maximumFractionDigits(-1))
                .isInstanceOf(IllegalArgumentException.class);
    }
    @Test
    public void testMinMaxFractionDigitsError() {
        assertThatThrownBy(() -> HumanDecimalFormat.builder()
                .minimumFractionDigits(3)
                .maximumFractionDigits(2)
                .build())
                .isInstanceOf(IllegalArgumentException.class);
    }
    @Test
    public void testMinMaxExponentError() {
        assertThatThrownBy(() -> HumanDecimalFormat.builder()
                .minimumExponentForExactValues(3)
                .maximumExponentForExactValues(2)
                .build())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @CsvSource({
            "123.456, '123.456'",
            "0.1, '0.1'",
            "2.0, '2'",
            "0.0, '0'"
    })
    public void testFormat(double input, String expected) {
        HumanDecimalFormat format = HumanDecimalFormat.builder().build();
        assertThat(format.format(input))
                .isEqualTo(expected);
    }
    @ParameterizedTest
    @CsvSource({
            "-10, 10, 0.000123, '0.000123'",
            "-3, 3, 0.000123, '1.23e-4'",
            "-4, 2, 0.000123, '0.000123'",
            "-10, 10, 123000.0, '123000'",
            "-4, 4, 123000.0, '1.23e5'",
            "-2, 5, 123000.0, '123000'",
            "0, 0, 0.0, '0'"
    })
    public void testFormatScientific(int minDigits, int maxDigits, double input, String expected) {
        HumanDecimalFormat format = HumanDecimalFormat.builder()
                .minimumExponentForExactValues(minDigits)
                .maximumExponentForExactValues(maxDigits)
                .build();
        assertThat(format.format(input))
                .isEqualTo(expected);
    }
    @ParameterizedTest
    @CsvSource({
            "0, 5, 123.4567, '123.4567'",
            "0, 3, 123.4567, '123.456'",
            "0, 1, 123.4567, '123.4'",
            "0, 0, 123.4567, '123'",
            "3, 5, 123.4567, '123.4567'",
            "5, 5, 123.4567, '123.45670'",
            "8, 10, 123.4567, '123.45670000'",
            "2, 10, 0.0, '0.00'"
    })
    public void testFormatFractionDigits(int minDigits, int maxDigits, double input, String expected) {
        HumanDecimalFormat format = HumanDecimalFormat.builder()
                .minimumFractionDigits(minDigits)
                .maximumFractionDigits(maxDigits)
                .build();
        assertThat(format.format(input))
                .isEqualTo(expected);
    }
    @ParameterizedTest
    @CsvSource({
            "1234567.0, '1,234,567'",
            "1234567.89012, '1,234,567.89012'"
    })
    public void testFormatGrouping(double input, String expected) {
        HumanDecimalFormat format = HumanDecimalFormat.builder()
                .includeGroupingCommas(true)
                .build();
        assertThat(format.format(input))
                .isEqualTo(expected);
    }
    @ParameterizedTest
    @CsvSource({
            "4, 12.3456, '12.3456'",
            "3, 12.3456, '~12.345'",
            "0, 12.3456, '~12'",
            "1, 0.00001, '~0'"
    })
    public void testFormatApprox(int minDigits, double input, String expected) {
        HumanDecimalFormat format = HumanDecimalFormat.builder()
                .maximumFractionDigits(minDigits)
                .includeApproximationSymbol(true)
                .build();
        assertThat(format.format(input))
                .isEqualTo(expected);
    }
    @ParameterizedTest
    @CsvSource({
            "DOWN, 1.5, '1'",
            "UP, 1.5, '2'",
            "HALF_DOWN, 1.5, '1'",
            "HALF_UP, 1.5, '2'",
            "HALF_EVEN, 1.5, '2'"
    })
    public void testFormatRoundingMode(RoundingMode roundingMode, double input, String expected) {
        HumanDecimalFormat format = HumanDecimalFormat.builder()
                .maximumFractionDigits(0)
                .roundingMode(roundingMode)
                .build();
        assertThat(format.format(input))
                .isEqualTo(expected);
    }

}
