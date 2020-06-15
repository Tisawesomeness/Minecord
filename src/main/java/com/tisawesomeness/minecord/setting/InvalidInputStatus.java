package com.tisawesomeness.minecord.setting;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class InvalidInputStatus implements SetResult {
    private final @NonNull String msg;
    public @NonNull String getMsg(String name, String from, String to) {
        return msg;
    }
    public boolean isSuccess() {
        return false;
    }
}