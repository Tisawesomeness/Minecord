package com.tisawesomeness.minecord.util.type;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

// Inspiration from Vavr's Validation type
// Unlike Vavr, error is always String, lombok is used, and combining directly returns another Validation
/**
 * Contains either a valid value of any type, or one or more error message strings.
 * <br>Which option is returned is controlled by {@link #isValid()}.
 * <br>This class is immutable and therefore thread-safe.
 * @param <T> The type of the value returned when this Validation is valid
 * @see Verification
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Validation<T> {
    private final @Nullable T value;
    private final Verification verification;

    /**
     * Creates a valid Validation.
     * @param value The success value
     * @param <T> The type of the Validation value
     * @return A valid Validation with no error message
     */
    public static <T> Validation<T> valid(@NonNull T value) {
        return new Validation<>(value, Verification.valid());
    }
    /**
     * Creates an invalid Validation.
     * @param errorMessage The error message
     * @param <T> The type of the Validation value, not the type of the error message
     * @return An invalid Validation with no value
     */
    public static <T> Validation<T> invalid(@NonNull String errorMessage) {
        return new Validation<>(null, Verification.invalid(errorMessage));
    }
    /**
     * Creates an invalid Validation.
     * @param firstError The first error message
     * @param secondError The second error message
     * @param rest The rest of the error messages
     * @param <T> The type of the Validation value, not the type of the error message
     * @return An invalid Validation with no value
     */
    public static <T> Validation<T> invalid(@NonNull String firstError, @NonNull String secondError, String... rest) {
        return new Validation<>(null, Verification.invalid(firstError, secondError, rest));
    }
    /**
     * Creates an invalid Validation.
     * @param errorMessages The list of error messages
     * @param <T> The type of the Validation value, not the type of the error message
     * @return An invalid Validation with no value
     * @throws IllegalArgumentException If the list of error messages is empty
     */
    public static <T> Validation<T> invalid(Collection<String> errorMessages) {
        return new Validation<>(null, Verification.invalid(errorMessages));
    }

    /**
     * Creates a new Validation from the provided optional.
     * <br>If the optional contains a value, a valid Validation is created with that value.
     * <br>Otherwise, an invalid Validation is created with the provided error message.
     * @param opt The Optional used to create a valid Validation
     * @param errorMessage The error message used to create an invalid Validation
     * @param <T> The type of the Optional and the new Validation
     * @return A Validation where {@link #isValid()} returns the same as {@link Optional#isPresent() opt.isPresent()}.
     */
    public static <T> Validation<T> fromOptional(Optional<T> opt, @NonNull String errorMessage) {
        return opt.map(Validation::valid).orElse(invalid(errorMessage));
    }
    /**
     * Creates a new Validation from an invalid Verification.
     * @param v The verification
     * @param <T> The type of the new Validation
     * @return An invalid Validation with the same error messages as the Verification
     */
    public static <T> Validation<T> fromInvalidVerification(Verification v) {
        if (v.isValid()) {
            throw new IllegalArgumentException("The incoming Verification must be invalid.");
        }
        return new Validation<>(null, v);
    }

    /**
     * Creates an invalid Validation, changing the type of the original.
     * @param v The original invalid Validation
     * @param <T> The type of the new Validation
     * @return A new Validation with a different type
     * @throws IllegalStateException If this Validation is valid
     */
    public static <T> Validation<T> propogateError(Validation<?> v) {
        if (v.isValid()) {
            throw new IllegalStateException("propogateError() cannot be used on a valid Validation.");
        }
        return new Validation<>(null, v.verification);
    }

    /**
     * Tests if this Validation is valid.
     * <br>If true, {@link #getValue()} returns a value.
     * <br>If false, {@link #getErrors()} is non-empty.
     * @return Whether this Validation is valid
     */
    public boolean isValid() {
        return verification.isValid();
    }

    /**
     * Gets the value of this Validation.
     * @return The value if {@link #isValid()} is {@code true}
     * @throws IllegalStateException If this Validation is not valid
     */
    public @NonNull T getValue() {
        if (value == null) {
            throw new IllegalStateException("No value present");
        }
        return value;
    }
    /**
     * Gets a list of error messages for this Validation.
     * @return An immutable list of string error messages
     */
    public List<String> getErrors() {
        return verification.getErrors();
    }

    /**
     * Combines this Validation with another.
     * <br>If either is invalid, then the error messages are combined.
     * <br>If both are valid, then the value in {@code other} is used.
     * @param other The other Validation to combine with
     * @param <U> The type of both Validations
     * @return A single Validation, which is valid only if both input Validations are valid.
     */
    public <U> Validation<U> combine(Validation<U> other) {
        if (isValid()) {
            return other;
        }
        if (other.isValid()) {
            return propogateError(this);
        }
        return new Validation<>(null, verification.combine(other.verification));
    }
    /**
     * Combines multiple Validations, reducing them using {@link #combine(Validation)}.
     * <br>Values on the right take priority.
     * @param first The first Validation which all others are combined onto.
     * @param rest If not specified, {@code first} is returned.
     * @param <U> The type of both Validations
     * @return A single Validation, which is valid only if both input Validations are valid.
     */
    @SafeVarargs // Does not store anything in array, and rest array is not visible
    public static <U> Validation<U> combineAll(Validation<? extends U> first, Validation<? extends U>... rest) {
        U val = first.isValid() ? first.getValue() : null;
        List<String> errors = new ArrayList<>(first.getErrors());
        for (Validation<? extends U> v : rest) {
            if (v.isValid()) {
                val = v.getValue();
            } else {
                errors.addAll(v.getErrors());
            }
        }
        if (errors.isEmpty()) {
            assert val != null; // if there are no errors, at least one validation must be valid and contain a value
            return valid(val);
        }
        return fromInvalidVerification(new Verification(Collections.unmodifiableList(errors)));
    }

    /**
     * If this Validation is valid, apply the mapper to the value.
     * Otherwise, returns a Validation with the same error message.
     * @param mapper A function mapping the old value to the new value
     * @param <U> The type of the returned Validation
     * @return A Validation with the mapped value if present
     */
    public <U> Validation<U> map(@NonNull Function<? super T, ? extends U> mapper) {
        if (isValid()) {
            return valid(mapper.apply(getValue()));
        }
        return propogateError(this);
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Validation)) {
            return false;
        }
        Validation<?> other = (Validation<?>) o;
        if (isValid()) {
            return other.isValid();
        }
        return !other.isValid() && verification.equals(other.verification);
    }
    @Override
    public int hashCode() {
        if (isValid()) {
            assert value != null;
            return 31 * value.hashCode() + verification.hashCode();
        }
        return 0;
    }

    /**
     * Displays either the value or all error messages of this Validation, depending on the result of {@link #isValid()}.
     * <br>Uses {@link #getValue()}.{@link Object#toString() toString()} if valid.
     */
    @Override
    public String toString() {
        if (isValid()) {
            return map(o -> String.format("Valid{%s}", o)).getValue();
        }
        return String.format("Invalid{%s}", getErrors());
    }

}
