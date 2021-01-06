package com.tisawesomeness.minecord.util.discord;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.util.function.UnaryOperator;

/**
 * Simple markdown elements that can be applied without configuration
 */
@RequiredArgsConstructor
public enum SimpleMarkdownAction implements MarkdownAction {
    // must be first
    QUOTE_BLOCK(MarkdownUtil::quoteBlock, -2),
    QUOTE(MarkdownUtil::quote, -1),
    // any order
    BOLD(MarkdownUtil::bold, 0),
    ITALICS(MarkdownUtil::italics, 0),
    UNDERLINE(MarkdownUtil::underline, 0),
    STRIKE(MarkdownUtil::strike, 0),
    SPOILER(MarkdownUtil::spoiler, 0),
    // must be last
    // MASKED_LINK 1
    MONOSPACE(MarkdownUtil::monospace, 2),
    CODEBLOCK(MarkdownUtil::codeblock, 3);
    // CODEBLOCK_WITH_LANGUAGE 3

    private final UnaryOperator<String> markdownFn;
    @Getter private final int priority;

    public @NonNull String apply(@NonNull String str) {
        return markdownFn.apply(str);
    }
}
