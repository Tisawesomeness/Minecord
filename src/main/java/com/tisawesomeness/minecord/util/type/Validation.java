package com.tisawesomeness.minecord.util.type;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;

// Inspiration from Vavr's Either and Validation types
/**
 * Contains either a valid value of any type, or an error message string.
 * <br>Which option is returned is controlled by {@link #isValid()}.
 * <br>Functions similar to a right-based {@code Either<String, T>}.
 * @param <T> The type of the value returned when this value is valid
 */
@RequiredArgsConstructor
@EqualsAndHashCode
public final class Validation<T> {
    private final Optional<T> value;
    private final Optional<String> errorMessage;

    /**
     * Creates a valid Validation.
     * @param value The success value
     * @param <T> The type of the validation value
     * @return A valid Validation with no error message
     */
    public static <T> Validation<T> valid(@NonNull T value) {
        return new Validation<>(Optional.of(value), Optional.empty());
    }
    /**
     * Creates an invalid Validation.
     * @param errorMessage The error message
     * @param <T> The type of the validation value, not the type of the error message
     * @return An invalid Validation with no value
     */
    public static <T> Validation<T> invalid(@NonNull String errorMessage) {
        return new Validation<>(Optional.empty(), Optional.of(errorMessage));
    }

    /**
     * Creates an invalid Validation, changing the type of the original.
     * @param v The original invalid Validation
     * @param <T> The type of the new Validation
     * @return A new Validation with a different type
     * @throws NoSuchElementException If this validation is valid
     */
    public static <T> Validation<T> propogateError(Validation<?> v) {
        return invalid(v.getErrorMessage());
    }

    /**
     * Tests if this validation is valid.
     * <br>If true, {@link #getValue()} returns a value.
     * <br>If false, {@link #getErrorMessage()} returns an error message.
     * @return Whether this validation is valid.
     */
    public boolean isValid() {
        return value.isPresent();
    }

    /**
     * Gets the value of this validation.
     * @return The non-null value if {@link #isValid()} is true
     * @throws NoSuchElementException If this validation is not valid
     */
    public @NonNull T getValue() {
        return value.orElseThrow(() -> new NoSuchElementException("No value present"));
    }
    /**
     * Gets the error message of this validation.
     * @return The non-null error message if {@link #isValid()} is false
     * @throws NoSuchElementException If this validation is valid
     */
    public @NonNull String getErrorMessage() {
        return errorMessage.orElseThrow(() -> new NoSuchElementException("No validation error message present"));
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
     * If this validation is invalid, apply the mapper to the error message.
     * Otherwise, returns the same validation.
     * @param mapper A function mapping the old error message to the new error message
     * @return A validation with the mapped value if present
     */
    public Validation<T> mapError(@NonNull Function<? super String, String> mapper) {
        if (!isValid()) {
            return invalid(mapper.apply(getErrorMessage()));
        }
        return this;
    }

    /**
     * Displays either the value or error message of this validation, depending on the result of {@link #isValid()}.
     * <br>Uses {@link #getValue()}.{@link Object#toString() toString()} if valid.
     */
    @Override
    public String toString() {
        if (isValid()) {
            return map(o -> String.format("Valid{%s}", o)).getValue();
        }
        return mapError(s -> String.format("Invalid{%s}", s)).getErrorMessage();
    }
}
