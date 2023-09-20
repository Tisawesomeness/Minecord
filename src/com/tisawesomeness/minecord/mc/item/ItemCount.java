package com.tisawesomeness.minecord.mc.item;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A specific number of Minecraft items. Item count may be negative.
 */
public class ItemCount {

    private final long itemCount;
    @Getter private final int stackSize;

    /**
     * Creates a new item count.
     * @param itemCount number of starting items
     * @param stackSize stack size of the item
     * @throws IllegalArgumentException if the stack size is not within 1-64
     */
    public ItemCount(long itemCount, int stackSize) {
        this.itemCount = itemCount;
        if (stackSize < 1 || 64 < stackSize) {
            throw new IllegalArgumentException("stackSize must be between 1 and 64 but was " + stackSize);
        }
        this.stackSize = stackSize;

    }

    /**
     * Creates a new item count with the added items and same stack size.
     * @param items number of items to add
     * @return new item count
     */
    public ItemCount addItems(long items) {
        return new ItemCount(itemCount + items, stackSize);
    }
    /**
     * Creates a new item count with the added stacks and same stack size.
     * @param stacks number of stacks to add
     * @return new item count
     */
    public ItemCount addStacks(long stacks) {
        return addItems(stackSize * stacks);
    }
    /**
     * Creates a new item count, adding the given number of containers, keeping the same stack size.
     * @param container container holding the items
     * @param count number of containers to add
     * @return new item count
     */
    public ItemCount addContainers(Container container, long count) {
        return addStacks(container.getSlots() * count);
    }

    /**
     * @return item count
     */
    public long getCount() {
        return itemCount;
    }

    /**
     * Gets the exact number of containers needed to hold this item count.
     * @param container container holding the items
     * @return number of containers, possibly fractional
     */
    public double getExact(Container container) {
        return (double) itemCount / (stackSize * container.getSlots());
    }

    /**
     * Computes a combination of containers that holds this item count.
     * Containers with higher capacities are prioritized.
     * <br>Example: 1863 items is equal to 1 chest, 2 stacks, 7 items.
     * @param containers containers allowed to be used
     * @return list of length {@code containers.size() + 1}, each element is the amount of containers necessary in
     * descending order, and one extra element at the end for leftover items (above example would return
     * {@code [1, 2, 7]})
     */
    public List<Long> distribute(Collection<Container> containers) {
        long stacks = itemCount / stackSize;
        long items = itemCount % stackSize;

        List<Long> list = new ArrayList<>();
        for (int i = Container.values().length - 1; i >= 0; i--) {
            Container container = Container.values()[i];
            if (containers.contains(container)) {

                list.add(stacks / container.getSlots());
                stacks %= container.getSlots();

            }
        }

        list.add(items);
        return list;
    }

}
