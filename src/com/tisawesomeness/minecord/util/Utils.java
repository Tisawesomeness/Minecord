package com.tisawesomeness.minecord.util;

import javax.annotation.Nullable;
import java.util.function.Function;

public final class Utils {
    private Utils() {}

    public static <T, R> @Nullable R mapNullable(@Nullable T obj, Function<? super T, ? extends R> mapper) {
        return obj == null ? null : mapper.apply(obj);
    }

}
