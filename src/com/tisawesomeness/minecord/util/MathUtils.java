package com.tisawesomeness.minecord.util;

import lombok.NonNull;
import lombok.SneakyThrows;

import javax.annotation.Nullable;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public final class MathUtils {

    public static final BigInteger LONG_MAX_VALUE = BigInteger.valueOf(Long.MAX_VALUE);
    public static final BigInteger LONG_MIN_VALUE = BigInteger.valueOf(Long.MIN_VALUE);

    private static final long LOWER_FOUR_BYTES = 0x0000_0000_FFFF_FFFFL;

    private MathUtils() {}

    /**
     * Clamps a value between a low and high bound.
     * If the value is lower than the low bound, the low bound is returned.
     * If the value is higher than the high bound, the high bound is returned.
     * Otherwise, the value itself is returned.
     * @param val The input number
     * @param low The low bound, inclusive
     * @param high The high bound, inclusive
     * @return A number guaranteed to be between the low and high bounds inclusive
     * @throws IllegalArgumentException If the low bound is not less than or equal to the high bound
     */
    public static long clamp(long val, long low, long high) {
        if (low > high) {
            throw new IllegalArgumentException(String.format("low=%d must be <= high=%d", low, high));
        }
        return Math.max(low, Math.min(val, high));
    }

    /**
     * Checks if an addition operation will overflow
     * @param a first number
     * @param b second number
     * @return whether an overflow will occur
     */
    public static boolean additionOverflows(long a, long b) {
        long result = a + b;
        return ((a ^ result) & (b ^ result)) < 0;
    }

    // https://stackoverflow.com/a/6195065
    /**
     * Checks if a multiplication operation will overflow
     * @param a first number
     * @param b second number
     * @return whether an overflow will occur
     */
    public static boolean multiplicationOverflows(long a, long b) {
        long result = a * b;
        return (Long.signum(a) * Long.signum(b) != Long.signum(result)) || (a != 0L && result / a != b);
    }

    /**
     * Casts an int to a long without sign extension.
     * Equivalent to creating a long with the given int as the lower 32 bits and zeroes as the upper 32 bits.
     * @param n an int
     * @return a long
     */
    public static long castWithoutSignExtension(int n) {
        return n & LOWER_FOUR_BYTES;
    }

    /**
     * Shuffles a collection into a new list.
     * @param collection input collection, remains unmodified
     * @return new list with shuffled elements
     * @param <T> type of list
     */
    public static <T> List<T> shuffle(Collection<T> collection) {
        List<T> list = new ArrayList<>(collection);
        Collections.shuffle(list);
        return list;
    }

    /**
     * Generates a pseudorandom Gaussian distributed value, using {@link ThreadLocalRandom#nextGaussian()}.
     * @param mean the mean of the distribution
     * @param standardDeviation the standard deviation (square root of variance) of the distribution
     * @return a randomly generated value
     */
    public static double randomGaussian(double mean, double standardDeviation) {
        return ThreadLocalRandom.current().nextGaussian() * standardDeviation + mean;
    }

    /**
     * Formats a decimal into a human-readable string, such as "2.7" or "3.72e-8". If the number's scientific notation
     * has an exponent between the provided min and max, then the number will be formatted exactly. Otherwise, the
     * number will be formatted in scientific notation. Numbers that cannot be expressed with the given fraction digits
     * will start with a '~'.
     * @param d the value to format
     * @param maxFractionDigits the maximum number of fraction digits to be shown
     * @param minExactExponent the minimum exponent to show exact values
     * @param maxExactExponent the maximum exponent to show exact values
     * @return the formatted number
     * @throws IllegalArgumentException if {@code minExactExponent > maxExactExponent}
     */
    public static String formatHumanReadable(double d, int maxFractionDigits, int minExactExponent, int maxExactExponent) {
        if (minExactExponent > maxExactExponent) {
            throw new IllegalArgumentException(String.format("minExactExponent %d cannot be greater than maxExactExponent %d",
                    minExactExponent, maxExactExponent));
        }

        NumberFormat scientificNotationFormat = new DecimalFormat("0.#E0");
        scientificNotationFormat.setMaximumFractionDigits(maxFractionDigits);
        String formattedScientific = scientificNotationFormat.format(d);

        String exponentStr = formattedScientific.substring(formattedScientific.indexOf('E') + 1);
        int exponent = Integer.parseInt(exponentStr); // always succeeds
        if (exponent < minExactExponent || maxExactExponent < exponent) {
            return addApproxSymbolIfNotExact(scientificNotationFormat, d).toLowerCase(Locale.ROOT);
        }

        NumberFormat decimalFormat = new DecimalFormat("0.#");
        decimalFormat.setMinimumFractionDigits(1);
        decimalFormat.setMaximumFractionDigits(maxFractionDigits);
        return addApproxSymbolIfNotExact(decimalFormat, d);
    }
    // Modifies format
    private static String addApproxSymbolIfNotExact(NumberFormat format, double d) {
        String original = format.format(d);
        format.setMaximumFractionDigits(format.getMaximumFractionDigits() + 1);
        String withOneMoreDigit = format.format(d);
        return original.equals(withOneMoreDigit) ? original : "~" + original;
    }

    /**
     * Computes the sha1 hash of a string.
     * @param str Any input string in UTF-8
     * @return The sha1 hash
     */
    // adapted from http://www.sha1-online.com/sha1-java/
    public static @NonNull String sha1(@NonNull String str) {
        byte[] result = getDigest().digest(str.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : result) {
            sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }
    @SneakyThrows(NoSuchAlgorithmException.class) // not possible
    private static MessageDigest getDigest() {
        return MessageDigest.getInstance("SHA-1");
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
                int digit = Character.digit(str.charAt(i++), radix);
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
