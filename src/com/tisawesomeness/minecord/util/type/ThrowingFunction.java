package com.tisawesomeness.minecord.util.type;

import lombok.SneakyThrows;

import java.util.function.Function;

/**
 * A version of {@link Function} that can throw an
 * unchecked exception with the help of {@link SneakyThrows}.
 * <br>Use <b>only</b> to override or implement methods that may throw exceptions.
 * @param <T> the type of the input to the function
 * @param <R> the type of the result of the function
 * @param <E> the type of exception the function can throw
 */
@FunctionalInterface
public interface ThrowingFunction<T , R, E extends Throwable> extends Function<T, R> {
    @SneakyThrows
    default R apply(T t) {
        return applyThrows(t);
    }
    R applyThrows(T t) throws E;
}
