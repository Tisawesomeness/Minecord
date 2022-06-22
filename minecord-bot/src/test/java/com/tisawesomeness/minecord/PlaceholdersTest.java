package com.tisawesomeness.minecord;

import com.tisawesomeness.minecord.common.BuildInfo;
import com.tisawesomeness.minecord.config.config.Config;
import com.tisawesomeness.minecord.testutil.Resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class PlaceholdersTest {

    private static Config config;
    private static final BotBranding branding = new BotBranding();
    private static final BuildInfo buildInfo = BuildInfo.getInstance();

    private static final int DUMMY_SHARD_COUNT = 4;

    @BeforeAll
    private static void initConfig() throws JsonProcessingException {
        config = Resources.config();
    }

    @ParameterizedTest(name = "{index} ==> Placeholder {0} is replaced with {1}")
    @DisplayName("Placeholders by themselves are replaced")
    @MethodSource("placeholderProvider")
    public void testPlaceholders(String placeholder, String value) {
        assertThat(Placeholders.parseConstants(placeholder, config, branding, DUMMY_SHARD_COUNT)).isEqualTo(value);
    }
    @ParameterizedTest(name = "{index} ==> Placeholder {0} is replaced with {1}")
    @DisplayName("Placeholders surrounded by other characters are replaced")
    @MethodSource("placeholderProvider")
    public void testPlaceholdersWithExtra(String placeholder, String value) {
        String input = ">" + placeholder + "<";
        String output = ">" + value + "<";
        assertThat(Placeholders.parseConstants(input, config, branding, DUMMY_SHARD_COUNT)).isEqualTo(output);
    }
    @Test
    @DisplayName("Multiple placeholders can be replaced in the same string")
    public void testMultiplePlaceholders() {
        String input = Placeholders.PREFIX + "user " + Placeholders.AUTHOR_TAG;
        String output = config.getSettingsConfig().getDefaultPrefix() + "user " + branding.getAuthorTag();
        assertThat(Placeholders.parseConstants(input, config, branding, DUMMY_SHARD_COUNT)).isEqualTo(output);
    }

    private static Stream<Arguments> placeholderProvider() {
        return Stream.of(
                arguments(Placeholders.AUTHOR, branding.getAuthor()),
                arguments(Placeholders.AUTHOR_TAG, branding.getAuthorTag()),
                arguments(Placeholders.INVITE, branding.getInvite()),
                arguments(Placeholders.HELP_SERVER, branding.getHelpServer()),
                arguments(Placeholders.WEBSITE, branding.getWebsite()),
                arguments(Placeholders.GITHUB, branding.getGithub()),

                arguments(Placeholders.VERSION, buildInfo.version),
                arguments(Placeholders.JDA_VERSION, buildInfo.jdaVersion),

                arguments(Placeholders.JAVA_VERSION, System.getProperty("java.version")),
                arguments(Placeholders.MC_VERSION, config.getSupportedMCVersion()),
                arguments(Placeholders.BOT_SHARDS, String.valueOf(DUMMY_SHARD_COUNT)),
                arguments(Placeholders.PREFIX, config.getSettingsConfig().getDefaultPrefix())
        );
    }

}
