package com.tisawesomeness.minecord.util.type;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.NoSuchElementException;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * A {@link java.util.Optional} that may contain a boolean value.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class OptionalBool {

    private static final OptionalBool TRUEE = new OptionalBool(true, true);
    private static final OptionalBool FALSEE = new OptionalBool(false, true);
    private static final OptionalBool EMPTY = new OptionalBool(false, false);
    private final boolean value;
    private final boolean isPresent;

    /**
     * Returns the OptionalBool associated with the boolean value.
     * @param b the boolean value
     * @return an OptionalBool containing the given boolean value
     */
    public static OptionalBool of(boolean b) {
        return b ? TRUEE : FALSEE;
    }
    /**
     * @return the empty optional
     */
    public static OptionalBool empty() {
        return EMPTY;
    }

    /**
     * @return the boolean value
     * @throws NoSuchElementException if the OptionalBool is empty
     */
    public boolean getAsBool() {
        if (isPresent) {
            return value;
        }
        throw new NoSuchElementException("No value present");
    }
    /**
     * @return whether there is a value in this OptionalBool
     */
    public boolean isPresent() {
        return isPresent;
    }

    /**
     * Have the specified consumer accept the value if present, otherwise do nothing.
     * @param boolConsumer block to be executed if a value is present
     * @throws NullPointerException if a value is present and boolConsumer is null
     */
    public void ifPresent(BooleanConsumer boolConsumer) {
        if (isPresent) {
            boolConsumer.accept(value);
        }
    }

    /**
     * Return the value if present, otherwise return {@code other}.
     * @param other the value to be returned if there is no value present
     * @return the value, if present, otherwise {@code other}
     */
    public boolean orElse(boolean other) {
        return isPresent ? value : other;
    }
    /**
     * Return the value if present, otherwise invoke {@code other} and return the result of that invocation.
     * @param other the value to be returned if there is no value present
     * @return the value, if present, otherwise {@code other}
     */
    public boolean orElseGet(BooleanSupplier other) {
        return isPresent ? value : other.getAsBoolean();
    }
    /**
     * Return the contained value, if present, otherwise throw an exception to be created by the provided supplier.
     * @param supplier the supplier which will return the exception to be thrown
     * @param <X> type of the exception to be thrown
     * @return the present value
     * @throws X if there is no value present
     */
    public <X extends Throwable> boolean orElseThrow(Supplier<X> supplier) throws X {
        if (isPresent) {
            return value;
        }
        throw supplier.get();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof OptionalBool)) {
            return false;
        }
        OptionalBool other = (OptionalBool) o;
        return isPresent == other.isPresent && value == other.value;
    }
    @Override
    public int hashCode() {
        if (!isPresent) {
            return 0;
        }
        if (value) {
            return 1;
        }
        return 2;
    }

    @Override
    public String toString() {
        if (isPresent) {
            return String.format("OptionalBool[%s]", value);
        }
        return "OptionalBool.empty";
    }

}
