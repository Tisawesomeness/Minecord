package com.tisawesomeness.minecord;

import com.tisawesomeness.minecord.config.branding.AnnouncementConfig;
import com.tisawesomeness.minecord.config.config.Config;
import com.tisawesomeness.minecord.testutil.Resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AnnounceRegistryTest {

    @Test
    @DisplayName("The announcement registry inits with no problems")
    public void testInit() throws JsonProcessingException {
        Config config = Resources.config();
        BotBranding branding = new BotBranding();
        AnnouncementConfig annConf = Resources.branding().getAnnouncementConfig();
        assertThat(new AnnounceRegistry(config, branding, annConf, 1)).isNotNull();
    }

}
