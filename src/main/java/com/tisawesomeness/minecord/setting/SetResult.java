package com.tisawesomeness.minecord.setting;

import lombok.NonNull;

/**
 * Represents the result of changing or clearing a setting.
 */
interface SetResult {
    @NonNull String getMsg(String name, String from, String to);
    boolean isSuccess();
}
