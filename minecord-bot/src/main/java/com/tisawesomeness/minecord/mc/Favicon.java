package com.tisawesomeness.minecord.mc;

import com.tisawesomeness.minecord.common.util.Either;
import com.tisawesomeness.minecord.util.type.Dimensions;
import lombok.*;

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
    private static final int MIN_LENGTH = 24;
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
        if (imageData.indexOf('\r') == -1 || imageData.indexOf('\n') == -1) {
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
    public Either<PngError, Dimensions> validate() {
        if (data.length < MIN_LENGTH) {
            return Either.left(PngError.TOO_SHORT);
        }
        DataInput is = new DataInputStream(new ByteArrayInputStream(data));
        if (is.readLong() != PNG_SIGNATURE) {
            return Either.left(PngError.BAD_SIGNATURE);
        }
        if (is.readInt() != IHDR_LENGTH) {
            return Either.left(PngError.BAD_IHDR_LENGTH);
        }
        if (is.readInt() != IHDR_TYPE) {
            return Either.left(PngError.BAD_IHDR_TYPE);
        }
        int width = is.readInt();
        if (width < 0) {
            return Either.left(PngError.NEGATIVE_WIDTH);
        }
        int height = is.readInt();
        if (height < 0) {
            return Either.left(PngError.NEGATIVE_WIDTH);
        }
        return Either.right(new Dimensions(width, height));
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
        NEGATIVE_HEIGHT
    }

}
