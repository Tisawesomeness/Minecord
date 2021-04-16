package com.tisawesomeness.minecord.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class StringsTest {

    private static final List<String> TEST_INPUT = Lists.of("len4", "length7", "len4");

    @Test
    @DisplayName("Repeating a string 0 times gives an empty string")
    public void testRepeat0() {
        assertThat(Strings.repeat("test", 0)).isEmpty();
    }
    @Test
    @DisplayName("Repeating a string 1 time gives the same string")
    public void testRepeat1() {
        assertThat(Strings.repeat("test", 1)).isEqualTo("test");
    }
    @Test
    @DisplayName("Repeating a string works")
    public void testRepeat() {
        assertThat(Strings.repeat("test", 3)).isEqualTo("testtesttest");
    }

    @Test
    @DisplayName("A substring starting at -1 is the whole string")
    public void testSafeSubstringN1() {
        assertThat(Strings.safeSubstring("abc", -1)).isEqualTo("abc");
    }
    @Test
    @DisplayName("A substring starting at 0 is the whole string")
    public void testSafeSubstring0() {
        assertThat(Strings.safeSubstring("abc", 0)).isEqualTo("abc");
    }
    @Test
    @DisplayName("A substring starting at 1 slices correctly")
    public void testSafeSubstring1() {
        assertThat(Strings.safeSubstring("abc", 1)).isEqualTo("bc");
    }
    @Test
    @DisplayName("A substring starting at string length is empty")
    public void testSafeSubstringMax() {
        assertThat(Strings.safeSubstring("abc", 3)).isEmpty();
    }
    @Test
    @DisplayName("A substring starting past string length is empty")
    public void testSafeSubstringOver() {
        assertThat(Strings.safeSubstring("abc", 4)).isEmpty();
    }
    @Test
    @DisplayName("A substring ending at -1 is empty")
    public void testSafeSubstring0toN1() {
        assertThat(Strings.safeSubstring("abc", 0, -1)).isEmpty();
    }
    @Test
    @DisplayName("A substring ending at 0 is empty")
    public void testSafeSubstring0to0() {
        assertThat(Strings.safeSubstring("abc", 0, 0)).isEmpty();
    }
    @Test
    @DisplayName("A substring ending at 1 is slices correctly")
    public void testSafeSubstring0to1() {
        assertThat(Strings.safeSubstring("abc", 0, 1)).isEqualTo("a");
    }
    @Test
    @DisplayName("A substring ending at string length is the whole string")
    public void testSafeSubstring0toMax() {
        assertThat(Strings.safeSubstring("abc", 0, 3)).isEqualTo("abc");
    }
    @Test
    @DisplayName("A substring ending past string length is the whole string")
    public void testSafeSubstring0toOver() {
        assertThat(Strings.safeSubstring("abc", 0, 4)).isEqualTo("abc");
    }
    @Test
    @DisplayName("A substring starting at 1 and ending at 2 slices correctly")
    public void testSafeSubstring1to2() {
        assertThat(Strings.safeSubstring("abc", 1, 2)).isEqualTo("b");
    }
    @Test
    @DisplayName("A substring starting at 2 and ending at 1 is empty")
    public void testSafeSubstring2to1() {
        assertThat(Strings.safeSubstring("abc", 2, 1)).isEmpty();
    }

    @Test
    @DisplayName("Splitting a list of empty strings by length returns an empty list")
    public void testSplitEmptyList() {
        List<String> lines = Collections.emptyList();
        assertThat(Strings.partitionLinesByLength(lines, 1))
                .isEmpty();
    }
    @Test
    @DisplayName("Splitting a list of empty strings by length returns an empty list")
    public void testSplitSingleList() {
        List<String> lines = Lists.of("a");
        assertThat(Strings.partitionLinesByLength(lines, 1))
                .isEqualTo(lines);
    }
    @Test
    @DisplayName("Splitting a list of strings by length 0 returns the list unmodified")
    public void testSplitMaxLengthZero() {
        List<String> lines = Lists.of("a", "b", "c");
        assertThat(Strings.partitionLinesByLength(lines, 0))
                .isEqualTo(lines);
    }

    @Test
    @DisplayName("Splitting a list of strings by length accounts for newlines properly")
    public void testSplitGrouping_Case1() {
        assertThat(Strings.partitionLinesByLength(TEST_INPUT, 11))
                .containsExactly("len4", "length7", "len4");
    }
    @Test
    @DisplayName("Splitting a list of strings by length accounts for newlines properly")
    public void testSplitGrouping_Case2() {
        assertThat(Strings.partitionLinesByLength(TEST_INPUT, 12))
                .containsExactly("len4\nlength7", "len4");
    }
    @Test
    @DisplayName("Splitting a list of strings by length accounts for newlines properly")
    public void testSplitGrouping_Case3() {
        assertThat(Strings.partitionLinesByLength(TEST_INPUT, 13))
                .containsExactly("len4\nlength7", "len4");
    }
    @Test
    @DisplayName("Splitting a list of strings by length accounts for newlines properly")
    public void testSplitGrouping_Case4() {
        assertThat(Strings.partitionLinesByLength(TEST_INPUT, 15))
                .containsExactly("len4\nlength7", "len4");
    }
    @Test
    @DisplayName("Splitting a list of strings by length accounts for newlines properly")
    public void testSplitGrouping_Case5() {
        assertThat(Strings.partitionLinesByLength(TEST_INPUT, 16))
                .containsExactly("len4\nlength7", "len4");
    }
    @Test
    @DisplayName("Splitting a list of strings by length accounts for newlines properly")
    public void testSplitGrouping_Case6() {
        assertThat(Strings.partitionLinesByLength(TEST_INPUT, 17))
                .containsExactly("len4\nlength7\nlen4");
    }
    @Test
    @DisplayName("Splitting a list of strings by length with a string over max length" +
            " leaves that string in a group by itself")
    public void testSplitGroupingOverfill() {
        List<String> lines = Lists.of("<", "len4", ">");
        assertThat(Strings.partitionLinesByLength(lines, 3))
                .isEqualTo(lines);
    }

    @Test
    @DisplayName("Splitting a list of strings by length accounts for empty strings properly")
    public void testSplitEmptyString_Case1() {
        List<String> lines = Lists.of("abc", "", "def", "ghi", "");
        assertThat(Strings.partitionLinesByLength(lines, 7))
                .containsExactly("abc\n", "def\nghi", "");
    }
    @Test
    @DisplayName("Splitting a list of strings by length accounts for empty strings properly")
    public void testSplitEmptyString_Case2() {
        List<String> lines = Lists.of("", "abc", "def");
        assertThat(Strings.partitionLinesByLength(lines, 5))
                .containsExactly("\nabc", "def");
    }
    @Test
    @DisplayName("Splitting a list of strings by length accounts for empty strings properly")
    public void testSplitEmptyString_Case3() {
        List<String> lines = Lists.of("abc", "", "", "", "", "", "def");
        assertThat(Strings.partitionLinesByLength(lines, 5))
                .containsExactly("abc\n\n", "\n\n", "def");
    }

    @Test
    @DisplayName("Splitting a list of strings by length works with a custom joiner")
    public void testSplitWithJoiner() {
        assertThat(Strings.partitionByLength(TEST_INPUT, " ", 16))
                .containsExactly("len4 length7", "len4");
    }
    @Test
    @DisplayName("Splitting a list of strings by length works with an empty string joiner")
    public void testSplitWithEmptyJoiner() {
        assertThat(Strings.partitionByLength(TEST_INPUT, "", 16))
                .containsExactly("len4length7len4");
    }
    @Test
    @DisplayName("Splitting a list of strings by length works with a multi-char joiner")
    public void testSplitWithLongJoiner() {
        List<String> lines = Lists.of("len4", "len4", "length7");
        assertThat(Strings.partitionByLength(lines, "---", 16))
                .containsExactly("len4---len4", "length7");
    }

    @Test
    @DisplayName("Splitting a list of empty strings by a negative length throws IllegalArgumentException")
    public void testSplitNegativeMaxLength() {
        List<String> lines = Lists.of("a", "b", "c");
        assertThatThrownBy(() -> Strings.partitionLinesByLength(lines, -1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Splitting a list with null strings throws NullPointerException")
    public void testSplitNullStrings_Case1() {
        List<String> lines = new ArrayList<>();
        lines.add(null);
        assertThatThrownBy(() -> Strings.partitionLinesByLength(lines, 7))
                .isInstanceOf(NullPointerException.class);
    }
    @Test
    @DisplayName("Splitting a list with null strings throws NullPointerException")
    public void testSplitNullStrings_Case2() {
        List<String> lines = new ArrayList<>();
        lines.add("abc");
        lines.add("def");
        lines.add(null);
        assertThatThrownBy(() -> Strings.partitionLinesByLength(lines, 7))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Splitting a list with a null joiner throws NullPointerException")
    public void testSplitNullJoiner() {
        assertThatThrownBy(() -> Strings.partitionByLength(TEST_INPUT, null, 7))
                .isInstanceOf(NullPointerException.class);
    }

}
