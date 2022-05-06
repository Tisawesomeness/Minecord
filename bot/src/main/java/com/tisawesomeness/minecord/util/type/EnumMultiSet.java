package com.tisawesomeness.minecord.util.type;

import org.apache.commons.collections4.multiset.AbstractMapMultiSet;

import java.util.Collection;
import java.util.EnumMap;

/**
 * A multiset that uses an {@link EnumMap} to improve performance.
 * @param <E> The enum type held in the multiset
 */
public class EnumMultiSet<E extends Enum<E>> extends AbstractMapMultiSet<E> {

    /**
     * Constructs an empty {@link EnumMultiSet}.
     * @param clazz The enum class
     */
    public EnumMultiSet(Class<E> clazz) {
        super(new EnumMap<>(clazz));
    }

    /**
     * Constructs a multiset containing all the members of the given collection.
     * @param coll a collection to copy into this multiset
     * @throws IllegalArgumentException If the provided collection is empty
     */
    public EnumMultiSet(final Collection<? extends E> coll) {
        this(getClazz(coll));
        addAll(coll);
    }
    private static <E extends Enum<E>> Class<E> getClazz(final Collection<? extends E> coll) {
        if (coll.isEmpty()) {
            throw new IllegalArgumentException("Specified coll is empty");
        }
        return coll.iterator().next().getDeclaringClass();
    }

}
