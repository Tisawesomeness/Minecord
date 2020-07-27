package com.tisawesomeness.minecord.util.type;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ValidationTest {

    @Test
    @DisplayName("Validation factory method does not alter value")
    public void testValidObject() {
        Object o = new Object();
        Validation<Object> v = Validation.valid(o);
        assertTrue(v.isValid());
        assertEquals(o, v.getValue());
    }
    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = "test")
    @DisplayName("Validation factory method does not alter error message")
    public void testInvalid(String candidate) {
        Validation<Object> v = Validation.invalid(candidate);
        assertFalse(v.isValid());
        List<String> errors = v.getErrors();
        assertEquals(1, errors.size());
        assertEquals(candidate, errors.get(0));
    }

    @Test
    @DisplayName("Validation from non-empty Optional returns that value")
    public void testFromOptionalValid() {
        Object o = new Object();
        Optional<Object> opt = Optional.of(o);
        Validation<Object> v = Validation.fromOptional(opt, "An error message");
        assertTrue(v.isValid());
        assertEquals(o, v.getValue());
    }
    @Test
    @DisplayName("Validation from empty Optional returns the error message")
    public void testFromOptionalInvalid() {
        String err = "An error message";
        Optional<Object> opt = Optional.empty();
        Validation<Object> v = Validation.fromOptional(opt, err);
        assertFalse(v.isValid());
        assertEquals(err, v.getErrors().get(0));
    }

    @Test
    @DisplayName("Validation from valid Verification throws IllegalArgumentException")
    public void testFromInvalidVerificationValid() {
        Verification v = Verification.valid();
        assertThrows(IllegalArgumentException.class, () -> Validation.fromInvalidVerification(v));
    }
    @Test
    @DisplayName("Validation from invalid Verification keeps error message")
    public void testFromInvalidVerificationInvalid() {
        String err = "An error message";
        Verification ve = Verification.invalid(err);
        Validation<?> va = Validation.fromInvalidVerification(ve);
        assertFalse(va.isValid());
        assertEquals(ve.getErrors(), va.getErrors());
    }

    @Test
    @DisplayName("getValue() throws IllegalStateException if not valid")
    public void testGetValue() {
        Validation<?> v = Validation.invalid("An error message");
        assertThrows(IllegalStateException.class, v::getValue);
    }
    @Test
    @DisplayName("getErrorMessage() is empty if valid")
    public void testGetErrorMessage() {
        Validation<Object> v = Validation.valid(new Object());
        List<String> errors = v.getErrors();
        assertEquals(0, errors.size());
    }

    @Test
    @DisplayName("propogateError() keeps the error message")
    public void testPropogateError() {
        String errorMessage = "An error message";
        Validation<?> v = Validation.invalid(errorMessage);
        List<String> errors = v.getErrors();
        assertEquals(1, errors.size());
        assertEquals(errorMessage, errors.get(0));
    }
    @Test
    @DisplayName("propogateError() throws for a valid Validation")
    public void testPropogateErrorValid() {
        Validation<Object> v = Validation.valid(new Object());
        assertThrows(IllegalStateException.class, () -> Validation.propogateError(v));
    }

    @Test
    @DisplayName("v1.combine(v2) with both valid keeps value")
    public void testCombineBothValid() {
        Object o = new Object();
        Validation<Object> v1 = Validation.valid(o);
        Validation<Object> v2 = Validation.valid(o);
        Validation<Object> vCombined = v1.combine(v2);
        assertTrue(vCombined.isValid());
        assertEquals(o, vCombined.getValue());
    }
    @Test
    @DisplayName("v1.combine(v2) with both valid, but different, keeps right value")
    public void testCombineBothValidRightPreference() {
        Object o1 = new Object();
        Validation<Object> v1 = Validation.valid(o1);
        Object o2 = new Object();
        Validation<Object> v2 = Validation.valid(o2);
        Validation<Object> vCombined = v1.combine(v2);
        assertTrue(vCombined.isValid());
        assertEquals(o2, vCombined.getValue());
    }
    @Test
    @DisplayName("v1.combine(v2) with left valid returns right error")
    public void testCombineLeftValid() {
        Object o = new Object();
        Validation<Object> v1 = Validation.valid(o);
        String errorMessage = "An error message";
        Validation<Object> v2 = Validation.invalid(errorMessage);
        Validation<Object> vCombined = v1.combine(v2);
        assertFalse(vCombined.isValid());
        List<String> errors = vCombined.getErrors();
        assertEquals(1, errors.size());
        assertEquals(errorMessage, errors.get(0));
    }
    @Test
    @DisplayName("v1.combine(v2) with right valid returns left error")
    public void testCombineRightValid() {
        String errorMessage = "An error message";
        Validation<Object> v1 = Validation.invalid(errorMessage);
        Object o = new Object();
        Validation<Object> v2 = Validation.valid(o);
        Validation<Object> vCombined = v1.combine(v2);
        assertFalse(vCombined.isValid());
        List<String> errors = vCombined.getErrors();
        assertEquals(1, errors.size());
        assertEquals(errorMessage, errors.get(0));
    }
    @Test
    @DisplayName("v1.combine(v2) with both invalid combines error messages")
    public void testCombineBothInvalid() {
        String errorMessage1 = "First error message";
        Validation<Object> v1 = Validation.invalid(errorMessage1);
        String errorMessage2 = "Second error message";
        Validation<Object> v2 = Validation.invalid(errorMessage2);
        Validation<Object> vCombined = v1.combine(v2);
        assertFalse(vCombined.isValid());
        List<String> errors = vCombined.getErrors();
        assertEquals(Arrays.asList(errorMessage1, errorMessage2), errors);
    }
    @Test
    @DisplayName("Combining with varargs combines all error message")
    public void testCombineAll() {
        String errorMessage1 = "First error message";
        Validation<Object> v1 = Validation.invalid(errorMessage1);
        String errorMessage2 = "Second error message";
        Validation<Object> v2 = Validation.invalid(errorMessage2);
        String errorMessage3 = "Third error message";
        Validation<Object> v3 = Validation.invalid(errorMessage3);
        Validation<Object> vCombined = Validation.combineAll(v1, v2, v3);
        assertFalse(vCombined.isValid());
        List<String> errors = vCombined.getErrors();
        assertEquals(Arrays.asList(errorMessage1, errorMessage2, errorMessage3), errors);
    }

    @Test
    @DisplayName("Calling map() on a valid validation applies the mapper")
    public void testMapValid() {
        Function<Integer, Integer> mapper = x -> x * 2;
        int i = 2;
        Validation<Integer> validation = Validation.valid(i);
        Validation<Integer> mappedValidation = validation.map(mapper);
        assertTrue(mappedValidation.isValid());
        assertEquals(mapper.apply(i), mappedValidation.getValue());
    }
    @Test
    @DisplayName("Calling map() on an invalid validation keeps the error message")
    public void testMapInvalid() {
        Function<Integer, Integer> mapper = x -> x * 2;
        String errorMessage = "An error message";
        Validation<Integer> validation = Validation.invalid(errorMessage);
        Validation<Integer> mappedValidation = validation.map(mapper);
        assertFalse(mappedValidation.isValid());
        List<String> errors = mappedValidation.getErrors();
        assertEquals(1, errors.size());
        assertEquals(errorMessage, errors.get(0));
    }

}
