package com.tisawesomeness.minecord.util;

import lombok.NonNull;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class to work with {@link UUID} and UUID strings in various formats.
 */
public final class UUIDs {
    private UUIDs() {}

    private static final Pattern SHORT_UUID_PATTERN = Pattern.compile(
            "^([0-9a-fA-F]{8})([0-9a-fA-F]{4})(4[0-9a-fA-F]{3})([89abAB][0-9a-fA-F]{3})([0-9a-fA-F]{12})$");
    private static final Pattern EFFICIENT_SHORT_UUID_PATTERN = Pattern.compile("^(.{8})(.{4})(.{4})(.{4})(.{12})$");
    private static final String SHORT_UUID_REPLACEMENT = "$1-$2-$3-$4-$5";
    private static final int UUID_LENGTH = 32;

    private static final Pattern LONG_UUID_PATTERN = Pattern.compile(
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-4[0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$");

    private static final Pattern DASH = Pattern.compile("-", Pattern.LITERAL);

    private static final Pattern INT_ARRAY_STRING = Pattern.compile(
            "\\[?(I;)?(-?\\d{1,10}),(-?\\d{1,10}),(-?\\d{1,10}),(-?\\d{1,10})]?", Pattern.CASE_INSENSITIVE);
    private static final Pattern MOST_LEAST_STRING = Pattern.compile("(-?\\d{1,19}),(-?\\d{1,19})");

    /**
     * <p>
     *     Tries to parse a version 4, variant 1 UUID from a string. Minecraft uses {@link UUID#randomUUID()}
     *     to generate UUIDs, which are all version 4, variant 1.
     * </p>
     * <p>
     *     A <b>standard</b> (with some extra tolerance) UUID is in the format
     *     {@code xxxxxxxx-xxxx-Mxxx-Nxxx-xxxxxxxxxxxx}
     *     <ul>
     *         <li>{@code x} = a hexadecimal digit 0-f, case insensitive</li>
     *         <li>{@code M} = the version of the UUID, which is 4.</li>
     *         <li>{@code N} = the upper bits encode the variant of the UUID, 8-b means variant 1.</li>
     *     </ul>
     * </p>
     * <p>
     *     A <b>1.16+ NBT</b> UUID is in the format {@code [I;A,B,C,D]}, the {@code I;} and the brackets on
     *     each side are optional.
     * </p>
     * <p>
     *     A <b>Pre-1.16 NBT</b> UUID can be in multiple formats:
     *     <ul>
     *         <li>{@code UUIDMost:X,UUIDLeast:Y} (unordered, the official format)</li>
     *         <li>{@code Most:X,Least:Y} (unordered)</li>
     *         <li>{@code X,Y} (ordered most, least)</li>
     *     </ul>
     * </p>
     * <p>
     *     This method will try all of the above formats until it finds a match.
     * </p>
     * @param str A possible UUID string
     * @return The parsed UUID or empty if the string is not the correct format
     */
    public static Optional<UUID> fromString(@NonNull String str) {
        if (LONG_UUID_PATTERN.matcher(str).matches()) {
            return Optional.of(UUID.fromString(str));
        }
        // Replacing first instead of matching and replacing to avoid matching regex twice
        String replaced = SHORT_UUID_PATTERN.matcher(str).replaceFirst(SHORT_UUID_REPLACEMENT);
        if (!replaced.equals(str)) {
            return Optional.of(UUID.fromString(replaced));
        }
        // Requires extra verification to check if the UUID is version 4, variant 1
        Optional<UUID> opt = fromStringIntArray(str);
        if (opt.isPresent()) {
            return opt.flatMap(UUIDs::flatMapIfValid);
        }
        return fromMostLeast(str).flatMap(UUIDs::flatMapIfValid);
    }

    // Not guaranteed to return version 4, variant 1
    private static Optional<UUID> fromStringIntArray(@NonNull CharSequence str) {
        Matcher arrMatcher = INT_ARRAY_STRING.matcher(str);
        if (!arrMatcher.matches()) {
            return Optional.empty();
        }
        OptionalInt oa = Mth.safeParseInt(arrMatcher.group(2));
        if (!oa.isPresent()) {
            return Optional.empty();
        }
        int a = oa.getAsInt();
        OptionalInt ob = Mth.safeParseInt(arrMatcher.group(3));
        if (!ob.isPresent()) {
            return Optional.empty();
        }
        int b = ob.getAsInt();
        OptionalInt oc = Mth.safeParseInt(arrMatcher.group(4));
        if (!oc.isPresent()) {
            return Optional.empty();
        }
        int c = oc.getAsInt();
        OptionalInt od = Mth.safeParseInt(arrMatcher.group(5));
        if (!od.isPresent()) {
            return Optional.empty();
        }
        int d = od.getAsInt();
        return Optional.of(fromInts(a, b, c, d));
    }

    // Not guaranteed to return version 4, variant 1
    private static Optional<UUID> fromMostLeast(@NonNull String str) {
        int commaIndex = str.indexOf(',');
        if (commaIndex == -1) {
            return Optional.empty();
        }
        Matcher mostLeastMatcher = MOST_LEAST_STRING.matcher(str);
        if (mostLeastMatcher.matches()) {
            OptionalLong msbOpt = Mth.safeParseLong(mostLeastMatcher.group(1));
            if (!msbOpt.isPresent()) {
                return Optional.empty();
            }
            long msb = msbOpt.getAsLong();
            OptionalLong lsbOpt = Mth.safeParseLong(mostLeastMatcher.group(2));
            if (!lsbOpt.isPresent()) {
                return Optional.empty();
            }
            long lsb = lsbOpt.getAsLong();
            return Optional.of(new UUID(msb, lsb));
        }
        return fromMostLeastNBT(str, commaIndex);
    }
    // Not guaranteed to return version 4, variant 1
    private static Optional<UUID> fromMostLeastNBT(@NonNull String str, int commaIndex) {
        // Separates string into "first,second"
        String first = str.substring(0, commaIndex);
        // Separates "first" into "name:num"
        int firstColonIndex = first.indexOf(':');
        if (firstColonIndex == -1) {
            return Optional.empty();
        }
        String firstNumStr = first.substring(firstColonIndex + 1);
        OptionalLong firstNumOpt = Mth.safeParseLong(firstNumStr);
        if (!firstNumOpt.isPresent()) {
            return Optional.empty();
        }
        long firstNum = firstNumOpt.getAsLong();

        // Doing the same but with second
        String second = str.substring(commaIndex + 1);
        int secondColonIndex = second.indexOf(':');
        if (secondColonIndex == -1) {
            return Optional.empty();
        }
        String secondNumStr = second.substring(secondColonIndex + 1);
        OptionalLong secondNumOpt = Mth.safeParseLong(secondNumStr);
        if (!secondNumOpt.isPresent()) {
            return Optional.empty();
        }
        long secondNum = secondNumOpt.getAsLong();

        boolean uuidMostAppearsFirst = false;
        long msb = 0;
        long lsb = 0;

        // The string can be in "most,least" order or "least,most" order
        // Detect whether the first part is most or least
        String firstName = first.substring(0, firstColonIndex);
        if (isMost(firstName)) {
            msb = firstNum;
            uuidMostAppearsFirst = true;
        } else if (isLeast(firstName)) {
            lsb = firstNum;
        } else {
            return Optional.empty();
        }

        // Then, check if string contains both most and least instead of "most,most" or "least,least"
        String secondName = second.substring(0, secondColonIndex);
        if (uuidMostAppearsFirst && isLeast(secondName)) {
            lsb = secondNum;
        } else if (!uuidMostAppearsFirst && isMost(secondName)) {
            msb = secondNum;
        } else {
            return Optional.empty();
        }

        return Optional.of(new UUID(msb, lsb));
    }
    // Minecraft NBT is not localized
    private static boolean isMost(@NonNull String str) {
        return "UUIDMost".equalsIgnoreCase(str) || "Most".equalsIgnoreCase(str);
    }
    private static boolean isLeast(@NonNull String str) {
        return "UUIDLeast".equalsIgnoreCase(str) || "Least".equalsIgnoreCase(str);
    }

    /**
     * Efficiently converts a short UUID to a UUID object by skipping regex validation.
     * <br>This does not enforce the correct UUID version and variant.
     * @param str <b>Must be a valid short UUID!</b>
     * @return A UUID object
     * @throws IllegalArgumentException if the input string is not a valid short UUID
     */
    public static @NonNull UUID fromGuaranteedShortString(@NonNull CharSequence str) {
        if (str.length() != UUID_LENGTH) {
            throw new IllegalArgumentException("A short UUID must be exactly 32 characters.");
        }
        String r = EFFICIENT_SHORT_UUID_PATTERN.matcher(str).replaceFirst(SHORT_UUID_REPLACEMENT);
        return UUID.fromString(r);
    }

    /**
     * Checks if a UUID is a valid version 4, variant 1 UUID.
     * @param uuid the UUID
     * @return true if the UUID is valid
     */
    public static boolean isValid(@NonNull UUID uuid) {
        // Variant 1 UUIDs will show up as variant 2 in Java
        return uuid.version() == 4 && uuid.variant() == 2;
    }
    private static Optional<UUID> flatMapIfValid(@NonNull UUID uuid) {
        return isValid(uuid) ? Optional.of(uuid) : Optional.empty();
    }

    /**
     * Converts a UUID to a string without dashes.
     * @param uuid Any input UUID
     * @return The UUID as a string
     */
    public static @NonNull String toShortString(@NonNull UUID uuid) {
        return DASH.matcher(uuid.toString()).replaceAll(Matcher.quoteReplacement(""));
    }

    /**
     * Converts a UUID to a string with dashes in {@code 8-4-4-4-12} format.
     * @param uuid Any input UUID
     * @return The UUID as a string
     */
    public static @NonNull String toLongString(@NonNull UUID uuid) {
        return uuid.toString();
    }

    /**
     * Converts a UUID to pre-1.16 UUIDMost/UUIDLeast NBT.
     * @param uuid The UUID
     * @return UUIDMost,UUIDLeast
     */
    public static @NonNull String toMostLeastString(@NonNull UUID uuid) {
        return String.format("UUIDMost:%d,UUIDLeast:%d", uuid.getMostSignificantBits(), uuid.getLeastSignificantBits());
    }

    /**
     * Converts a UUID to an 1.16+ NBT int array string.
     * @param uuid The UUID
     * @return The NBT int array as a string
     */
    public static @NonNull String toIntArrayString(@NonNull UUID uuid) {
        int[] arr = toIntArray(uuid);
        return String.format("[I;%d,%d,%d,%d]", arr[0], arr[1], arr[2], arr[3]);
    }
    /**
     * Converts a UUID to an array of four ints, most to least significant, used in 1.16+ NBT.
     * @param uuid The UUID
     * @return An array of four ints
     */
    public static int[] toIntArray(@NonNull UUID uuid) {
        int[] arr = new int[4];
        long msb = uuid.getMostSignificantBits();
        arr[0] = (int) (msb >> Integer.SIZE);
        arr[1] = (int) msb;
        long lsb = uuid.getLeastSignificantBits();
        arr[2] = (int) (lsb >> Integer.SIZE);
        arr[3] = (int) lsb;
        return arr;
    }
    /**
     * Converts an int array to a UUID.
     * @param arr An array of four ints, most to least significant
     * @return The UUID
     */
    public static @NonNull UUID fromIntArray(int[] arr) {
        if (arr.length != 4) {
            throw new IllegalArgumentException("Array length must be 4 but was " + arr.length);
        }
        return fromInts(arr[0], arr[1], arr[2], arr[3]);
    }
    /**
     * Converts four integers from most to least significant to a UUID.
     * @param a bits 127-96
     * @param b bits 95-64
     * @param c bits 63-32
     * @param d bits 31-0
     * @return The UUID
     */
    public static @NonNull UUID fromInts(int a, int b, int c, int d) {
        // casting lower bits to long will sign-extend, which messes up the upper bits
        long msb = (((long) a) << Integer.SIZE) | Mth.castWithoutSignExtension(b);
        long lsb = (((long) c) << Integer.SIZE) | Mth.castWithoutSignExtension(d);
        return new UUID(msb, lsb);
    }

}
