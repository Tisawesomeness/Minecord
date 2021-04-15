package com.tisawesomeness.minecord.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DiscordUtilsTest {

    private static final String ID = "292279711034245130";
    private static final String MENTION = "<@" + ID + ">";
    private static final String NICK_MENTION = "<@!" + ID + ">";

    private static final DiscordUtils.ParseOptions NORMAL = DiscordUtils.parseOptionsBuilder()
            .respondToMentions(true, ID)
            .build();
    private static final DiscordUtils.ParseOptions NO_MENTIONS = DiscordUtils.parseOptionsBuilder().build();
    private static final DiscordUtils.ParseOptions OPTIONAL_PREFIX = DiscordUtils.parseOptionsBuilder()
            .respondToMentions(true, ID)
            .prefixRequired(false)
            .build();

    @Test
    @DisplayName("Command parsing removes literal prefix")
    public void testParsePrefix() {
        assertThat(DiscordUtils.parseCommand("&ping", "&", NORMAL))
                .contains("ping");
    }
    @Test
    @DisplayName("Command parsing removes mention")
    public void testParseMention() {
        assertThat(DiscordUtils.parseCommand(MENTION + " ping", "&", NORMAL))
                .contains("ping");
    }
    @Test
    @DisplayName("Command parsing removes nick mention")
    public void testParseNickMention() {
        assertThat(DiscordUtils.parseCommand(NICK_MENTION + " ping", "&", NORMAL))
                .contains("ping");
    }
    @Test
    @DisplayName("Command parsing removes literal prefix even when string empty")
    public void testParsePrefixEmpty() {
        assertThat(DiscordUtils.parseCommand("&", "&", NORMAL))
                .contains("");
    }
    @Test
    @DisplayName("Command parsing removes mention even when string empty")
    public void testParseMentionEmpty() {
        assertThat(DiscordUtils.parseCommand(MENTION, "&", NORMAL))
                .contains("");
    }
    @Test
    @DisplayName("Command parsing removes nick mention even when string empty")
    public void testParseNickMentionEmpty() {
        assertThat(DiscordUtils.parseCommand(NICK_MENTION, "&", NORMAL))
                .contains("");
    }

    @Test
    @DisplayName("Command parsing fails when no prefix")
    public void testParseNoPrefix() {
        assertThat(DiscordUtils.parseCommand("ping", "&", NO_MENTIONS))
                .isEmpty();
    }
    @Test
    @DisplayName("Command parsing does not remove mention when disabled")
    public void testParseMentionDisabled() {
        assertThat(DiscordUtils.parseCommand(MENTION + " ping", "&", NO_MENTIONS))
                .isEmpty();
    }
    @Test
    @DisplayName("Command parsing does not remove nick mention when disabled")
    public void testParseNickMentionDisabled() {
        assertThat(DiscordUtils.parseCommand(NICK_MENTION + " ping", "&", NO_MENTIONS))
                .isEmpty();
    }

    @Test
    @DisplayName("Command parsing removes literal prefix even when prefix optional")
    public void testParsePrefixOptional() {
        assertThat(DiscordUtils.parseCommand("&ping", "&", OPTIONAL_PREFIX))
                .contains("ping");
    }
    @Test
    @DisplayName("Command parsing removes mention even when prefix optional")
    public void testParseMentionOptional() {
        assertThat(DiscordUtils.parseCommand(MENTION + " ping", "&", OPTIONAL_PREFIX))
                .contains("ping");
    }
    @Test
    @DisplayName("Command parsing removes nick mention even when prefix optional")
    public void testParseNickMentionOptional() {
        assertThat(DiscordUtils.parseCommand(NICK_MENTION + " ping", "&", OPTIONAL_PREFIX))
                .contains("ping");
    }
    @Test
    @DisplayName("Command parsing doesn't require prefix when prefix optional")
    public void testParseNoPrefixOptional() {
        assertThat(DiscordUtils.parseCommand("ping", "&", OPTIONAL_PREFIX))
                .contains("ping");
    }

}
