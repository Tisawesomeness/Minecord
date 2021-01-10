package com.tisawesomeness.minecord.util;

import com.google.common.base.Preconditions;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

public final class StringUtils {
    private StringUtils() {}

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
    public static List<String> partitionByLength(List<@NonNull String> strings, @NonNull String joiner, int maxLength) {
        Preconditions.checkArgument(maxLength >= 0);
        if (strings.isEmpty() || maxLength == 0) {
            return strings;
        }
        String startingStr = strings.get(0);
        Preconditions.checkNotNull(startingStr);
        if (strings.size() == 1) {
            return strings;
        }

        List<String> partitions = new ArrayList<>();
        StringJoiner currentPartition = new StringJoiner(joiner);
        currentPartition.add(startingStr);

        for (int i = 1; i < strings.size(); i++) {
            String str = strings.get(i);
            Preconditions.checkNotNull(str);

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
