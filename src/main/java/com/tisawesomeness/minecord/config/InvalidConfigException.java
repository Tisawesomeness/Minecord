package com.tisawesomeness.minecord.config;

import com.tisawesomeness.minecord.util.type.Verification;

/**
 * Thrown when a config file is invalid.
 */
public class InvalidConfigException extends RuntimeException {
    /**
     * Creates a new InvalidConfigException from a Verification with a list of errors.
     * @param v The config verification
     */
    public InvalidConfigException(Verification v) {
        super("The config file is invalid!\n" + String.join("\n", v.getErrors()));
    }
    /**
     * Creates a new InvalidConfigException from another exception.
     * @param cause The cause of this exception
     */
    public InvalidConfigException(Throwable cause) {
        super(cause);
    }
}
