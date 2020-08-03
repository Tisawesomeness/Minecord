package com.tisawesomeness.minecord.util.type;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class VerificationTest {

    @Test
    @DisplayName("Valid factory method returns valid Verification")
    public void testValid() {
        Verification v = Verification.valid();
        assertThat(v.isValid()).isTrue();
    }
    @Test
    @DisplayName("Invalid factory method returns invalid Verification, keeping error")
    public void testInvalid() {
        String errorMessage = "An error message";
        Verification v = Verification.invalid(errorMessage);
        assertThat(v.isValid()).isFalse();
        assertThat(v.getErrors()).containsExactly(errorMessage);
    }

    @Test
    @DisplayName("Valid Verification converts to valid Validation")
    public void testToValidationValid() {
        Verification ve = Verification.valid();
        Object o = new Object();
        Validation<Object> va = ve.toValidation(o);
        assertThat(va.isValid()).isTrue();
        assertThat(va.getValue()).isEqualTo(o);
    }
    @Test
    @DisplayName("Invalid Verification converts to invalid Validation")
    public void testToValidationInvalid() {
        String errorMessage = "An error message";
        Verification ve = Verification.invalid(errorMessage);
        Validation<Object> va = ve.toValidation(new Object());
        assertThat(va.isValid()).isFalse();
        assertThat(va.getErrors()).isEqualTo(ve.getErrors());
    }

    @Test
    @DisplayName("v1.combine(v2) with both valid is valid")
    public void testCombineBothValid() {
        Verification v1 = Verification.valid();
        Verification v2 = Verification.valid();
        Verification vCombined = v1.combine(v2);
        assertThat(vCombined.isValid()).isTrue();
    }
    @Test
    @DisplayName("v1.combine(v2) with left valid returns right error")
    public void testCombineLeftValid() {
        Verification v1 = Verification.valid();
        String errorMessage = "An error message";
        Verification v2 = Verification.invalid(errorMessage);
        Verification vCombined = v1.combine(v2);
        assertThat(vCombined.isValid()).isFalse();
        assertThat(vCombined.getErrors()).containsExactly(errorMessage);
    }
    @Test
    @DisplayName("v1.combine(v2) with right valid returns left error")
    public void testCombineRightValid() {
        String errorMessage = "An error message";
        Verification v1 = Verification.invalid(errorMessage);
        Verification v2 = Verification.valid();
        Verification vCombined = v1.combine(v2);
        assertThat(vCombined.isValid()).isFalse();
        assertThat(vCombined.getErrors()).containsExactly(errorMessage);
    }
    @Test
    @DisplayName("v1.combine(v2) with both invalid combines error messages")
    public void testCombineBothInvalid() {
        String errorMessage1 = "First error message";
        Verification v1 = Verification.invalid(errorMessage1);
        String errorMessage2 = "Second error message";
        Verification v2 = Verification.invalid(errorMessage2);
        Verification vCombined = v1.combine(v2);
        assertThat(vCombined.isValid()).isFalse();
        assertThat(vCombined.getErrors()).containsExactly(errorMessage1, errorMessage2);
    }
    @Test
    @DisplayName("Combining with varargs combines all error message")
    public void testCombineVarargs() {
        String errorMessage1 = "First error message";
        Verification v1 = Verification.invalid(errorMessage1);
        String errorMessage2 = "Second error message";
        Verification v2 = Verification.invalid(errorMessage2);
        String errorMessage3 = "Third error message";
        Verification v3 = Verification.invalid(errorMessage3);
        Verification vCombined = Verification.combineAll(v1, v2, v3);
        assertThat(vCombined.isValid()).isFalse();
        assertThat(vCombined.getErrors()).containsExactly(errorMessage1, errorMessage2, errorMessage3);
    }

}
