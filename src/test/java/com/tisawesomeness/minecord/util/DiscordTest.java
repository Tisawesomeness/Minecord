package com.tisawesomeness.minecord.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class DiscordTest {

    private static final String ID = "292279711034245130";
    private static final String MENTION = "<@" + ID + ">";
    private static final String NICK_MENTION = "<@!" + ID + ">";

    private static final Discord.ParseOptions NORMAL = Discord.parseOptionsBuilder()
            .respondToMentions(true, ID)
            .build();
    private static final Discord.ParseOptions NO_MENTIONS = Discord.parseOptionsBuilder().build();
    private static final Discord.ParseOptions MANUAL_NO_MENTIONS = Discord.parseOptionsBuilder()
            .respondToMentions(false, null)
            .build();
    private static final Discord.ParseOptions OPTIONAL_PREFIX = Discord.parseOptionsBuilder()
            .respondToMentions(true, ID)
            .prefixRequired(false)
            .build();

    @Test
    @DisplayName("Command parsing removes literal prefix")
    public void testParsePrefix() {
        assertThat(Discord.parseCommand("&ping", "&", NORMAL))
                .contains("ping");
    }
    @Test
    @DisplayName("Command parsing removes literal prefix plus space")
    public void testParsePrefixSpace() {
        assertThat(Discord.parseCommand("& ping", "&", NORMAL))
                .contains("ping");
    }
    @Test
    @DisplayName("Command parsing removes mention")
    public void testParseMention() {
        assertThat(Discord.parseCommand(MENTION + " ping", "&", NORMAL))
                .contains("ping");
    }
    @Test
    @DisplayName("Command parsing removes nick mention")
    public void testParseNickMention() {
        assertThat(Discord.parseCommand(NICK_MENTION + " ping", "&", NORMAL))
                .contains("ping");
    }
    @Test
    @DisplayName("Command parsing removes literal prefix even when string empty")
    public void testParsePrefixEmpty() {
        assertThat(Discord.parseCommand("&", "&", NORMAL))
                .contains("");
    }
    @Test
    @DisplayName("Command parsing removes literal prefix plus space even when string empty")
    public void testParsePrefixEmptySpace() {
        assertThat(Discord.parseCommand("& ", "&", NORMAL))
                .contains("");
    }
    @Test
    @DisplayName("Command parsing removes mention even when string empty")
    public void testParseMentionEmpty() {
        assertThat(Discord.parseCommand(MENTION, "&", NORMAL))
                .contains("");
    }
    @Test
    @DisplayName("Command parsing removes nick mention even when string empty")
    public void testParseNickMentionEmpty() {
        assertThat(Discord.parseCommand(NICK_MENTION, "&", NORMAL))
                .contains("");
    }

    @Test
    @DisplayName("Command parsing fails when no prefix")
    public void testParseNoPrefix() {
        assertThat(Discord.parseCommand("ping", "&", NO_MENTIONS))
                .isEmpty();
    }
    @Test
    @DisplayName("Command parsing does not remove mention when disabled")
    public void testParseMentionDisabled() {
        assertThat(Discord.parseCommand(MENTION + " ping", "&", NO_MENTIONS))
                .isEmpty();
    }
    @Test
    @DisplayName("Command parsing does not remove nick mention when disabled")
    public void testParseNickMentionDisabled() {
        assertThat(Discord.parseCommand(NICK_MENTION + " ping", "&", NO_MENTIONS))
                .isEmpty();
    }
    @Test
    @DisplayName("Command parsing does not remove mention when disabled manually")
    public void testParseMentionDisabledManual() {
        assertThat(Discord.parseCommand(MENTION + " ping", "&", MANUAL_NO_MENTIONS))
                .isEmpty();
    }

    @Test
    @DisplayName("Command parsing removes literal prefix even when prefix optional")
    public void testParsePrefixOptional() {
        assertThat(Discord.parseCommand("&ping", "&", OPTIONAL_PREFIX))
                .contains("ping");
    }
    @Test
    @DisplayName("Command parsing removes literal prefix  plus spaceeven when prefix optional")
    public void testParsePrefixOptionalSpace() {
        assertThat(Discord.parseCommand("& ping", "&", OPTIONAL_PREFIX))
                .contains("ping");
    }
    @Test
    @DisplayName("Command parsing removes mention even when prefix optional")
    public void testParseMentionOptional() {
        assertThat(Discord.parseCommand(MENTION + " ping", "&", OPTIONAL_PREFIX))
                .contains("ping");
    }
    @Test
    @DisplayName("Command parsing removes nick mention even when prefix optional")
    public void testParseNickMentionOptional() {
        assertThat(Discord.parseCommand(NICK_MENTION + " ping", "&", OPTIONAL_PREFIX))
                .contains("ping");
    }
    @Test
    @DisplayName("Command parsing doesn't require prefix when prefix optional")
    public void testParseNoPrefixOptional() {
        assertThat(Discord.parseCommand("ping", "&", OPTIONAL_PREFIX))
                .contains("ping");
    }

    @Test
    @DisplayName("Providing a null self id when responding to mentions throws NPE")
    public void testParseBuilderNullSelfId() {
        assertThatThrownBy(() -> Discord.parseOptionsBuilder().respondToMentions(true, null))
                .isInstanceOf(NullPointerException.class);
    }

}
