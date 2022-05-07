package com.tisawesomeness.minecord.testutil;

import com.tisawesomeness.minecord.config.branding.Branding;
import com.tisawesomeness.minecord.config.config.Config;
import com.tisawesomeness.minecord.share.config.ConfigReader;
import com.tisawesomeness.minecord.share.config.VerifiableConfig;
import com.tisawesomeness.minecord.share.util.IO;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.NonNull;

public class Resources {

    public static @NonNull Config config() throws JsonProcessingException {
        return readFromResources("config.yml", Config.class);
    }
    public static @NonNull Branding branding() throws JsonProcessingException {
        return readFromResources("branding.yml", Branding.class);
    }

    private static <K extends VerifiableConfig> @NonNull K readFromResources(@NonNull String resource, Class<K> clazz)
            throws JsonProcessingException {
        return ConfigReader.getMapper().readValue(IO.loadResource(resource), clazz);
    }

}
