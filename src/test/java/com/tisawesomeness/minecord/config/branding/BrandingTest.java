package com.tisawesomeness.minecord.config.branding;

import com.tisawesomeness.minecord.testutil.Resources;
import com.tisawesomeness.minecord.util.type.Verification;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BrandingTest {

    private static Branding branding;

    @BeforeAll
    private static void initConfig() throws JsonProcessingException {
        branding = Resources.branding();
    }

    @Test
    @DisplayName("Default branding is valid")
    public void testConfig() {
        Verification v = branding.verify();
        assertThat(v.isValid())
                .withFailMessage("Expecting branding to be valid, but got errors " + v.getErrors())
                .isTrue();
    }

}
