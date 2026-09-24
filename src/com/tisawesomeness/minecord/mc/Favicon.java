package com.tisawesomeness.minecord.mc;

import com.tisawesomeness.minecord.util.type.Dimensions;
import lombok.*;

import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.util.Base64;
import java.util.Optional;
import java.util.regex.Pattern;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Favicon {

    /** The expected width and height of a favicon. Older clients may not display favicons with different sizes. */
    public static final int EXPECTED_SIZE = 64;

    private static final String PREAMBLE = "data:image/png;base64,";
    private static final Pattern NEWLINES_PATTERN = Pattern.compile("[\r\n]");
    private static final int MIN_LENGTH_FOR_DIMENSIONS = 24;
    private static final int MIN_LENGTH = 33;
    private static final long PNG_SIGNATURE = 0x89_504E47_0D0A_1A_0AL;
    private static final int IHDR_LENGTH = 13;
    private static final int IHDR_TYPE = ('I' << 24) + ('H' << 16) + ('D' << 8) + 'R';

    /** The image data for the favicon. */
    @Getter private final byte[] data;
    private final boolean usesNewlines;

    /**
     * Creates a favicon from image bytes.
     * @param data byte array representing a raw PNG image
     * @return new favicon
     */
    public static Favicon from(byte[] data) {
        return new Favicon(data, false);
    }

    /**
     * Parses a base64-encoded favicon, according to the server ping
     * <a href="https://wiki.vg/Server_List_Ping#Status_Response">specification</a>. Newlines are accepted and trimmed
     * before parsing, use {@link #usesNewlines()} to check if newlines were used.
     * @param str the string to parse
     * @return the favicon, or empty if the input is malformed
     */
    public static Optional<Favicon> parse(@NonNull String str) {
        if (!str.startsWith(PREAMBLE)) {
            return Optional.empty();
        }
        String imageData = str.substring(PREAMBLE.length());
        boolean usesNewlines = false;
        if (imageData.indexOf('\r') != -1 || imageData.indexOf('\n') != -1) {
            imageData = NEWLINES_PATTERN.matcher(imageData).replaceAll("");
            usesNewlines = true;
        }
        try {
            byte[] decoded = Base64.getDecoder().decode(imageData);
            return Optional.of(new Favicon(decoded, usesNewlines));
        } catch (IllegalArgumentException ignored) {
            return Optional.empty();
        }
    }

    /**
     * If this favicon was created through {@link #parse(String)}, returns whether newlines were in the original input.
     * Favicons with newlines no longer work since 1.13.
     * @return whether newlines were used to create this favicon
     */
    public boolean usesNewlines() {
        return usesNewlines;
    }

    /**
     * Checks if the favicon is a valid PNG image. If the PNG can be read, returns the width and height.
     * This method only checks if a PNG can be <strong>read</strong>, not necessarily displayed.
     * <br>This method can return any {@link PngError PngError}.
     * @return the image dimensions if the PNG is valid, or a {@link PngError PngError} otherwise
     */
    @SneakyThrows // IOE, not possible with ByteArrayInputStream
    public Png validate() {
        if (data.length < MIN_LENGTH_FOR_DIMENSIONS) {
            return new Png(PngError.TOO_SHORT);
        }
        DataInput is = new DataInputStream(new ByteArrayInputStream(data));
        if (is.readLong() != PNG_SIGNATURE) {
            return new Png(PngError.BAD_SIGNATURE);
        }
        if (is.readInt() != IHDR_LENGTH) {
            return new Png(PngError.BAD_IHDR_LENGTH);
        }
        if (is.readInt() != IHDR_TYPE) {
            return new Png(PngError.BAD_IHDR_TYPE);
        }
        int width = is.readInt();
        if (width < 0) {
            return new Png(PngError.NEGATIVE_WIDTH);
        }
        int height = is.readInt();
        if (height < 0) {
            return new Png(PngError.NEGATIVE_WIDTH);
        }

        Dimensions dimensions = new Dimensions(width, height);
        if (data.length < MIN_LENGTH) {
            return new Png(dimensions, PngError.TOO_SHORT);
        }
        byte bitDepth = is.readByte();
        if (!isValidBitDepth(bitDepth)) {
            return new Png(dimensions, PngError.BAD_BIT_DEPTH);
        }
        byte colorType = is.readByte();
        if (!isValidColorType(colorType)) {
            return new Png(dimensions, PngError.BAD_COLOR_TYPE);
        }
        if (!boundsFitsInInt(width, height, bitDepth)) {
            return new Png(PngError.TOO_BIG);
        }
        return new Png(dimensions);
    }

    private static boolean isValidBitDepth(byte bitDepth) {
        return bitDepth == 1 || bitDepth == 2 || bitDepth == 4 || bitDepth == 8 || bitDepth == 16;
    }
    private static boolean isValidColorType(byte colorType) {
        return colorType == 0 || colorType == 2 || colorType == 3 || colorType == 4 || colorType == 6;
    }
    private static boolean boundsFitsInInt(int width, int height, byte bitDepth) {
        if (bitDepth != 16) {
            return true;
        }
        // 4 components for RGBA, x2 estimate
        long estimate = 4L * width * height * 2L;
        // must fit within int
        return estimate == (int) estimate;
    }

    @Value
    @AllArgsConstructor
    public static class Png {
        @Nullable Dimensions dimensions;
        @Nullable PngError error;

        public Png(Dimensions dimensions) {
            this(dimensions, null);
        }
        public Png(PngError error) {
            this(null, error);
        }
    }

    /**
     * An error that can occur when validating a PNG image. See the
     * <a href="https://en.wikipedia.org/wiki/PNG#File_format">Wikipedia page</a> for more details.
     */
    public enum PngError {
        /** Image data is too small to be a valid PNG */
        TOO_SHORT,
        /** Does not contain PNG file header */
        BAD_SIGNATURE,
        /** IHDR chunk length is invalid */
        BAD_IHDR_LENGTH,
        /** First PNG chunk is not IHDR */
        BAD_IHDR_TYPE,
        /** Width of PNG is negative (or overflow) */
        NEGATIVE_WIDTH,
        /** Height of PNG is negative (or overflow) */
        NEGATIVE_HEIGHT,
        /** Bit depth is not valid */
        BAD_BIT_DEPTH,
        /** Color type is not valid */
        BAD_COLOR_TYPE,
        /** Estimate of PNG buffer size does not fit within int */
        TOO_BIG
    }

}
