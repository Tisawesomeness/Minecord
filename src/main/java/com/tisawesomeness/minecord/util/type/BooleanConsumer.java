package com.tisawesomeness.minecord.util.type;

import lombok.NonNull;

import java.util.function.Consumer;

/**
 * A {@link Consumer} that takes a primitive boolean as input.
 */
@FunctionalInterface
public interface BooleanConsumer {

    /**
     * Performs this operation on the given argument.
     * @param b the input argument
     */
    void accept(boolean b);

    /**
     * Returns a composed that performs this operation, then the after operation.
     * @param consumer the operation to perform after
     * @return a new consumer
     */
    default BooleanConsumer andThen(@NonNull BooleanConsumer consumer) {
        return b -> {
            accept(b);
            consumer.accept(b);
        };
    }

}
