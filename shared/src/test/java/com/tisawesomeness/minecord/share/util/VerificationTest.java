package com.tisawesomeness.minecord.share.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;

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
    @DisplayName("Invalid factory method returns invalid Verification with two errors")
    public void testInvalid2() {
        String error1 = "error1";
        String error2 = "error2";
        Verification v = Verification.invalid(error1, error2);
        assertThat(v.isValid()).isFalse();
        assertThat(v.getErrors()).containsExactly(error1, error2);
    }
    @Test
    @DisplayName("Invalid factory method returns invalid Verification with three errors")
    public void testInvalid3() {
        String error1 = "error1";
        String error2 = "error2";
        String error3 = "error3";
        Verification v = Verification.invalid(error1, error2, error3);
        assertThat(v.isValid()).isFalse();
        assertThat(v.getErrors()).containsExactly(error1, error2, error3);
    }
    @Test
    @DisplayName("Invalid factory method returns invalid Verification from list with two errors")
    public void testInvalidList2() {
        Collection<String> list = Arrays.asList("error1", "error2");
        Verification v = Verification.invalid(list);
        assertThat(v.isValid()).isFalse();
        assertThat(v.getErrors()).isEqualTo(list);
    }
    @Test
    @DisplayName("Invalid factory method returns invalid Verification from list with three errors")
    public void testInvalidList3() {
        Collection<String> list = Arrays.asList("error1", "error2", "error3");
        Verification v = Verification.invalid(list);
        assertThat(v.isValid()).isFalse();
        assertThat(v.getErrors()).isEqualTo(list);
    }

    @Test
    @DisplayName("Verify factory method returns valid Verification when condition is true")
    public void testVerifyValid() {
        String errorMessage = "An error message";
        Verification v = Verification.verify(true, errorMessage);
        assertThat(v.isValid()).isTrue();
    }
    @Test
    @DisplayName("Verify factory method returns invalid Verification when condition is false")
    public void testVerifyInvalid() {
        String errorMessage = "An error message";
        Verification v = Verification.verify(false, errorMessage);
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
    @DisplayName("Combining with varargs combines valids")
    public void testCombineAllValid() {
        Verification v = Verification.valid();
        Verification vCombined = Verification.combineAll(v, v, v);
        assertThat(vCombined.isValid()).isTrue();
    }
    @Test
    @DisplayName("Combining with varargs combines valids and invalids the same as combining normally")
    public void testCombineAllMixed() {
        Verification v1 = Verification.valid();
        String errorMessage = "error message";
        Verification v2 = Verification.invalid(errorMessage);
        Verification vCombined = Verification.combineAll(v1, v2);
        assertThat(vCombined.isValid()).isFalse();
        assertThat(vCombined.getErrors()).containsExactly(errorMessage);
    }
    @Test
    @DisplayName("Combining with varargs combines all error message")
    public void testCombineAllInvalid() {
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

    @Test
    @DisplayName("A valid Verification is equal to itself")
    public void testEqualsReflexiveValid() {
        Verification v = Verification.valid();
        assertThat(v).isEqualTo(v);
    }
    @Test
    @DisplayName("An invalid Verification is equal to itself")
    public void testEqualsReflexiveInvalid() {
        Verification v = Verification.invalid("err");
        assertThat(v).isEqualTo(v);
    }
    @Test
    @DisplayName("Two Verifications with the same error message are symmetrically equal")
    public void testEqualsSymmetric() {
        Verification v1 = Verification.invalid("err");
        Verification v2 = Verification.invalid("err");
        assertThat(v1).isEqualTo(v2).hasSameHashCodeAs(v2);
        assertThat(v2).isEqualTo(v1);
    }
    @Test
    @DisplayName("Two Verifications with the same error messages are symmetrically equal")
    public void testEqualsSymmetric2() {
        String err1 = "err1";
        String err2 = "err2";
        Verification v1 = Verification.invalid(err1, err2);
        Verification v2 = Verification.invalid(err1, err2);
        assertThat(v1).isEqualTo(v2).hasSameHashCodeAs(v2);
        assertThat(v2).isEqualTo(v1);
    }
    @Test
    @DisplayName("A valid Verification is never equal to an invalid Verification")
    public void testValidNotEqualsInvalid() {
        Verification valid = Verification.valid();
        Verification invalid = Verification.invalid("err");
        assertThat(valid).isNotEqualTo(invalid);
    }
    @Test
    @DisplayName("Two Verifications with different error messages are not equal")
    public void testNotEqualsInvalid() {
        Verification v1 = Verification.invalid("err");
        Verification v2 = Verification.invalid("ERR");
        assertThat(v1).isNotEqualTo(v2);
    }
    @Test
    @DisplayName("Two Verifications with differently ordered error messages are not equal")
    public void testNotEqualsOrdered() {
        String err1 = "err1";
        String err2 = "err2";
        Verification v1 = Verification.invalid(err1, err2);
        Verification v2 = Verification.invalid(err2, err1);
        assertThat(v1).isNotEqualTo(v2);
    }

}
