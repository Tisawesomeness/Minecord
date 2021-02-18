package com.tisawesomeness.minecord.testutil;

import com.google.common.base.Preconditions;
import lombok.NonNull;

public class MiscTestUtils {

    /**
     * Repeats a string a certain number of times
     * @param str The string to repeat
     * @param n The number of times to repeat
     * @return The repeated string
     * @throws IllegalArgumentException If {@code n} is negative
     */
    public static @NonNull String repeat(@NonNull String str, int n) {
        Preconditions.checkArgument(n >= 0, "number of times to repeat must be non-negative");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append(str);
        }
        return sb.toString();
    }

}
