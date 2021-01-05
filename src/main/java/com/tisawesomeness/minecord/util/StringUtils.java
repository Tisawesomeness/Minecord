package com.tisawesomeness.minecord.util;

import com.google.common.base.Preconditions;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class StringUtils {
    private StringUtils() {}

    public static List<String> splitLinesByLength(List<@NonNull String> lines, int maxLength) {
        return splitByLength(lines, "\n", maxLength);
    }
    public static List<String> splitByLength(List<@NonNull String> strings, @NonNull String joiner, int maxLength) {
        Preconditions.checkArgument(maxLength >= 0);
        if (strings.isEmpty() || maxLength == 0) {
            return strings;
        }
        if (strings.size() == 1) {
            Preconditions.checkNotNull(strings.get(0));
            return strings;
        }

        List<String> partitions = new ArrayList<>();
        String startingStr = strings.get(0);
        Preconditions.checkNotNull(startingStr);
        StringBuilder currentPartition = new StringBuilder(startingStr);

        for (int i = 1; i < strings.size(); i++) {
            String str = strings.get(i);
            Preconditions.checkNotNull(str);
            // only adds the current partition to the list when the next line won't fit
            int lengthIfLineAdded = currentPartition.length() + str.length() + joiner.length();
            if (lengthIfLineAdded > maxLength) {
                partitions.add(currentPartition.toString());
                currentPartition = new StringBuilder();
                // prevents adding a joiner (usually newline) at the start of a string
            } else {
                currentPartition.append(joiner);
            }
            currentPartition.append(str);
        }

        // the leftover partition wasn't added in the earlier loop, add manually
        // the loop always runs at least once (since list has size > 1)
        // so the current partition will always be modified and need to be added
        partitions.add(currentPartition.toString());
        return Collections.unmodifiableList(partitions);
    }

}
