package com.tisawesomeness.minecord.command;

import com.tisawesomeness.minecord.command.meta.CommandContext;
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
        String[] args = {};
        CommandContext ctx = new DummyContext(args);
        assertThat(ctx.joinArgs()).isEmpty();
    }
    @Test
    @DisplayName("Joining one arg gives the same arg")
    public void testJoinArgsSingle() {
        String[] args = {"one"};
        CommandContext ctx = new DummyContext(args);
        assertThat(ctx.joinArgs()).isEqualTo("one");
    }
    @Test
    @DisplayName("Joining args works with multiple args")
    public void testJoinArgs() {
        String[] args = {"one", "two", "three"};
        CommandContext ctx = new DummyContext(args);
        assertThat(ctx.joinArgs()).isEqualTo("one two three");
    }

    @Test
    @DisplayName("Joining no args starting at 0 gives an empty string")
    public void testJoinArgsSliceEmptyFrom0() {
        String[] args = {};
        CommandContext ctx = new DummyContext(args);
        assertThat(ctx.joinArgsSlice(0)).isEmpty();
    }
    @Test
    @DisplayName("Joining no args starting at 1 gives an empty string")
    public void testJoinArgsSliceEmptyFrom1() {
        String[] args = {};
        CommandContext ctx = new DummyContext(args);
        assertThat(ctx.joinArgsSlice(1)).isEmpty();
    }
    @Test
    @DisplayName("Joining one arg starting at 0 gives the same arg")
    public void testJoinArgsSliceSingleFrom0() {
        String[] args = {"one"};
        CommandContext ctx = new DummyContext(args);
        assertThat(ctx.joinArgsSlice(0)).isEqualTo("one");
    }
    @Test
    @DisplayName("Joining one arg starting at 1 gives an empty string")
    public void testJoinArgsSliceSingleFrom1() {
        String[] args = {"one"};
        CommandContext ctx = new DummyContext(args);
        assertThat(ctx.joinArgsSlice(1)).isEmpty();
    }
    @Test
    @DisplayName("Joining args starting at 0 works")
    public void testJoinArgsSliceFrom0() {
        String[] args = {"one", "two", "three"};
        CommandContext ctx = new DummyContext(args);
        assertThat(ctx.joinArgsSlice(0)).isEqualTo("one two three");
    }
    @Test
    @DisplayName("Joining args starting at 1 works")
    public void testJoinArgsSliceFrom1() {
        String[] args = {"one", "two", "three"};
        CommandContext ctx = new DummyContext(args);
        assertThat(ctx.joinArgsSlice(1)).isEqualTo("two three");
    }
    @Test
    @DisplayName("Joining args starting at -1 throws IllegalArgumentException")
    public void testJoinArgsSliceFromNegativeThrows() {
        String[] args = {};
        CommandContext ctx = new DummyContext(args);
        assertThatThrownBy(() -> ctx.joinArgsSlice(-1)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Joining no args ending at 0 gives an empty string")
    public void testJoinArgsSliceEmptyTo0() {
        String[] args = {};
        CommandContext ctx = new DummyContext(args);
        assertThat(ctx.joinArgsSlice(0, 0)).isEmpty();
    }
    @Test
    @DisplayName("Joining no args ending at 1 gives an empty string")
    public void testJoinArgsSliceEmptyTo1() {
        String[] args = {};
        CommandContext ctx = new DummyContext(args);
        assertThat(ctx.joinArgsSlice(0, 1)).isEmpty();
    }
    @Test
    @DisplayName("Joining one arg ending at 0 gives an empty string")
    public void testJoinArgsSliceSingleTo0() {
        String[] args = {"one"};
        CommandContext ctx = new DummyContext(args);
        assertThat(ctx.joinArgsSlice(0, 0)).isEmpty();
    }
    @Test
    @DisplayName("Joining one arg ending at 1 gives the same arg")
    public void testJoinArgsSliceSingleTo1() {
        String[] args = {"one"};
        CommandContext ctx = new DummyContext(args);
        assertThat(ctx.joinArgsSlice(0, 1)).isEqualTo("one");
    }
    @Test
    @DisplayName("Joining args ending at 0 gives an empty string")
    public void testJoinArgsSliceTo0() {
        String[] args = {"one", "two", "three"};
        CommandContext ctx = new DummyContext(args);
        assertThat(ctx.joinArgsSlice(0, 0)).isEmpty();
    }
    @Test
    @DisplayName("Joining args ending at 1 works")
    public void testJoinArgsSliceTo1() {
        String[] args = {"one", "two", "three"};
        CommandContext ctx = new DummyContext(args);
        assertThat(ctx.joinArgsSlice(0, 1)).isEqualTo("one");
    }
    @Test
    @DisplayName("Joining args with range 1 works")
    public void testJoinArgsSliceRange1() {
        String[] args = {"one", "two", "three"};
        CommandContext ctx = new DummyContext(args);
        assertThat(ctx.joinArgsSlice(1, 2)).isEqualTo("two");
    }
    @Test
    @DisplayName("Joining args with range 2 works")
    public void testJoinArgsSliceRange2() {
        String[] args = {"one", "two", "three", "four"};
        CommandContext ctx = new DummyContext(args);
        assertThat(ctx.joinArgsSlice(1, 3)).isEqualTo("two three");
    }
    @Test
    @DisplayName("Joining args starting and ending at negative throws IllegalArgumentException")
    public void testJoinArgsSliceFromToNegativeThrows() {
        String[] args = {};
        CommandContext ctx = new DummyContext(args);
        assertThatThrownBy(() -> ctx.joinArgsSlice(-2, -1)).isInstanceOf(IllegalArgumentException.class);
    }
    @Test
    @DisplayName("Joining args with negative range throws IllegalArgumentException")
    public void testJoinArgsSliceNegativeRangeThrows() {
        String[] args = {};
        CommandContext ctx = new DummyContext(args);
        assertThatThrownBy(() -> ctx.joinArgsSlice(2, 1)).isInstanceOf(IllegalArgumentException.class);
    }

    @RequiredArgsConstructor
    private static class DummyContext extends AbstractContext {
        // Overrides getter
        @Getter private final String[] args;
    }

}
