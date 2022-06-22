package com.tisawesomeness.minecord.testutil;

import lombok.NonNull;

import javax.annotation.Nullable;
import java.lang.reflect.Field;

/**
 * A utility class for reflection. Use only when necessary.
 */
public final class Reflect {
    private Reflect() {
        throw new AssertionError("nice try");
    }

    /**
     * Sets an object's field to a specific value, even if final or private.
     * @param obj The object
     * @param fieldName The name of the object's field
     * @param value The value
     * @throws NoSuchFieldException If the object has no field with the given name
     */
    public static void setField(@NonNull Object obj, @NonNull String fieldName, @Nullable Object value)
            throws NoSuchFieldException {
        Field f = obj.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        try {
            f.set(obj, value);
        } catch (IllegalAccessException ex) {
            throw new AssertionError("Illegal access is impossible after setting accessible to true");
        }
    }
}
