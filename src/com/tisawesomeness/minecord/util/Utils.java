package com.tisawesomeness.minecord.util;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class Utils {
    private Utils() {}

    public static <T, R> @Nullable R mapNullable(@Nullable T obj, Function<? super T, ? extends R> mapper) {
        return obj == null ? null : mapper.apply(obj);
    }
    public static <T1, T2, R> @Nullable R mapNullable(@Nullable T1 obj, Function<? super T1, ? extends T2> mapper1,
                                                    Function<? super T2, ? extends R> mapper2) {
        return mapNullable(mapNullable(obj, mapper1), mapper2);
    }

    public static <T> List<T> flatten(Collection<? extends Collection<T>> list) {
        return list.stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

}
