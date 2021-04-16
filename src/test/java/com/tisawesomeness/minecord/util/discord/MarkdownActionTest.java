package com.tisawesomeness.minecord.util.discord;

import com.tisawesomeness.minecord.util.Lists;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MarkdownActionTest {

    @Test
    @DisplayName("Markdown actions are ordered by priority correctly")
    public void testOrdering() {
        MaskedLink maskedLink = new MaskedLink("https://example.com/");
        Codeblock codeblock = new Codeblock();
        List<MarkdownAction> actions = Lists.of(
                codeblock,
                SimpleMarkdownAction.BOLD,
                SimpleMarkdownAction.QUOTE,
                SimpleMarkdownAction.ITALICS,
                SimpleMarkdownAction.MONOSPACE,
                SimpleMarkdownAction.QUOTE_BLOCK,
                SimpleMarkdownAction.UNDERLINE,
                SimpleMarkdownAction.STRIKE,
                SimpleMarkdownAction.SPOILER,
                maskedLink
        );
        List<MarkdownAction> sorted = Lists.sort(actions, MarkdownAction.comparingByPriority());
        assertThat(sorted)
                .startsWith(SimpleMarkdownAction.QUOTE_BLOCK, SimpleMarkdownAction.QUOTE)
                .endsWith(maskedLink, SimpleMarkdownAction.MONOSPACE, codeblock);
    }

}
