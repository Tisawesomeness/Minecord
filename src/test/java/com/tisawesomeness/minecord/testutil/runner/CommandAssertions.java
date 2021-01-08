package com.tisawesomeness.minecord.testutil.runner;

import net.dv8tion.jda.api.entities.MessageEmbed;

/**
 * Assertions for command objects
 */
public class CommandAssertions {

    public static TestContextAssert assertThat(TestContext actual) {
        return TestContextAssert.assertThat(actual);
    }
    public static MessageEmbedAssert assertThat(MessageEmbed actual) {
        return MessageEmbedAssert.assertThat(actual);
    }

}
