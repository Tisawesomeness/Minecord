package com.tisawesomeness.minecord.util;

import java.util.*;

public class StringUtils {

    /**
     * Splits a string with newlines into groups, making sure that every group is a whole number of lines and at most the max length.
     * The provided String is split into lines, then stored in an ArrayList that follows the other methods specifications.
     * If a line is over the max length, it is split into multiple lines.
     * Useful for getting past Discord's char limits.
     * @param str An ArrayList of String lines without newline characters.
     * @param maxLength The maximum length allowed for a string in the returned list.
     * @return A list of strings where every string length < maxLength - 1
     */
    public static ArrayList<String> splitLinesByLength(String str, int maxLength) {
        return splitLinesByLength(new ArrayList<>(Arrays.asList(str.split("\n"))), maxLength);
    }

    /**
     * Splits a list of lines into groups, making sure that none of them are over the max length.
     * This takes into account the additional newline character.
     * If a line is over the max length, it is split into multiple lines.
     * Useful for getting past Discord's char limits.
     * @param lines An ArrayList of String lines without newline characters.
     * @param maxLength The maximum length allowed for a string in the returned list.
     * @return A list of strings where every string length < maxLength - 1
     */
    public static ArrayList<String> splitLinesByLength(ArrayList<String> lines, int maxLength) {
        ArrayList<String> split = new ArrayList<>();
        String splitBuf = "";
        for (int i = 0; i < lines.size(); i++) {
            // Max line length check
            if (lines.get(i).length() > maxLength - 1) {
                lines.add(i + 1, lines.get(i).substring(maxLength - 1));
                lines.set(i, lines.get(i).substring(0, maxLength - 1));
            }
            String fieldTemp = splitBuf + lines.get(i) + "\n";
            if (fieldTemp.length() > maxLength) {
                i -= 1; // The line goes over the char limit, don't include!
                split.add(splitBuf.substring(0, splitBuf.length() - 1));
                splitBuf = "";
            } else {
                splitBuf = fieldTemp;
            }
            // Last line check
            if (i == lines.size() - 1) {
                split.add(fieldTemp);
            }
        }
        return split;
    }

    /**
     * Gets the total number of characters in a list of lines, adding 1 for each newline
     * @param lines An ArrayList of String lines, without newline characters
     * @return The total number of characters
     */
    public static int getTotalChars(ArrayList<String> lines) {
        int chars = 0;
        for (String line : lines) {
            chars += line.length() + 1; // +1 for newline
        }
        return chars;
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
    public static List<String> partitionByLength(List<String> strings, String joiner, int maxLength) {
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
