package com.tisawesomeness.minecord.debug;

import lombok.NonNull;

/**
 * Provides debug information to the {@code &debug} command.
 */
public interface DebugOption {
    /**
     * @return The name of this debug option, used for user input
     */
    @NonNull String getName();
    /**
     * Gets useful debug information this object is responsible for.
     * @param extra A possibly-empty string, used to select sub-options
     * @return The debug information formatted as a multiline string
     */
    @NonNull String debug(@NonNull String extra);
}
