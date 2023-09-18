package com.tisawesomeness.minecord.mc.item;

import com.tisawesomeness.minecord.util.Mth;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.EnumSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

public class ItemCountTest {

    @ParameterizedTest
    @ValueSource(longs = {0, 1, 5, 65})
    public void testNew(long count) {
        assertThat(new ItemCount(count, 64).getCount())
                .isEqualTo(count);
    }
    @ParameterizedTest
    @ValueSource(ints = {0, 65})
    public void testNewInvalid(int stackSize) {
        assertThatThrownBy(() -> new ItemCount(0, stackSize))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1, 0, 1, 65})
    public void testAddItems(long count) {
        assertThat(new ItemCount(5, 64).addItems(count).getCount())
                .isEqualTo(5 + count);
    }
    @ParameterizedTest
    @CsvSource({
            "0, 64, 1, 64",
            "2, 1, 5, 7",
            "37, 16, 5, 117",
            "15, 64, -2, -113"
    })
    public void testAddStacks(long initialCount, int stackSize, long stacks, long expectedCount) {
        assertThat(new ItemCount(initialCount, stackSize).addStacks(stacks).getCount())
                .isEqualTo(expectedCount);
    }
    @ParameterizedTest
    @CsvSource({
            "0, 64, STACK, 1, 64",
            "0, 64, CHEST, -2, -3456",
            "7, 16, DOUBLE_CHEST, 5, 4327",
            "-165, 64, CHEST_SHULKER, 1, 46491",
            "9164, 64, DOUBLE_CHEST_SHULKER, 3, 289100"
    })
    public void testAddContainers(long initialCount, int stackSize, Container container, long count, long expectedCount) {
        assertThat(new ItemCount(initialCount, stackSize).addContainers(container, count).getCount())
                .isEqualTo(expectedCount);
    }

    @ParameterizedTest
    @CsvSource({
            "0, 64, STACK, 0.0",
            "32, 64, STACK, 0.5",
            "63, 64, STACK, 0.984375",
            "64, 64, STACK, 1.0",
            "128, 64, STACK, 2.0",
            "-63, 64, STACK, -0.984375",
            "2, 16, STACK, 0.125",
            "83, 1, STACK, 83.0",
            "27000, 64, CHEST, 15.625",
            "27000, 64, DOUBLE_CHEST, 7.8125",
            "139968, 64, CHEST_SHULKER, 3.0",
            "93312, 64, DOUBLE_CHEST_SHULKER, 1.0"
    })
    public void testExact(long count, int stackSize, Container container, double expected) {
        assertThat(new ItemCount(count, stackSize).getExact(container))
                .isCloseTo(expected, within(Mth.EPSILON));
    }

    @ParameterizedTest
    @CsvSource({
            "0, 64, 0, 0",
            "2, 64, 0, 2",
            "2, 1, 2, 0",
            "69, 64, 1, 5",
            "1863, 64, 29, 7",
            "-1863, 64, -29, -7"
    })
    public void testDistributeStack(long count, int stackSize, long stacks, long items) {
        assertThat(new ItemCount(count, stackSize).distribute(EnumSet.of(Container.STACK)))
                .containsExactly(stacks, items);
    }
    @ParameterizedTest
    @CsvSource({
            "0, 64, 0, 0, 0",
            "27, 1, 1, 0, 0",
            "1863, 64, 1, 2, 7",
            "-1863, 64, -1, -2, -7"
    })
    public void testDistributeChest(long count, int stackSize, long chests, long stacks, long items) {
        assertThat(new ItemCount(count, stackSize).distribute(EnumSet.of(Container.STACK, Container.CHEST)))
                .containsExactly(chests, stacks, items);
    }
    @ParameterizedTest
    @CsvSource({
            "0, 64, 0, 0, 0, 0, 0, 0",
            "99999, 64, 1, 0, 1, 1, 23, 31",
            "-99999, 64, -1, 0, -1, -1, -23, -31"
    })
    public void testDistributeAll(long count, int stackSize, long doubleChestShulkers, long chestShulkers,
                                  long doubleChests, long chests, long stacks, long items) {
        Set<Container> containers = EnumSet.of(Container.STACK, Container.CHEST, Container.DOUBLE_CHEST,
                Container.CHEST_SHULKER, Container.DOUBLE_CHEST_SHULKER);
        assertThat(new ItemCount(count, stackSize).distribute(containers))
                .containsExactly(doubleChestShulkers, chestShulkers, doubleChests, chests, stacks, items);
    }

}
