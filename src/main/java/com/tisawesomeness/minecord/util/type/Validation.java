package com.tisawesomeness.minecord.util.type;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

// Inspiration from Vavr's Validation type
// Unlike Vavr, error is always String, lombok is used, and combining directly returns another Validation
// Class is immutable
/**
 * Contains either a valid value of any type, or one or more error message strings.
 * <br>Which option is returned is controlled by {@link #isValid()}.
 * @param <T> The type of the value returned when this validation is valid
 */
@RequiredArgsConstructor
@EqualsAndHashCode
public final class Validation<T> {
    private final @Nullable T value;
    private final List<String> errors; // Must be immutable

    /**
     * Creates a valid Validation.
     * @param value The success value
     * @param <T> The type of the validation value
     * @return A valid Validation with no error message
     */
    public static <T> Validation<T> valid(@NonNull T value) {
        return new Validation<>(value, Collections.emptyList());
    }
    /**
     * Creates an invalid Validation.
     * @param errorMessage The error message
     * @param <T> The type of the validation value, not the type of the error message
     * @return An invalid Validation with no value
     */
    public static <T> Validation<T> invalid(@NonNull String errorMessage) {
        return new Validation<>(null, Collections.singletonList(errorMessage));
    }

    /**
     * Creates an invalid Validation, changing the type of the original.
     * @param v The original invalid Validation
     * @param <T> The type of the new Validation
     * @return A new Validation with a different type
     * @throws IllegalStateException If this validation is valid
     */
    public static <T> Validation<T> propogateError(Validation<?> v) {
        if (v.isValid()) {
            throw new IllegalStateException("propogateError() cannot be used on a valid Validation.");
        }
        return new Validation<>(null, v.errors);
    }

    /**
     * Tests if this validation is valid.
     * <br>If true, {@link #getValue()} returns a value.
     * <br>If false, {@link #getErrors()} is non-empty.
     * @return Whether this validation is valid
     */
    public boolean isValid() {
        return value != null;
    }

    /**
     * Gets the value of this validation.
     * @return The value if {@link #isValid()} is {@code true}
     * @throws IllegalStateException If this validation is not valid
     */
    public @NonNull T getValue() {
        if (value == null) {
            throw new IllegalStateException("No value present");
        }
        return value;
    }
    /**
     * Gets a list of error messages for this validation.
     * @return An immutable list of string error messages
     */
    public List<String> getErrors() {
        return errors;
    }

    /**
     * Combines this Validation with another.
     * <br>If either is invalid, then the error messages are combined.
     * <br>If both are valid, then the value in {@code other} is used.
     * @param other The other validation to combine with
     * @param <U> The type of both validations
     * @return A single Validation, which is valid iff both input validations are valid.
     */
    public <U> Validation<U> combine(Validation<U> other) {
        if (isValid()) {
            return other;
        }
        if (other.isValid()) {
            return propogateError(this);
        }
        List<String> list = new ArrayList<>(errors);
        list.addAll(other.errors);
        return new Validation<>(null, Collections.unmodifiableList(list));
    }
    /**
     * Combines multiple validations, reducing them using {@link #combine(Validation)}.
     * <br>Values on the right take priority.
     * @param first The first validation which all others are combined onto.
     * @param second Used to prevent ambiguity with the non-static combine method.
     * @param rest If not specified, {@code first.combine(second)} is returned.
     * @param <U> The type of both validations
     * @return A single Validation, which is valid iff both input validations are valid.
     */
    @SafeVarargs // Does not store anything in array, and rest array is not visible
    public static <U> Validation<U> combine(
            Validation<U> first, Validation<U> second, Validation<U>... rest) {
        Validation<U> v = first.combine(second);
        for (Validation<U> r : rest) {
            v = v.combine(r);
        }
        return v;
    }

    /**
     * If this validation is valid, apply the mapper to the value.
     * Otherwise, returns a validation with the same error message.
     * @param mapper A function mapping the old value to the new value
     * @param <U> The type of the returned Validation
     * @return A validation with the mapped value if present
     */
    public <U> Validation<U> map(@NonNull Function<? super T, ? extends U> mapper) {
        if (isValid()) {
            return valid(mapper.apply(getValue()));
        }
        return propogateError(this);
    }

    /**
     * Displays either the value or all error messages of this validation, depending on the result of {@link #isValid()}.
     * <br>Uses {@link #getValue()}.{@link Object#toString() toString()} if valid.
     */
    @Override
    public String toString() {
        if (isValid()) {
            return map(o -> String.format("Valid{%s}", o)).getValue();
        }
        return String.format("Invalid{%s}", errors);
    }
}
