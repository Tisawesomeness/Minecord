package com.tisawesomeness.minecord.util.type;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.AbstractListValuedMap;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;

/**
 * A list valued map that uses an {@link EnumMap} to improve performance.
 * @param <K> The enum type used as keys for the map
 * @param <V> The type used as values for the map
 */
public class ListValuedEnumMap<K extends Enum<K>, V> extends AbstractListValuedMap<K, V> {

    /**
     * The initial list capacity when using none specified in constructor.
     */
    private static final int DEFAULT_INITIAL_LIST_CAPACITY = 3;

    /**
     * The initial list capacity when creating a new value collection.
     */
    private final int initialListCapacity;

    /**
     * Creates an empty ListValuedEnumMap with the default initial
     * map capacity (16) and the default initial list capacity (3).
     */
    public ListValuedEnumMap(Class<K> clazz) {
        this(clazz, DEFAULT_INITIAL_LIST_CAPACITY);
    }

    /**
     * Creates an empty ListValuedEnumMap with the specified initial
     * map and list capacities.
     * @param clazz The enum class
     * @param initialListCapacity the initial capacity used for value collections
     */
    public ListValuedEnumMap(Class<K> clazz, int initialListCapacity) {
        super(new EnumMap<>(clazz));
        this.initialListCapacity = initialListCapacity;
    }

    /**
     * Creates an ListValuedEnumMap copying all the mappings of the given map.
     * @param map a <code>MultiValuedMap</code> to copy into this map
     */
    public ListValuedEnumMap(final MultiValuedMap<? extends K, ? extends V> map) {
        this(getClazz(map), DEFAULT_INITIAL_LIST_CAPACITY);
        super.putAll(map);
    }
    private static <K extends Enum<K>, V> Class<K> getClazz(final MultiValuedMap<? extends K, ? extends V> map) {
        if (map.isEmpty()) {
            throw new IllegalArgumentException("Specified map is empty");
        }
        return map.keySet().iterator().next().getDeclaringClass();
    }

    /**
     * Creates an ListValuedEnumMap copying all the mappings of the given map.
     * @param map a <code>Map</code> to copy into this map
     */
    public ListValuedEnumMap(final Map<? extends K, ? extends V> map) {
        this(getClazz(map), DEFAULT_INITIAL_LIST_CAPACITY);
        super.putAll(map);
    }
    private static <K extends Enum<K>, V> Class<K> getClazz(final Map<? extends K, ? extends V> map) {
        if (map.isEmpty()) {
            throw new IllegalArgumentException("Specified map is empty");
        }
        return map.keySet().iterator().next().getDeclaringClass();
    }

    protected ArrayList<V> createCollection() {
        return new ArrayList<>(initialListCapacity);
    }

    /**
     * Creates an empty list valued enum map.
     * @param clazz The enum class
     * @param <K> The enum type used as keys for the map
     * @param <V> The type used as values for the map
     * @return A new list valued enum map
     */
    public static <K extends Enum<K>, V> ListValuedEnumMap<K, V> create(Class<K> clazz) {
        return new ListValuedEnumMap<>(clazz);
    }

}
