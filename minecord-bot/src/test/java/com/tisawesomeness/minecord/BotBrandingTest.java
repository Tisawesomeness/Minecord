package com.tisawesomeness.minecord;

import com.tisawesomeness.minecord.config.branding.Branding;
import com.tisawesomeness.minecord.config.branding.BrandingConfig;
import com.tisawesomeness.minecord.config.config.Config;
import com.tisawesomeness.minecord.testutil.Reflect;
import com.tisawesomeness.minecord.testutil.Resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BotBrandingTest {

    private static final Object[] DEFAULT_BRANDING = {
            BotBranding.AUTHOR,
            BotBranding.AUTHOR_TAG,
            BotBranding.INVITE,
            BotBranding.HELP_SERVER,
            BotBranding.WEBSITE,
            BotBranding.GITHUB
    };

    private static Config config;
    private static Branding brand;
    private static Config notSelfHostedConfig;

    @BeforeAll
    private static void initConfig() throws JsonProcessingException, NoSuchFieldException {
        config = Resources.config();
        brand = Resources.branding();

        Config tempConfig = Resources.config();
        Reflect.setField(tempConfig, "isSelfHosted", false);
        notSelfHostedConfig = tempConfig;
    }

    @Test
    @DisplayName("Default branding contains default values")
    public void testDefaultBranding() {
        BotBranding branding = new BotBranding();
        assertFieldsContains(branding, DEFAULT_BRANDING);
    }
    @Test
    @DisplayName("Configured branding contains config values")
    public void testConfigBranding() {
        BrandingConfig bc = brand.getBrandingConfig();
        assertThat(bc).isNotNull();
        BotBranding branding = new BotBranding(config, brand);
        assertFieldsContains(branding,
                bc.getAuthor(),
                bc.getAuthorTag(),
                bc.getInvite(),
                bc.getHelpServer(),
                bc.getWebsite(),
                bc.getGithub()
        );
    }
    @Test
    @DisplayName("Branding forces default values if the bot is not self-hosted")
    public void testConfigNotSelfHosted() {
        BotBranding branding = new BotBranding(notSelfHostedConfig, brand);
        assertThat(notSelfHostedConfig.isSelfHosted()).isFalse();
        assertFieldsContains(branding, DEFAULT_BRANDING);
    }

    private static void assertFieldsContains(BotBranding branding, Object... values) {
        assertThat(branding).extracting(
                BotBranding::getAuthor,
                BotBranding::getAuthorTag,
                BotBranding::getInvite,
                BotBranding::getHelpServer,
                BotBranding::getWebsite,
                BotBranding::getGithub
        ).containsExactly(values);
    }

}
