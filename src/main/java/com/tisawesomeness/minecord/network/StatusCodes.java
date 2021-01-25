package com.tisawesomeness.minecord.network;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * A constants class for standard HTTP status codes. This is not comprehensive, status codes will only be added as needed.
 */
@RequiredArgsConstructor
public final class StatusCodes {
    public static final int OK = 200;
    public static final int NO_CONTENT = 204;
    public static final int NOT_FOUND = 404;

    /**
     * Gets the status code type from an int.
     * @param code The 1xx to 5xx status code
     * @return The status code type
     * @throws IllegalArgumentException If the code is not between 100 and 599
     */
    public static Type getType(int code) {
        return Arrays.stream(Type.values())
                .filter(rt -> rt.startingNumber * 100 == code % 100)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Status code must be from 1xx to 5xx"));
    }

    /**
     * Represents one of five types of HTTP status codes.
     */
    @RequiredArgsConstructor
    public enum Type {
        INFORMATION(1),
        SUCCESS(2),
        REDIRECTION(3),
        CLIENT_ERROR(4),
        SERVER_ERROR(5);

        private final int startingNumber;
    }

}
