package com.tisawesomeness.minecord.config;

import com.tisawesomeness.minecord.util.type.Verification;

/**
 * Thrown when a config file is invalid.
 */
public class InvalidConfigException extends RuntimeException {

    /**
     * Creates a new InvalidConfigException.
     */
    public InvalidConfigException() {}
    /**
     * Creates a new InvalidConfigException with a message.
     * @param message The message
     */
    public InvalidConfigException(String message) {
        super(message);
    }
    /**
     * Creates a new InvalidConfigException from another exception.
     * @param cause The cause of this exception
     */
    public InvalidConfigException(Throwable cause) {
        super(cause);
    }
    /**
     * Creates a new InvalidConfigException from another exception with a message.
     * @param message The message
     * @param cause The cause of this exception
     */
    public InvalidConfigException(String message, Throwable cause) {
        super(message, cause);
    }
    /**
     * Creates a new InvalidConfigException from a Verification with a list of errors.
     * @param v The config verification
     */
    public InvalidConfigException(Verification v) {
        super("The config file is invalid!\n" + String.join("\n", v.getErrors()));
    }

}
