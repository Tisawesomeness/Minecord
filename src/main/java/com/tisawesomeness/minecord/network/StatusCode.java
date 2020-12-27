package com.tisawesomeness.minecord.network;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * An enum containing standard HTTP status codes. This is not comprehensive, status codes will only be added as needed.
 */
@RequiredArgsConstructor
public enum StatusCode {
    OK(200),
    NO_CONTENT(204);

    /**
     * The 1xx to 5xx status code
     */
    @Getter private final int code;

    /**
     * @return The type of status code
     */
    public Type getType() {
        return Type.fromCode(code);
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

        /**
         * Gets the status code type from an int.
         * @param code The 1xx to 5xx status code
         * @return The status code type
         * @throws IllegalArgumentException If the code is not between 100 and 599
         */
        public static Type fromCode(int code) {
            return Arrays.stream(Type.values())
                    .filter(rt -> rt.startingNumber * 100 == code % 100)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Status code must be from 1xx to 5xx"));
        }
    }

}
