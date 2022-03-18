package com.tisawesomeness.minecord.util.type;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nullable;
import java.util.function.Function;

// Inspiration from Vavr's Either type
// Why is right success? So isRight() could also mean isCorrect()
/**
 * This type is always either a Left or a Right, controlled by {@link #isRight()}.
 * <br>By convention, right is success and left is failure.
 * @param <L> The type of the left value
 * @param <R> The type of the right value
 */
@RequiredArgsConstructor
public final class Either<L, R> {
    private final @Nullable L left;
    private final @Nullable R right;

    /**
     * Creates a Left Either.
     * @param value The left value
     * @param <L> The type of the left value
     * @param <R> The type of the right value
     * @return An Either where {@link #isRight()} returns {@code false}
     */
    public static <L, R> Either<L, R> left(@NonNull L value) {
        return new Either<>(value, null);
    }
    /**
     * Creates a Right Either.
     * @param value The right value
     * @param <L> The type of the left value
     * @param <R> The type of the right value
     * @return An Either where {@link #isRight()} returns {@code true}
     */
    public static <L, R> Either<L, R> right(@NonNull R value) {
        return new Either<>(null, value);
    }

    /**
     * @return True if this is a Right, otherwise this is a Left
     */
    public boolean isRight() {
        return right != null;
    }

    /**
     * Gets the left value.
     * @return The left value if {@link #isRight()} is {@code false}
     * @throws IllegalStateException If this Either is not a Left
     */
    public @NonNull L getLeft() {
        if (left == null) {
            throw new IllegalStateException("No left value present");
        }
        return left;
    }
    /**
     * Gets the right value.
     * @return The right value if {@link #isRight()} is {@code true}
     * @throws IllegalStateException If this Either is not a Right
     */
    public @NonNull R getRight() {
        if (right == null) {
            throw new IllegalStateException("No right value present");
        }
        return right;
    }

    /**
     * Collapses this Either into a single value.
     * @param leftMapper If this is a Left, maps the value
     * @param rightMapper If this is a Right, maps the value
     * @param <T> The type of the single return value
     * @return Either the result of mapping the left or right values
     */
    public <T> T fold(@NonNull Function<? super L, ? extends T> leftMapper,
            @NonNull Function<? super R, ? extends T> rightMapper) {
        if (isRight()) {
            return rightMapper.apply(right);
        }
        return leftMapper.apply(left);
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Either)) {
            return false;
        }
        Either<?, ?> other = (Either<?, ?>) o;
        if (isRight()) {
            return other.isRight() && right.equals(other.right);
        }
        return !other.isRight() && left.equals(other.left);
    }
    @Override
    public int hashCode() {
        // LSB is 1 for left, 0 for right
        if (isRight()) {
            return right.hashCode() << 1;
        }
        return left.hashCode() << 1 + 1;
    }

    /**
     * Displays whether this Either is a Left or a Right and its corresponding value.
     * <br>Uses each value's {@link Object#toString()} method.
     */
    @Override
    public String toString() {
        return fold(l -> String.format("Left{%s}", l), r -> String.format("Right{%s}", r));
    }

}
