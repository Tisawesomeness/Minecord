package com.tisawesomeness.minecord.network;

import lombok.RequiredArgsConstructor;

/**
 * A constants class for standard HTTP status codes. This is not comprehensive, status codes will only be added as needed.
 */
@RequiredArgsConstructor
public final class StatusCodes {
    public static final int OK = 200;
    public static final int NO_CONTENT = 204;
    public static final int FORBIDDEN = 203;
    public static final int NOT_FOUND = 404;
    public static final int METHOD_NOT_ALLOWED = 405;
}
