package com.tisawesomeness.minecord.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class StringUtilsTest {

    private static final List<String> TEST_INPUT = List.of("len4", "length7", "len4");

    @Test
    @DisplayName("Splitting a list of empty strings by length returns an empty list")
    public void testSplitEmptyList() {
        List<String> lines = Collections.emptyList();
        assertThat(StringUtils.partitionLinesByLength(lines, 1))
                .isEmpty();
    }

    @Test
    @DisplayName("Splitting a list of empty strings by length returns an empty list")
    public void testSplitSingleList() {
        List<String> lines = List.of("a");
        assertThat(StringUtils.partitionLinesByLength(lines, 1))
                .isEqualTo(lines);
    }

    @Test
    @DisplayName("Splitting a list of strings by length 0 returns the list unmodified")
    public void testSplitMaxLengthZero() {
        List<String> lines = List.of("a", "b", "c");
        assertThat(StringUtils.partitionLinesByLength(lines, 0))
                .isEqualTo(lines);
    }

    @Test
    @DisplayName("Splitting a list of strings by length accounts for newlines properly")
    public void testSplitGrouping_Case1() {
        assertThat(StringUtils.partitionLinesByLength(TEST_INPUT, 11))
                .containsExactly("len4", "length7", "len4");
    }

    @Test
    @DisplayName("Splitting a list of strings by length accounts for newlines properly")
    public void testSplitGrouping_Case2() {
        assertThat(StringUtils.partitionLinesByLength(TEST_INPUT, 12))
                .containsExactly("len4\nlength7", "len4");
    }

    @Test
    @DisplayName("Splitting a list of strings by length accounts for newlines properly")
    public void testSplitGrouping_Case3() {
        assertThat(StringUtils.partitionLinesByLength(TEST_INPUT, 13))
                .containsExactly("len4\nlength7", "len4");
    }

    @Test
    @DisplayName("Splitting a list of strings by length accounts for newlines properly")
    public void testSplitGrouping_Case4() {
        assertThat(StringUtils.partitionLinesByLength(TEST_INPUT, 15))
                .containsExactly("len4\nlength7", "len4");
    }

    @Test
    @DisplayName("Splitting a list of strings by length accounts for newlines properly")
    public void testSplitGrouping_Case5() {
        assertThat(StringUtils.partitionLinesByLength(TEST_INPUT, 16))
                .containsExactly("len4\nlength7", "len4");
    }

    @Test
    @DisplayName("Splitting a list of strings by length accounts for newlines properly")
    public void testSplitGrouping_Case6() {
        assertThat(StringUtils.partitionLinesByLength(TEST_INPUT, 17))
                .containsExactly("len4\nlength7\nlen4");
    }

    @Test
    @DisplayName("Splitting a list of strings by length with a string over max length" +
            " leaves that string in a group by itself")
    public void testSplitGroupingOverfill() {
        List<String> lines = List.of("<", "len4", ">");
        assertThat(StringUtils.partitionLinesByLength(lines, 3))
                .isEqualTo(lines);
    }

    @Test
    @DisplayName("Splitting a list of strings by length accounts for empty strings properly")
    public void testSplitEmptyString_Case1() {
        List<String> lines = List.of("abc", "", "def", "ghi", "");
        assertThat(StringUtils.partitionLinesByLength(lines, 7))
                .containsExactly("abc\n", "def\nghi", "");
    }

    @Test
    @DisplayName("Splitting a list of strings by length accounts for empty strings properly")
    public void testSplitEmptyString_Case2() {
        List<String> lines = List.of("", "abc", "def");
        assertThat(StringUtils.partitionLinesByLength(lines, 5))
                .containsExactly("\nabc", "def");
    }

    @Test
    @DisplayName("Splitting a list of strings by length accounts for empty strings properly")
    public void testSplitEmptyString_Case3() {
        List<String> lines = List.of("abc", "", "", "", "", "", "def");
        assertThat(StringUtils.partitionLinesByLength(lines, 5))
                .containsExactly("abc\n\n", "\n\n", "def");
    }

    @Test
    @DisplayName("Splitting a list of strings by length works with a custom joiner")
    public void testSplitWithJoiner() {
        assertThat(StringUtils.partitionByLength(TEST_INPUT, " ", 16))
                .containsExactly("len4 length7", "len4");
    }

    @Test
    @DisplayName("Splitting a list of strings by length works with an empty string joiner")
    public void testSplitWithEmptyJoiner() {
        assertThat(StringUtils.partitionByLength(TEST_INPUT, "", 16))
                .containsExactly("len4length7len4");
    }

    @Test
    @DisplayName("Splitting a list of strings by length works with a multi-char joiner")
    public void testSplitWithLongJoiner() {
        List<String> lines = List.of("len4", "len4", "length7");
        assertThat(StringUtils.partitionByLength(lines, "---", 16))
                .containsExactly("len4---len4", "length7");
    }

    @Test
    @DisplayName("Splitting a list of empty strings by a negative length throws IllegalArgumentException")
    public void testSplitNegativeMaxLength() {
        List<String> lines = List.of("a", "b", "c");
        assertThatThrownBy(() -> StringUtils.partitionLinesByLength(lines, -1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Splitting a list with null strings throws NullPointerException")
    public void testSplitNullStrings_Case1() {
        List<String> lines = new ArrayList<>();
        lines.add(null);
        assertThatThrownBy(() -> StringUtils.partitionLinesByLength(lines, 7))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Splitting a list with null strings throws NullPointerException")
    public void testSplitNullStrings_Case2() {
        List<String> lines = new ArrayList<>();
        lines.add("abc");
        lines.add("def");
        lines.add(null);
        assertThatThrownBy(() -> StringUtils.partitionLinesByLength(lines, 7))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Splitting a list with a null joiner throws NullPointerException")
    public void testSplitNullJoiner() {
        assertThatThrownBy(() -> StringUtils.partitionByLength(TEST_INPUT, null, 7))
                .isInstanceOf(NullPointerException.class);
    }

}
