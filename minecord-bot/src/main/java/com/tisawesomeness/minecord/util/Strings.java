package com.tisawesomeness.minecord.util;

import lombok.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

public final class Strings {
    private Strings() {}

    /**
     * Repeats a string a certain number of times.
     * @param str The string to repeat
     * @param n The number of times to repeat
     * @return The repeated string
     * @throws IllegalArgumentException If {@code n} is negative
     */
    public static @NonNull String repeat(@NonNull String str, int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Number of times to repeat must be non-negative, was " + n);
        }
        if (n == 0) {
            return "";
        }
        if (n == 1) {
            return str;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append(str);
        }
        return sb.toString();
    }

    /**
     * Safely substrings a string. The start index will be bound between 0 and the length of the string.
     * @param str The string to slice
     * @param beginIndex The starting substring index, inclusive
     * @return The sliced string
     * @see String#substring(int)
     */
    public static @NonNull String safeSubstring(@NonNull String str, int beginIndex) {
        return str.substring(Mth.clamp(beginIndex, 0, str.length()));
    }
    /**
     * Safely substrings a string. The start and end indices will be bound between 0 and the length of the string.
     * @param str The string to slice
     * @param beginIndex The starting substring index, inclusive
     * @param endIndex The ending substring index, exclusive
     * @return The sliced string
     * @see String#substring(int, int)
     */
    public static @NonNull String safeSubstring(@NonNull String str, int beginIndex, int endIndex) {
        if (beginIndex >= endIndex) {
            return "";
        }
        return str.substring(Math.max(beginIndex, 0), Math.min(endIndex, str.length()));
    }

    /**
     * Creates a new string with the character at the given index replaced with the replacement character.
     * @param str input string
     * @param idx index of character to replace
     * @param replacement character to replace with
     * @return new string
     */
    public static String replaceCharAt(String str, int idx, char replacement) {
        return str.substring(0, idx) + replacement + str.substring(idx + 1);
    }

    /**
     * Joins a list of lines into a series of partitions, ensuring that no line in the returned list
     * has a length over the max length (as long as every input line is no longer than the max length).
     * This is useful for transforming a single message (split into inseparable pieces) into multiple message that
     * satisfy all length limits.
     * <br>If the input list contains any line that is over the max length, it will appear in the output list
     * unmodified. Therefore, you should ensure that the input list has strings with the correct length.
     * @param lines The list of lines to partition
     * @param maxLength The max length of any string in the output list
     * @return A list of strings, where no string has a length over the max length
     * @throws IllegalArgumentException if the max length is negative
     * @throws NullPointerException if the list contains null
     */
    public static List<String> partitionLinesByLength(List<@NonNull String> lines, int maxLength) {
        return partitionByLength(lines, "\n", maxLength);
    }

    /**
     * Joins a list of strings into a series of partitions, ensuring that no string in the returned list
     * has a length over the max length (as long as every input string is no longer than the max length).
     * This is useful for transforming a single message (split into inseparable pieces) into multiple message that
     * satisfy all length limits.
     * <br>If the input list contains any string that is over the max length, it will appear in the output list
     * unmodified. Therefore, you should ensure that the input list has strings with the correct length.
     * @param strings The list of strings to partition
     * @param joiner When multiple input strings are joined into a single output string, this joiner is used
     * @param maxLength The max length of any string in the output list
     * @return A list of strings, where no string has a length over the max length
     * @throws IllegalArgumentException if the max length is negative
     * @throws NullPointerException if the list contains null
     */
    public static List<String> partitionByLength(List<String> strings, @NonNull String joiner, int maxLength) {
        if (maxLength < 0) {
            throw new IllegalArgumentException("Max length must be non-negative but was "+ maxLength);
        }
        if (strings.isEmpty() || maxLength == 0) {
            return strings;
        }
        String startingStr = strings.get(0);
        if (startingStr == null) {
            throw new NullPointerException("The first string in the input list cannot be null.");
        }
        if (strings.size() == 1) {
            return strings;
        }

        List<String> partitions = new ArrayList<>();
        StringJoiner currentPartition = new StringJoiner(joiner);
        currentPartition.add(startingStr);

        for (int i = 1; i < strings.size(); i++) {
            String str = strings.get(i);
            if (str == null) {
                throw new NullPointerException("The string in index " + i + " in the input list cannot be null.");
            }

            // only adds the current partition to the list when the next line won't fit
            int lengthIfLineAdded = currentPartition.length() + str.length() + joiner.length();
            if (lengthIfLineAdded > maxLength) {
                partitions.add(currentPartition.toString());
                currentPartition = new StringJoiner(joiner);
            }
            currentPartition.add(str);
        }

        // the leftover partition wasn't added in the earlier loop, add manually
        // the loop always runs at least once (since list has size > 1)
        // so the current partition will always be modified and need to be added
        partitions.add(currentPartition.toString());
        return Collections.unmodifiableList(partitions);
    }

}
