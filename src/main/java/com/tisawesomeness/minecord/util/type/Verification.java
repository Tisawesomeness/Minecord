package com.tisawesomeness.minecord.util.type;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * An object that may contain one or more error messages.
 * <br>If this Verification {@link #isValid()}, then the list of error messages is empty.
 * <br>This class is immutable and therefore thread-safe.
 * <br>See {@link Verification} for a version of this class that also contains a value when valid.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Verification {
    private final static Verification VALID = new Verification(Collections.emptyList());
    private final List<String> errors; // Must be immutable

    /**
     * Returns the valid Verification.
     * @return A Verification with no error messages
     */
    public static Verification valid() {
        return VALID;
    }
    /**
     * Creates an invalid Verification.
     * @param errorMessage The error message
     * @return A Verification that has one error message
     */
    public static Verification invalid(String errorMessage) {
        return new Verification(Collections.singletonList(errorMessage));
    }

    /**
     * Tests if this Verification is valid.
     * <br>If true, {@link #getErrors()} is empty.
     * @return Whether this Verification is valid
     */
    public boolean isValid() {
        return errors.isEmpty();
    }
    /**
     * Gets a list of error messages for this Verification.
     * @return An immutable list of string error messages
     */
    public List<String> getErrors() {
        return errors;
    }

    /**
     * Creates a Validation from this Verification
     * @param value The value given to the Validation if this Verification is valid
     * @param <T> The type of the new Validation
     * @return A Validation that is valid only if this Verification is valid
     */
    public <T> Validation<T> toValidation(T value) {
        if (isValid()) {
            return Validation.valid(value);
        }
        return Validation.fromInvalidVerification(this);
    }

    /**
     * Combines this Verification with another.
     * <br>If either is invalid, then the error messages are combined.
     * <br>If both are valid, then the resulting Verification is also valid.
     * @param other The other Verification to combine with
     * @return A single Verification, which is valid only if both input Verifications are valid.
     */
    public Verification combine(Verification other) {
        if (isValid()) {
            return other;
        }
        if (other.isValid()) {
            return this;
        }
        List<String> list = new ArrayList<>(errors);
        list.addAll(other.errors);
        return new Verification(Collections.unmodifiableList(list));
    }

    /**
     * Combines multiple Verifications, reducing them using {@link #combine(Verification)}.
     * @param first The first Verification which all others are combined onto.
     * @param rest If not specified, {@code first} is returned.
     * @return A single Verification, which is valid only if both input Verifications are valid.
     */
    public static Verification combineAll(Verification first, Verification... rest) {
        return Arrays.stream(rest).reduce(first, Verification::combine);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Verification)) {
            return false;
        }
        Verification other = (Verification) o;
        return errors.equals(other.errors);
    }
    @Override
    public int hashCode() {
        return errors.hashCode();
    }

    @Override
    public String toString() {
        return "Verification" + errors;
    }

}
