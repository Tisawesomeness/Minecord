package com.tisawesomeness.minecord.util.type;

import lombok.SneakyThrows;

import java.util.function.Function;

/**
 * A version of {@link Function} that can throw unchecked exceptions with the help of {@link SneakyThrows}.
 * <br>Use <b>only</b> to override or implement methods that may throw exceptions.
 * @param <T> the type of the input to the function
 * @param <R> the type of the result of the function
 */
@FunctionalInterface
public interface ThrowingFunction<T , R> extends Function<T, R> {
    @SneakyThrows
    default R apply(T t) {
        return applyThrows(t);
    }
    R applyThrows(T t) throws Exception;
}
