package com.tisawesomeness.minecord.lang;

import com.tisawesomeness.minecord.util.ListUtils;
import com.tisawesomeness.minecord.util.discord.Codeblock;
import com.tisawesomeness.minecord.util.discord.MarkdownAction;
import com.tisawesomeness.minecord.util.discord.MaskedLink;
import com.tisawesomeness.minecord.util.discord.SimpleMarkdownAction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class LocalizedMarkdownBuilderTest {

    private static final List<MarkdownAction> actions = ListUtils.of(
            SimpleMarkdownAction.QUOTE_BLOCK,
            SimpleMarkdownAction.QUOTE,
            SimpleMarkdownAction.BOLD,
            SimpleMarkdownAction.ITALICS,
            SimpleMarkdownAction.UNDERLINE,
            SimpleMarkdownAction.STRIKE,
            SimpleMarkdownAction.SPOILER,
            new MaskedLink("https://example.com/"),
            SimpleMarkdownAction.MONOSPACE,
            new Codeblock()
    );

    @Test
    @DisplayName("Immediately building a builder simply returns the message")
    public void testSimpleFormat() {
        String constant = "constant";
        MessageFormat mf = new MessageFormat(constant);
        String actual = new LocalizedMarkdownBuilder(mf).build();
        assertThat(actual).isEqualTo(constant);
    }

    @Test
    @DisplayName("Immediately building a builder simply returns the message, even if not all args are used")
    public void testSimpleFormatWithUnfinishedArgs() {
        String constant = "sample {0} text";
        MessageFormat mf = new MessageFormat(constant);
        String actual = new LocalizedMarkdownBuilder(mf).build();
        assertThat(actual).isEqualTo(constant);
    }

    @Test
    @DisplayName("Immediately building a builder with args simply returns the formatted message")
    public void testSimpleFormatWithArgs() {
        String arg = ":thinking:";
        MessageFormat mf = new MessageFormat("sample {0} text");
        String actual = new LocalizedMarkdownBuilder(mf, arg).build();
        Object[] formatArgs = {arg};
        assertThat(actual).isEqualTo(mf.format(formatArgs));
    }

    @ParameterizedTest(name = "{index} ==> Action {0} is applied correctly")
    @DisplayName("Adding markdown works")
    @MethodSource("actionProvider")
    public void testMarkdown(MarkdownAction candidate) {
        String arg = "123";
        MessageFormat mf = new MessageFormat("{0}");
        String actual = new LocalizedMarkdownBuilder(mf, arg)
                .apply(0, candidate)
                .build();
        assertThat(actual).isEqualTo(candidate.apply(arg));
    }

    @ParameterizedTest(name = "{index} ==> Action {0} is applied correctly when extra text is at the end")
    @DisplayName("Adding markdown works")
    @MethodSource("actionProvider")
    public void testMarkdownWithEnd(MarkdownAction candidate) {
        String arg = "123";
        MessageFormat mf = new MessageFormat("{0} test");
        String actual = new LocalizedMarkdownBuilder(mf, arg)
                .apply(0, candidate)
                .build();
        assertThat(actual).isEqualTo(candidate.apply(arg) + " test");
    }

    @Test
    @DisplayName("Builder resolves conflicting bold and monospace markdown")
    public void testConflictingMarkdown() {
        String expected = "**`123`**";
        MessageFormat mf = new MessageFormat("{0}");
        String actual = new LocalizedMarkdownBuilder(mf, "123")
                .bold(0)
                .monospace(0)
                .build();
        assertThat(actual).isEqualTo(expected);
        actual = new LocalizedMarkdownBuilder(mf, "123")
                .monospace(0)
                .bold(0)
                .build();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Builder resolves conflicting bold and monospace markdown")
    public void testMultiMarkdown() {
        MessageFormat mf = new MessageFormat("a:{0} b:{1} c:{2}");
        String actual = new LocalizedMarkdownBuilder(mf, "A", "B", "C")
                .bold(0)
                .strike(1)
                .spoiler(2)
                .build();
        assertThat(actual).isEqualTo("a:**A** b:~~B~~ c:||C||");
    }

    @Test
    @DisplayName("Builder can apply markdown to only a specific field")
    public void testMarkdownField() {
        MessageFormat mf = new MessageFormat("{0,number,integer} {0,choice,0#days|1#day|1<days} ago");
        String actual = new LocalizedMarkdownBuilder(mf, 123)
                .underline(0, NumberFormat.Field.INTEGER)
                .build();
        assertThat(actual).isEqualTo("__123__ days ago");
    }

    @Test
    @DisplayName("Builder can apply nested markdown to only a specific field")
    public void testNestedMarkdownField() {
        MessageFormat mf = new MessageFormat("{0,number,integer} {0,choice,0#days|1#day|1<days} ago");
        String actual = new LocalizedMarkdownBuilder(mf, 123)
                .bold(0)
                .underline(0, NumberFormat.Field.INTEGER)
                .build();
        assertThat(actual).isIn(
                "**__123__ days** ago",
                "**__123__** **days** ago",
                "__**123**__ **days** ago"
        );
    }

    private static Stream<MarkdownAction> actionProvider() {
        return actions.stream();
    }

}
