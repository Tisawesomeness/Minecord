package com.tisawesomeness.minecord.util.type;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;

// Inspiration from Vavr's Either and Validation types
@RequiredArgsConstructor
@EqualsAndHashCode
public final class Validation<T> {
    private final Optional<T> value;
    private final Optional<String> errorMessage;

    public static <T> Validation<T> valid(@NonNull T value) {
        return new Validation<>(Optional.of(value), Optional.empty());
    }
    public static <T> Validation<T> invalid(@NonNull String errorMessage) {
        return new Validation<>(Optional.empty(), Optional.of(errorMessage));
    }

    public static <T> Validation<T> propogateError(Validation<?> other) {
        return Validation.invalid(other.getErrorMessage());
    }

    public boolean isValid() {
        return value.isPresent();
    }
    public @NonNull T getValue() {
        return value.orElseThrow(() -> new NoSuchElementException("No value present"));
    }
    public @NonNull String getErrorMessage() {
        return errorMessage.orElseThrow(() -> new NoSuchElementException("No validation error message present"));
    }

    public <U> Validation<U> map(Function<T, U> mapper) {
        if (isValid()) {
            return Validation.valid(mapper.apply(getValue()));
        }
        return propogateError(this);
    }
    public Validation<T> mapError(Function<String, String> mapper) {
        if (!isValid()) {
            return Validation.invalid(mapper.apply(getErrorMessage()));
        }
        return this;
    }

    @Override
    public String toString() {
        if (isValid()) {
            return map(o -> String.format("Valid{%s}", o)).getValue();
        }
        return mapError(s -> String.format("Invalid{%s}", s)).getErrorMessage();
    }
}
