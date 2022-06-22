package com.tisawesomeness.minecord.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class ValidationTest {

    @Test
    @DisplayName("Validation factory method does not alter value")
    public void testValidObject() {
        Object o = new Object();
        Validation<Object> v = Validation.valid(o);
        assertThat(v.isValid()).isTrue();
        assertThat(v.getValue()).isEqualTo(o);
    }
    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = "test")
    @DisplayName("Validation factory method does not alter error message")
    public void testInvalid(String candidate) {
        Validation<Object> v = Validation.invalid(candidate);
        assertThat(v.isValid()).isFalse();
        assertThat(v.getErrors()).containsExactly(candidate);
    }
    @Test
    @DisplayName("Invalid factory method returns invalid Validation with two errors")
    public void testInvalid2() {
        String error1 = "error1";
        String error2 = "error2";
        Validation<Object> v = Validation.invalid(error1, error2);
        assertThat(v.isValid()).isFalse();
        assertThat(v.getErrors()).containsExactly(error1, error2);
    }
    @Test
    @DisplayName("Invalid factory method returns invalid Validation with three errors")
    public void testInvalid3() {
        String error1 = "error1";
        String error2 = "error2";
        String error3 = "error3";
        Validation<Object> v = Validation.invalid(error1, error2, error3);
        assertThat(v.isValid()).isFalse();
        assertThat(v.getErrors()).containsExactly(error1, error2, error3);
    }
    @Test
    @DisplayName("Invalid factory method returns invalid Validation from list with two errors")
    public void testInvalidList2() {
        Collection<String> list = Arrays.asList("error1", "error2");
        Validation<Object> v = Validation.invalid(list);
        assertThat(v.isValid()).isFalse();
        assertThat(v.getErrors()).isEqualTo(list);
    }
    @Test
    @DisplayName("Invalid factory method returns invalid Validation from list with three errors")
    public void testInvalidList3() {
        Collection<String> list = Arrays.asList("error1", "error2", "error3");
        Validation<Object> v = Validation.invalid(list);
        assertThat(v.isValid()).isFalse();
        assertThat(v.getErrors()).isEqualTo(list);
    }

    @Test
    @DisplayName("Validation from non-empty Optional returns that value")
    public void testFromOptionalValid() {
        Object o = new Object();
        Optional<Object> opt = Optional.of(o);
        Validation<Object> v = Validation.fromOptional(opt, "An error message");
        assertThat(v.isValid()).isTrue();
        assertThat(v.getValue()).isEqualTo(o);
    }
    @Test
    @DisplayName("Validation from empty Optional returns the error message")
    public void testFromOptionalInvalid() {
        String err = "An error message";
        Optional<Object> opt = Optional.empty();
        Validation<Object> v = Validation.fromOptional(opt, err);
        assertThat(v.isValid()).isFalse();
        assertThat(v.getErrors()).containsExactly(err);
    }

    @Test
    @DisplayName("Validation from valid Verification throws IllegalArgumentException")
    public void testFromInvalidVerificationValid() {
        Verification v = Verification.valid();
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> Validation.fromInvalidVerification(v));
    }
    @Test
    @DisplayName("Validation from invalid Verification keeps error message")
    public void testFromInvalidVerificationInvalid() {
        String err = "An error message";
        Verification ve = Verification.invalid(err);
        Validation<?> va = Validation.fromInvalidVerification(ve);
        assertThat(va.isValid()).isFalse();
        assertThat(va.getErrors()).isEqualTo(ve.getErrors());
    }

    @Test
    @DisplayName("getValue() throws IllegalStateException if not valid")
    public void testGetValue() {
        Validation<?> v = Validation.invalid("An error message");
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(v::getValue);
    }
    @Test
    @DisplayName("getErrorMessage() is empty if valid")
    public void testGetErrorMessage() {
        Validation<Object> v = Validation.valid(new Object());
        assertThat(v.getErrors()).isEmpty();
    }

    @Test
    @DisplayName("asVerification() converts to a valid verification")
    public void testAsVerificationValid() {
        Validation<Object> v = Validation.valid(new Object());
        assertThat(v.asVerification().isValid()).isTrue();
    }
    @Test
    @DisplayName("asVerification() converts to an invalid verification")
    public void testAsVerificationInvalid() {
        String err = "An error message";
        Validation<?> v = Validation.invalid(err);
        assertThat(v.asVerification().getErrors()).containsExactly(err);
    }

    @Test
    @DisplayName("propogateError() keeps the error message")
    public void testPropogateError() {
        String errorMessage = "An error message";
        Validation<?> v = Validation.invalid(errorMessage);
        assertThat(v.getErrors()).containsExactly(errorMessage);
    }
    @Test
    @DisplayName("propogateError() throws for a valid Validation")
    public void testPropogateErrorValid() {
        Validation<Object> v = Validation.valid(new Object());
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> Validation.propogateError(v));
    }

    @Test
    @DisplayName("v1.combine(v2) with both valid keeps value")
    public void testCombineBothValid() {
        Object o = new Object();
        Validation<Object> v1 = Validation.valid(o);
        Validation<Object> v2 = Validation.valid(o);
        Validation<Object> vCombined = v1.combine(v2);
        assertThat(vCombined.isValid()).isTrue();
        assertThat(vCombined.getValue()).isEqualTo(o);
    }
    @Test
    @DisplayName("v1.combine(v2) with both valid, but different, keeps right value")
    public void testCombineBothValidRightPreference() {
        Object o1 = new Object();
        Validation<Object> v1 = Validation.valid(o1);
        Object o2 = new Object();
        Validation<Object> v2 = Validation.valid(o2);
        Validation<Object> vCombined = v1.combine(v2);
        assertThat(vCombined.isValid()).isTrue();
        assertThat(vCombined.getValue()).isEqualTo(o2);
    }
    @Test
    @DisplayName("v1.combine(v2) with left valid returns right error")
    public void testCombineLeftValid() {
        Object o = new Object();
        Validation<Object> v1 = Validation.valid(o);
        String errorMessage = "An error message";
        Validation<Object> v2 = Validation.invalid(errorMessage);
        Validation<Object> vCombined = v1.combine(v2);
        assertThat(vCombined.isValid()).isFalse();
        assertThat(vCombined.getErrors()).containsExactly(errorMessage);
    }
    @Test
    @DisplayName("v1.combine(v2) with right valid returns left error")
    public void testCombineRightValid() {
        String errorMessage = "An error message";
        Validation<Object> v1 = Validation.invalid(errorMessage);
        Object o = new Object();
        Validation<Object> v2 = Validation.valid(o);
        Validation<Object> vCombined = v1.combine(v2);
        assertThat(vCombined.isValid()).isFalse();
        assertThat(vCombined.getErrors()).containsExactly(errorMessage);
    }
    @Test
    @DisplayName("v1.combine(v2) with both invalid combines error messages")
    public void testCombineBothInvalid() {
        String errorMessage1 = "First error message";
        Validation<Object> v1 = Validation.invalid(errorMessage1);
        String errorMessage2 = "Second error message";
        Validation<Object> v2 = Validation.invalid(errorMessage2);
        Validation<Object> vCombined = v1.combine(v2);
        assertThat(vCombined.isValid()).isFalse();
        assertThat(vCombined.getErrors()).containsExactly(errorMessage1, errorMessage2);
    }
    @Test
    @DisplayName("Combining with varargs combines valids")
    public void testCombineAllValid() {
        Validation<Integer> v1 = Validation.valid(1);
        Validation<Integer> v2 = Validation.valid(2);
        Validation<Integer> v3 = Validation.valid(3);
        Validation<Integer> vCombined = Validation.combineAll(v1, v2, v3);
        assertThat(vCombined.isValid()).isTrue();
        assertThat(vCombined.getValue()).isEqualTo(3);
    }
    @Test
    @DisplayName("Combining with varargs combines valids and invalids the same as combining normally")
    public void testCombineAllMixed() {
        Validation<Integer> v1 = Validation.valid(1);
        String errorMessage = "error message";
        Validation<Integer> v2 = Validation.invalid(errorMessage);
        Validation<Integer> vCombined = Validation.combineAll(v1, v2);
        assertThat(vCombined.isValid()).isFalse();
        assertThat(vCombined.getErrors()).containsExactly(errorMessage);
    }
    @Test
    @DisplayName("Combining with varargs combines all error message")
    public void testCombineAllInvalid() {
        String errorMessage1 = "First error message";
        Validation<Object> v1 = Validation.invalid(errorMessage1);
        String errorMessage2 = "Second error message";
        Validation<Object> v2 = Validation.invalid(errorMessage2);
        String errorMessage3 = "Third error message";
        Validation<Object> v3 = Validation.invalid(errorMessage3);
        Validation<Object> vCombined = Validation.combineAll(v1, v2, v3);
        assertThat(vCombined.isValid()).isFalse();
        assertThat(vCombined.getErrors()).containsExactly(errorMessage1, errorMessage2, errorMessage3);
    }

    @Test
    @DisplayName("Calling map() on a valid validation applies the mapper")
    public void testMapValid() {
        Function<Integer, Integer> mapper = x -> x * 2;
        int i = 2;
        Validation<Integer> validation = Validation.valid(i);
        Validation<Integer> mappedValidation = validation.map(mapper);
        assertThat(mappedValidation.isValid()).isTrue();
        assertThat(mappedValidation.getValue()).isEqualTo(mapper.apply(i));
    }
    @Test
    @DisplayName("Calling map() on an invalid validation keeps the error message")
    public void testMapInvalid() {
        Function<Integer, Integer> mapper = x -> x * 2;
        String errorMessage = "An error message";
        Validation<Integer> validation = Validation.invalid(errorMessage);
        Validation<Integer> mappedValidation = validation.map(mapper);
        assertThat(mappedValidation.isValid()).isFalse();
        assertThat(mappedValidation.getErrors()).containsExactly(errorMessage);
    }

    @Test
    @DisplayName("A valid Validation is equal to itself")
    public void testEqualsReflexiveValid() {
        Validation<Object> v = Validation.valid(new Object());
        assertThat(v).isEqualTo(v);
    }
    @Test
    @DisplayName("An invalid Validation is equal to itself")
    public void testEqualsReflexiveInvalid() {
        Validation<Object> v = Validation.invalid("err");
        assertThat(v).isEqualTo(v);
    }
    @Test
    @DisplayName("Two Validations with the same error message are symmetrically equal")
    public void testEqualsSymmetric() {
        Validation<Object> v1 = Validation.invalid("err");
        Validation<Object> v2 = Validation.invalid("err");
        assertThat(v1).isEqualTo(v2).hasSameHashCodeAs(v2);
        assertThat(v2).isEqualTo(v1);
    }
    @Test
    @DisplayName("Two Validations with the same error messages are symmetrically equal")
    public void testEqualsSymmetric2() {
        String err1 = "err1";
        String err2 = "err2";
        Validation<Object> v1 = Validation.invalid(err1, err2);
        Validation<Object> v2 = Validation.invalid(err1, err2);
        assertThat(v1).isEqualTo(v2).hasSameHashCodeAs(v2);
        assertThat(v2).isEqualTo(v1);
    }
    @Test
    @DisplayName("A valid Validation is never equal to an invalid Validation")
    public void testValidNotEqualsInvalid() {
        Validation<Object> valid = Validation.valid(new Object());
        Validation<Object> invalid = Validation.invalid("err");
        assertThat(valid).isNotEqualTo(invalid);
    }
    @Test
    @DisplayName("Two Validations with different error messages are not equal")
    public void testNotEqualsInvalid() {
        Validation<Object> v1 = Validation.invalid("err");
        Validation<Object> v2 = Validation.invalid("ERR");
        assertThat(v1).isNotEqualTo(v2);
    }
    @Test
    @DisplayName("Two Validations with differently ordered error messages are not equal")
    public void testNotEqualsOrdered() {
        String err1 = "err1";
        String err2 = "err2";
        Validation<Object> v1 = Validation.invalid(err1, err2);
        Validation<Object> v2 = Validation.invalid(err2, err1);
        assertThat(v1).isNotEqualTo(v2);
    }

}
