package com.tisawesomeness.minecord.util.type;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class VerificationTest {

    @Test
    @DisplayName("Valid factory method returns valid Verification")
    public void testValid() {
        Verification v = Verification.valid();
        assertTrue(v.isValid());
    }
    @Test
    @DisplayName("Invalid factory method returns invalid Verification, keeping error")
    public void testInvalid() {
        String errorMessage = "An error message";
        Verification v = Verification.invalid(errorMessage);
        assertFalse(v.isValid());
        List<String> errors = v.getErrors();
        assertEquals(1, errors.size());
        assertEquals(errorMessage, errors.get(0));
    }

    @Test
    @DisplayName("v1.combine(v2) with both valid is valid")
    public void testCombineBothValid() {
        Verification v1 = Verification.valid();
        Verification v2 = Verification.valid();
        Verification vCombined = v1.combine(v2);
        assertTrue(vCombined.isValid());
    }
    @Test
    @DisplayName("v1.combine(v2) with left valid returns right error")
    public void testCombineLeftValid() {
        Verification v1 = Verification.valid();
        String errorMessage = "An error message";
        Verification v2 = Verification.invalid(errorMessage);
        Verification vCombined = v1.combine(v2);
        assertFalse(vCombined.isValid());
        List<String> errors = vCombined.getErrors();
        assertEquals(1, errors.size());
        assertEquals(errorMessage, errors.get(0));
    }
    @Test
    @DisplayName("v1.combine(v2) with right valid returns left error")
    public void testCombineRightValid() {
        String errorMessage = "An error message";
        Verification v1 = Verification.invalid(errorMessage);
        Verification v2 = Verification.valid();
        Verification vCombined = v1.combine(v2);
        assertFalse(vCombined.isValid());
        List<String> errors = vCombined.getErrors();
        assertEquals(1, errors.size());
        assertEquals(errorMessage, errors.get(0));
    }
    @Test
    @DisplayName("v1.combine(v2) with both invalid combines error messages")
    public void testCombineBothInvalid() {
        String errorMessage1 = "First error message";
        Verification v1 = Verification.invalid(errorMessage1);
        String errorMessage2 = "Second error message";
        Verification v2 = Verification.invalid(errorMessage2);
        Verification vCombined = v1.combine(v2);
        assertFalse(vCombined.isValid());
        List<String> errors = vCombined.getErrors();
        assertEquals(Arrays.asList(errorMessage1, errorMessage2), errors);
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
        Verification vCombined = Verification.combine(v1, v2, v3);
        assertFalse(vCombined.isValid());
        List<String> errors = vCombined.getErrors();
        assertEquals(Arrays.asList(errorMessage1, errorMessage2, errorMessage3), errors);
    }

}
