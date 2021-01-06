package com.tisawesomeness.minecord.util;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility functions that don't fit anywhere else.
 */
public final class MiscUtils {
    private MiscUtils() {}

    /**
     * Sorts a collection into a new list.
     * @param list The collection
     * @param <T> The type of the collection, must be comparable
     * @return An unmodifiable sorted list
     */
    public static <T extends Comparable<? super T>> List<T> sort(Collection<? extends T> list) {
        return list.stream().sorted().collect(Collectors.toList());
    }

    /**
     * Sorts a collection into a new list using the given comparator.
     * @param list The collection
     * @param comparator The comparator to use for sorting
     * @param <T> The type of the collection
     * @return An unmodifiable sorted list
     */
    public static <T> List<T> sort(Collection<? extends T> list, Comparator<? super T> comparator) {
        return list.stream().sorted(comparator).collect(Collectors.toList());
    }

}
