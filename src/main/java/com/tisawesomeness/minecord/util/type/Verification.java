package com.tisawesomeness.minecord.util.type;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.*;

/**
 * An object that may contain one or more error messages.
 * <br>If this Verification {@link #isValid()}, then the list of error messages is empty.
 * <br>This class is immutable and therefore thread-safe.
 * <br>See {@link Verification} for a version of this class that also contains a value when valid.
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
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
    public static Verification invalid(@NonNull String errorMessage) {
        return new Verification(Collections.singletonList(errorMessage));
    }
    /**
     * Creates an invalid Verification.
     * @param firstError The first error message
     * @param secondError The second error message
     * @param rest The rest of the error messages
     * @return A Verification that has multiple error messages
     */
    public static Verification invalid(@NonNull String firstError, @NonNull String secondError, String... rest) {
        List<String> errors = new ArrayList<>();
        errors.add(firstError);
        errors.add(secondError);
        errors.addAll(Arrays.asList(rest));
        return new Verification(Collections.unmodifiableList(errors));
    }
    /**
     * Creates an invalid Verification.
     * @param errorMessages The list of error messages
     * @return A Verification that has multiple error messages
     * @throws IllegalArgumentException If the list of error messages is empty
     */
    public static Verification invalid(Collection<String> errorMessages) {
        if (errorMessages.isEmpty()) {
            throw new IllegalArgumentException("The list of error messages must not be empty");
        }
        return new Verification(Collections.unmodifiableList(new ArrayList<>(errorMessages)));
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
    public <T> Validation<T> toValidation(@NonNull T value) {
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
        List<String> list = new ArrayList<>(first.errors);
        for (Verification v : rest) {
            list.addAll(v.errors);
        }
        return new Verification(Collections.unmodifiableList(list));
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
