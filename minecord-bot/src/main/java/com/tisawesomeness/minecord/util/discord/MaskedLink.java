package com.tisawesomeness.minecord.util.discord;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.net.URL;

/**
 * A markdown masked link
 */
@Value
@RequiredArgsConstructor
public class MaskedLink implements MarkdownAction {
    @NonNull String url;

    /**
     * Creates a new masked link from a URL
     * @param url The URL
     */
    public MaskedLink(@NonNull URL url) {
        this(url.toString());
    }

    public @NonNull String apply(@NonNull String text) {
        return MarkdownUtil.maskedLink(text, url);
    }
    public int getPriority() {
        return 1;
    }
}
