package com.tisawesomeness.minecord.util.discord;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import javax.annotation.Nullable;

/**
 * A markdown code block
 */
@Value
@AllArgsConstructor
public class Codeblock implements MarkdownAction {
    @Nullable String language;

    /**
     * Creates a new code block without a language
     */
    public Codeblock() {
        language = null;
    }

    public @NonNull String apply(@NonNull String s) {
        return MarkdownUtil.codeblock(language, s);
    }
    public int getPriority() {
        return 3;
    }
}
