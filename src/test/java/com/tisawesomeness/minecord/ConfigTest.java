package com.tisawesomeness.minecord;

import com.tisawesomeness.minecord.config.ConfigReader;
import com.tisawesomeness.minecord.config.config.Config;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfigTest {

    private static Config config;

    @BeforeAll
    private static void initConfig() throws JsonProcessingException {
        config = ConfigReader.readFromResources();
    }

    @Test
    @DisplayName("Default config initializes properly")
    public void testConfig() {
        assertThat(config).isNotNull();
    }

}
