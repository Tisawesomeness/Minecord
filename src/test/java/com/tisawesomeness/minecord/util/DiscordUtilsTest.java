package com.tisawesomeness.minecord.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DiscordUtilsTest {

    private static final String ID = "292279711034245130";
    private static final String MENTION = "<@" + ID + ">";
    private static final String NICK_MENTION = "<@!" + ID + ">";

    @Test
    @DisplayName("Command parsing removes literal prefix")
    public void testParsePrefix() {
        assertThat(DiscordUtils.parseCommand("&ping", "&", ID, true))
                .contains("ping");
    }
    @Test
    @DisplayName("Command parsing removes mention")
    public void testParseMention() {
        assertThat(DiscordUtils.parseCommand(MENTION + " ping", "&", ID, true))
                .contains("ping");
    }
    @Test
    @DisplayName("Command parsing removes nick mention")
    public void testParseNickMention() {
        assertThat(DiscordUtils.parseCommand(NICK_MENTION + " ping", "&", ID, true))
                .contains("ping");
    }
    @Test
    @DisplayName("Command parsing fails when no prefix")
    public void testParseNoPrefix() {
        assertThat(DiscordUtils.parseCommand("ping", "&", ID, false))
                .isEmpty();
    }
    @Test
    @DisplayName("Command parsing does not remove mention when disabled")
    public void testParseMentionDisabled() {
        assertThat(DiscordUtils.parseCommand(MENTION + " ping", "&", ID, false))
                .isEmpty();
    }
    @Test
    @DisplayName("Command parsing does not remove nick mention when disabled")
    public void testParseNickMentionDisabled() {
        assertThat(DiscordUtils.parseCommand(NICK_MENTION + " ping", "&", ID, false))
                .isEmpty();
    }
    @Test
    @DisplayName("Command parsing removes literal prefix even when string empty")
    public void testParsePrefixEmpty() {
        assertThat(DiscordUtils.parseCommand("&", "&", ID, true))
                .contains("");
    }
    @Test
    @DisplayName("Command parsing removes mention even when string empty")
    public void testParseMentionEmpty() {
        assertThat(DiscordUtils.parseCommand(MENTION, "&", ID, true))
                .contains("");
    }
    @Test
    @DisplayName("Command parsing removes nick mention even when string empty")
    public void testParseNickMentionEmpty() {
        assertThat(DiscordUtils.parseCommand(NICK_MENTION, "&", ID, true))
                .contains("");
    }

}
