package com.tisawesomeness.minecord.testutil;

import com.tisawesomeness.minecord.config.ConfigReader;
import com.tisawesomeness.minecord.config.branding.Branding;
import com.tisawesomeness.minecord.config.config.Config;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.NonNull;

public class Resources {
    public static @NonNull Config config() throws JsonProcessingException {
        return ConfigReader.readFromResources("config.yml", Config.class);
    }
    public static @NonNull Branding branding() throws JsonProcessingException {
        return ConfigReader.readFromResources("branding.yml", Branding.class);
    }
}
