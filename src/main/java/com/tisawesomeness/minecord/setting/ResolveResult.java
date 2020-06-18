package com.tisawesomeness.minecord.setting;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

/**
 * The result of the {@link ISetting#resolve(String input)} method, containing a setting value or a message explaining why the input was invalid.
 * @param <T> The type of the setting.
 */
@RequiredArgsConstructor
public
class ResolveResult<T> {
    public final Optional<T> value;
    private final String msg;

    /**
     * Creates a successful resolve result.
     * @param val The value of the setting parsed from user input.
     */
    public ResolveResult(T val) {
        this(Optional.of(val), null);
    }
    /**
     * Creates a failed resolve result.
     * @param msg The message to display.
     */
    public ResolveResult(String msg) {
        this(Optional.empty(), msg);
    }

    /**
     * Gets the status associated with a failed resolve.
     * @throws UnsupportedOperationException When the value is valid.
     */
    public InvalidInputStatus toStatus() {
        if (value.isPresent()) {
            throw new UnsupportedOperationException("Resolve returned a valid value, use it.");
        }
        return new InvalidInputStatus(msg);
    }

    @RequiredArgsConstructor
    static class InvalidInputStatus implements SetResult {
        private final @NonNull String msg;
        public @NonNull String getMsg(String name, String from, String to) {
            return msg;
        }
        public boolean isSuccess() {
            return false;
        }
    }
}
