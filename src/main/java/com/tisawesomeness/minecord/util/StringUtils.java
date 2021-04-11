package com.tisawesomeness.minecord.util;

import lombok.NonNull;

import javax.annotation.Nullable;
import java.util.*;

public final class StringUtils {
    private StringUtils() {}

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
        return str.substring(limit(0, beginIndex, str.length()));
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
    private static int limit(int low, int val, int high) {
        return Math.max(low, Math.min(val, high));
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



    /*
     * Copyright (c) 1994, 2019, Oracle and/or its affiliates. All rights reserved.
     * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
     *
     * This code is free software; you can redistribute it and/or modify it
     * under the terms of the GNU General Public License version 2 only, as
     * published by the Free Software Foundation.  Oracle designates this
     * particular file as subject to the "Classpath" exception as provided
     * by Oracle in the LICENSE file that accompanied this code.
     *
     * This code is distributed in the hope that it will be useful, but WITHOUT
     * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
     * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
     * version 2 for more details (a copy is included in the LICENSE file that
     * accompanied this code).
     *
     * You should have received a copy of the GNU General Public License version
     * 2 along with this work; if not, write to the Free Software Foundation,
     * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
     *
     * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
     * or visit www.oracle.com if you need additional information or have any
     * questions.
     *
     * The code below is a modified version of parseInt() and parseLong() from JDK 15's source code in the links below.
     * It was changed by Tis_awesomeness (GitHub Tisawesomeness) on Mar 10 2021 to return an Optional number.
     * https://hg.openjdk.java.net/jdk/jdk15/file/0dabbdfd97e6/src/java.base/share/classes/java/lang/Integer.java
     * https://hg.openjdk.java.net/jdk/jdk15/file/0dabbdfd97e6/src/java.base/share/classes/java/lang/Long.java
     */

    /**
     * Parses an int from a string without relying on exceptions.
     * @param str The string to parse, may or may not be an integer
     * @return The integer if present, empty if null
     * @throws IllegalArgumentException If {@code radix} is not between
     *                                  {@link Character#MIN_RADIX} and {@link Character#MAX_RADIX}
     * @see Integer#parseInt(String)
     */
    public static OptionalInt safeParseInt(@Nullable String str) {
        return safeParseInt(str, 10);
    }
    /**
     * Parses an int from a string without relying on exceptions.
     * @param str The string to parse, may or may not be an integer
     * @param radix The radix of the number
     * @return The integer if present, empty if null
     * @throws IllegalArgumentException If {@code radix} is not between
     *                                  {@link Character#MIN_RADIX} and {@link Character#MAX_RADIX}
     * @see Integer#parseInt(String, int)
     */
    public static OptionalInt safeParseInt(@Nullable String str, int radix) {
        if (radix < Character.MIN_RADIX) {
            throw new IllegalArgumentException("radix " + radix + " less than Character.MIN_RADIX");
        }
        if (radix > Character.MAX_RADIX) {
            throw new IllegalArgumentException("radix " + radix + " greater than Character.MAX_RADIX");
        }
        if (str == null) {
            return OptionalInt.empty();
        }

        boolean negative = false;
        int i = 0;
        int len = str.length();
        int limit = -Integer.MAX_VALUE;

        if (len > 0) {
            char firstChar = str.charAt(0);
            if (firstChar < '0') { // Possible leading "+" or "-"
                if (firstChar == '-') {
                    negative = true;
                    limit = Integer.MIN_VALUE;
                } else if (firstChar != '+') {
                    return OptionalInt.empty();
                }

                if (len == 1) { // Cannot have lone "+" or "-"
                    return OptionalInt.empty();
                }
                i++;
            }
            int multmin = limit / radix;
            int result = 0;
            while (i < len) {
                // Accumulating negatively avoids surprises near MAX_VALUE
                int digit = Character.digit(str.charAt(i++), radix);
                if (digit < 0 || result < multmin) {
                    return OptionalInt.empty();
                }
                result *= radix;
                if (result < limit + digit) {
                    return OptionalInt.empty();
                }
                result -= digit;
            }
            return OptionalInt.of(negative ? result : -result);
        } else {
            return OptionalInt.empty();
        }
    }

    /**
     * Parses a long from a string without relying on exceptions.
     * @param str The string to parse, may or may not be a long
     * @return The long if present, empty if null
     * @throws IllegalArgumentException If {@code radix} is not between
     *                                  {@link Character#MIN_RADIX} and {@link Character#MAX_RADIX}
     * @see Long#parseLong(String, int)
     */
    public static OptionalLong safeParseLong(@Nullable String str) {
        return safeParseLong(str, 10);
    }
    /**
     * Parses a long from a string without relying on exceptions.
     * @param str The string to parse, may or may not be a long
     * @param radix The radix of the number
     * @return The long if present, empty if null
     * @throws IllegalArgumentException If {@code radix} is not between
     *                                  {@link Character#MIN_RADIX} and {@link Character#MAX_RADIX}
     * @see Long#parseLong(String, int)
     */
    public static OptionalLong safeParseLong(@Nullable String str, int radix) {
        if (radix < Character.MIN_RADIX) {
            throw new IllegalArgumentException("radix " + radix + " less than Character.MIN_RADIX");
        }
        if (radix > Character.MAX_RADIX) {
            throw new IllegalArgumentException("radix " + radix + " greater than Character.MAX_RADIX");
        }
        if (str == null) {
            return OptionalLong.empty();
        }

        boolean negative = false;
        int i = 0;
        int len = str.length();
        long limit = -Long.MAX_VALUE;

        if (len > 0) {
            char firstChar = str.charAt(0);
            if (firstChar < '0') { // Possible leading "+" or "-"
                if (firstChar == '-') {
                    negative = true;
                    limit = Long.MIN_VALUE;
                } else if (firstChar != '+') {
                    return OptionalLong.empty();
                }

                if (len == 1) { // Cannot have lone "+" or "-"
                    return OptionalLong.empty();
                }
                i++;
            }
            long multmin = limit / radix;
            long result = 0;
            while (i < len) {
                // Accumulating negatively avoids surprises near MAX_VALUE
                int digit = Character.digit(str.charAt(i++),radix);
                if (digit < 0 || result < multmin) {
                    return OptionalLong.empty();
                }
                result *= radix;
                if (result < limit + digit) {
                    return OptionalLong.empty();
                }
                result -= digit;
            }
            return OptionalLong.of(negative ? result : -result);
        } else {
            return OptionalLong.empty();
        }
    }

}
