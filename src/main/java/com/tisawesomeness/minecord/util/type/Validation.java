package com.tisawesomeness.minecord.util.type;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.NoSuchElementException;
import java.util.Optional;

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

    public boolean isValid() {
        return value.isPresent();
    }
    public @NonNull T getValue() {
        return value.orElseThrow(() -> new NoSuchElementException("No value present"));
    }
    public @NonNull String getErrorMessage() {
        return errorMessage.orElseThrow(() -> new NoSuchElementException("No validation error message present"));
    }

    @Override
    public String toString() {
        if (isValid()) {
            return value.map(o -> String.format("Valid{%s}", o)).orElseThrow(this::assertionError);
        }
        return errorMessage.map(s -> String.format("Invalid{%s}", s)).orElseThrow(this::assertionError);
    }
    private AssertionError assertionError() {
        return new AssertionError("Both value and error cannot be empty");
    }
}
