package com.tisawesomeness.minecord.command.meta;

import com.tisawesomeness.minecord.testutil.runner.AbstractContext;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CommandContextTest {

    @Test
    @DisplayName("Joining no args gives an empty string")
    public void testJoinArgsEmpty() {
        CommandContext ctx = new DummyArgsContext();
        assertThat(ctx.joinArgs()).isEmpty();
    }
    @Test
    @DisplayName("Joining one arg gives the same arg")
    public void testJoinArgsSingle() {
        CommandContext ctx = new DummyArgsContext("one");
        assertThat(ctx.joinArgs()).isEqualTo("one");
    }
    @Test
    @DisplayName("Joining args works with multiple args")
    public void testJoinArgs() {
        CommandContext ctx = new DummyArgsContext("one", "two", "three");
        assertThat(ctx.joinArgs()).isEqualTo("one two three");
    }

    @Test
    @DisplayName("Joining no args starting at 0 gives an empty string")
    public void testJoinArgsSliceEmptyFrom0() {
        CommandContext ctx = new DummyArgsContext();
        assertThat(ctx.joinArgsSlice(0)).isEmpty();
    }
    @Test
    @DisplayName("Joining no args starting at 1 gives an empty string")
    public void testJoinArgsSliceEmptyFrom1() {
        CommandContext ctx = new DummyArgsContext();
        assertThat(ctx.joinArgsSlice(1)).isEmpty();
    }
    @Test
    @DisplayName("Joining one arg starting at 0 gives the same arg")
    public void testJoinArgsSliceSingleFrom0() {
        CommandContext ctx = new DummyArgsContext("one");
        assertThat(ctx.joinArgsSlice(0)).isEqualTo("one");
    }
    @Test
    @DisplayName("Joining one arg starting at 1 gives an empty string")
    public void testJoinArgsSliceSingleFrom1() {
        CommandContext ctx = new DummyArgsContext("one");
        assertThat(ctx.joinArgsSlice(1)).isEmpty();
    }
    @Test
    @DisplayName("Joining args starting at 0 works")
    public void testJoinArgsSliceFrom0() {
        CommandContext ctx = new DummyArgsContext("one", "two", "three");
        assertThat(ctx.joinArgsSlice(0)).isEqualTo("one two three");
    }
    @Test
    @DisplayName("Joining args starting at 1 works")
    public void testJoinArgsSliceFrom1() {
        CommandContext ctx = new DummyArgsContext("one", "two", "three");
        assertThat(ctx.joinArgsSlice(1)).isEqualTo("two three");
    }
    @Test
    @DisplayName("Joining args starting at -1 throws IllegalArgumentException")
    public void testJoinArgsSliceFromNegativeThrows() {
        CommandContext ctx = new DummyArgsContext();
        assertThatThrownBy(() -> ctx.joinArgsSlice(-1)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Joining no args ending at 0 gives an empty string")
    public void testJoinArgsSliceEmptyTo0() {
        CommandContext ctx = new DummyArgsContext();
        assertThat(ctx.joinArgsSlice(0, 0)).isEmpty();
    }
    @Test
    @DisplayName("Joining no args ending at 1 gives an empty string")
    public void testJoinArgsSliceEmptyTo1() {
        CommandContext ctx = new DummyArgsContext();
        assertThat(ctx.joinArgsSlice(0, 1)).isEmpty();
    }
    @Test
    @DisplayName("Joining one arg ending at 0 gives an empty string")
    public void testJoinArgsSliceSingleTo0() {
        String[] args = {"one"};
        CommandContext ctx = new DummyArgsContext(args);
        assertThat(ctx.joinArgsSlice(0, 0)).isEmpty();
    }
    @Test
    @DisplayName("Joining one arg ending at 1 gives the same arg")
    public void testJoinArgsSliceSingleTo1() {
        CommandContext ctx = new DummyArgsContext("one");
        assertThat(ctx.joinArgsSlice(0, 1)).isEqualTo("one");
    }
    @Test
    @DisplayName("Joining args ending at 0 gives an empty string")
    public void testJoinArgsSliceTo0() {
        CommandContext ctx = new DummyArgsContext("one", "two", "three");
        assertThat(ctx.joinArgsSlice(0, 0)).isEmpty();
    }
    @Test
    @DisplayName("Joining args ending at 1 works")
    public void testJoinArgsSliceTo1() {
        CommandContext ctx = new DummyArgsContext("one", "two", "three");
        assertThat(ctx.joinArgsSlice(0, 1)).isEqualTo("one");
    }
    @Test
    @DisplayName("Joining args with range 1 works")
    public void testJoinArgsSliceRange1() {
        CommandContext ctx = new DummyArgsContext("one", "two", "three");
        assertThat(ctx.joinArgsSlice(1, 2)).isEqualTo("two");
    }
    @Test
    @DisplayName("Joining args with range 2 works")
    public void testJoinArgsSliceRange2() {
        CommandContext ctx = new DummyArgsContext("one", "two", "three", "four");
        assertThat(ctx.joinArgsSlice(1, 3)).isEqualTo("two three");
    }
    @Test
    @DisplayName("Joining args starting and ending at negative throws IllegalArgumentException")
    public void testJoinArgsSliceFromToNegativeThrows() {
        CommandContext ctx = new DummyArgsContext();
        assertThatThrownBy(() -> ctx.joinArgsSlice(-2, -1)).isInstanceOf(IllegalArgumentException.class);
    }
    @Test
    @DisplayName("Joining args with negative range throws IllegalArgumentException")
    public void testJoinArgsSliceNegativeRangeThrows() {
        CommandContext ctx = new DummyArgsContext();
        assertThatThrownBy(() -> ctx.joinArgsSlice(2, 1)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("getPrefix() does not modify the raw prefix `&`")
    public void testGetPrefix() {
        CommandContext ctx = new DummyPrefixContext("&");
        assertThat(ctx.getPrefix()).isEqualTo("&");
    }
    @Test
    @DisplayName("getPrefix() adds an extra space to raw prefix `mc`")
    public void testGetPrefixSpace() {
        CommandContext ctx = new DummyPrefixContext("mc");
        assertThat(ctx.getPrefix()).isEqualTo("mc ");
    }

    private static class DummyArgsContext extends AbstractContext {
        // Overrides getter
        @Getter private final String[] args;
        public DummyArgsContext(String... args) {
            this.args = args;
        }
    }

    @RequiredArgsConstructor
    private static class DummyPrefixContext extends AbstractContext {
        // Overrides getter
        @Getter private final String rawPrefix;
    }

}
