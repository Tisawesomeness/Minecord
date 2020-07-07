package com.tisawesomeness.minecord.util.type;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ValidationTest {

    @Test
    @DisplayName("Validation factory method does not alter object value")
    public void testValidObject() {
        Object o = new Object();
        Validation<Object> v = Validation.valid(o);
        assertTrue(v.isValid());
        assertEquals(o, v.getValue());
    }
    @ParameterizedTest
    @ValueSource(ints = {Integer.MIN_VALUE, -2, -1, 0, 1, 2, Integer.MAX_VALUE})
    @DisplayName("Validation factory method does not alter primitive value")
    public void testValidPrimitive(int candidate) {
        Validation<Integer> v = Validation.valid(candidate);
        assertTrue(v.isValid());
        assertEquals(candidate, v.getValue());
    }
    @ParameterizedTest
    @MethodSource("objectArrayProvider")
    @DisplayName("Validation factory method does not alter array value")
    public void testValidArray(Object[] candidate) {
        Validation<Object[]> v = Validation.valid(candidate);
        assertTrue(v.isValid());
        assertArrayEquals(candidate, v.getValue());
    }
    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = "test")
    @DisplayName("Validation factory method does not alter string value")
    public void testValidPrimitive(String candidate) {
        Validation<String> v = Validation.valid(candidate);
        assertTrue(v.isValid());
        assertEquals(candidate, v.getValue());
    }

    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = "test")
    @DisplayName("Validation factory method does not alter error message")
    public void testInvalid(String candidate) {
        Validation<Object> v = Validation.invalid(candidate);
        assertFalse(v.isValid());
        assertEquals(candidate, v.getErrorMessage());
    }

    @Test
    @DisplayName("getValue() throws NoSuchElementException if not valid")
    public void testGetValue() {
        Validation<?> v = Validation.invalid("An error message");
        assertThrows(NoSuchElementException.class, v::getValue);
    }
    @Test
    @DisplayName("getErrorMessage() throws NoSuchElementException if valid")
    public void testGetErrorMessage() {
        Validation<Object> v = Validation.valid(new Object());
        assertThrows(NoSuchElementException.class, v::getErrorMessage);
    }

    @Test
    @DisplayName("propogateError() keeps the error message")
    public void testPropogateError() {
        String errorMessage = "An error message";
        Validation<?> v = Validation.invalid(errorMessage);
        assertFalse(v.isValid());
        assertEquals(errorMessage, v.getErrorMessage());
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
        assertEquals(errorMessage, mappedValidation.getErrorMessage());
    }
    @Test
    @DisplayName("Calling mapError() on a valid validation keeps the value")
    public void testMapErrorValid() {
        Function<String, String> mapper = String::toLowerCase;
        int i = 2;
        Validation<Integer> validation = Validation.valid(i);
        Validation<Integer> mappedValidation = validation.mapError(mapper);
        assertTrue(mappedValidation.isValid());
        assertEquals(i, mappedValidation.getValue());
    }
    @Test
    @DisplayName("Calling mapError() on an ivalid validation applies the mapper")
    public void testMapErrorInvalid() {
        Function<String, String> mapper = String::toLowerCase;
        String errorMessage = "An error message";
        Validation<Integer> validation = Validation.invalid(errorMessage);
        Validation<Integer> mappedValidation = validation.mapError(mapper);
        assertFalse(mappedValidation.isValid());
        assertEquals(mapper.apply(errorMessage), mappedValidation.getErrorMessage());
    }

    private static Stream<Arguments> objectArrayProvider() {
        Object o = new Object();
        return Stream.of(
                Arguments.of((Object) new Object[0]),
                Arguments.of((Object) new Object[]{o}),
                Arguments.of((Object) new Object[]{o, o})
        );
    }

}
