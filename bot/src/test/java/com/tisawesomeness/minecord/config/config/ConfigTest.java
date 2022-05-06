package com.tisawesomeness.minecord.config.config;

import com.tisawesomeness.minecord.BotBrandingTest;
import com.tisawesomeness.minecord.testutil.Resources;
import com.tisawesomeness.minecord.util.type.Verification;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfigTest {

    private static Config config;

    @BeforeAll
    private static void initConfig() throws JsonProcessingException {
        config = Resources.config();
    }

    @Test
    @DisplayName("Default config is valid")
    public void testConfig() {
        Verification v = config.verify();
        assertThat(v.isValid())
                .withFailMessage("Expecting config to be valid, but got errors " + v.getErrors())
                .isTrue();
    }

    /**
     * Used generally everywhere, not a big deal for tests but a HUGE deal for users and docs
     */
    @Test
    @DisplayName("Default config has prefix &")
    public void testDefaultPrefix() {
        assertThat(config.getSettingsConfig().getDefaultPrefix()).isEqualTo("&");
    }
    /**
     * {@link BotBrandingTest#testConfigBranding()}
     */
    @Test
    @DisplayName("Default config is self hosted")
    public void testSelfHosted() {
        assertThat(config.isSelfHosted()).isTrue();
    }

}
