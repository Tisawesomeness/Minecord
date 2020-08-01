package com.tisawesomeness.minecord.config;

import com.tisawesomeness.minecord.config.serial.Config;
import com.tisawesomeness.minecord.util.RequestUtils;
import com.tisawesomeness.minecord.util.type.Verification;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import org.jetbrains.annotations.TestOnly;

import java.io.IOException;
import java.nio.file.Path;

public class ConfigReader {
    private static final YAMLFactory factory = new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);
    private static final ObjectMapper mapper = buildMapper();

    public static ObjectMapper buildMapper() {
        return JsonMapper.builder(factory)
                .defaultSetterInfo(JsonSetter.Value.forValueNulls(Nulls.FAIL))
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .build();
    }

    public static Config read(Path path) {
        Config config;
        try {
            config = mapper.readValue(path.toFile(), Config.class);
        } catch (IOException ex) {
            throw new InvalidConfigException(ex);
        }
        Verification v = config.verify();
        if (v.isValid()) {
            return config;
        }
        throw new InvalidConfigException(v);
    }
    @TestOnly
    public static Config readFromResources() throws JsonProcessingException {
        return mapper.readValue(RequestUtils.loadResource("config.yml"), Config.class);
    }

    private static class InvalidConfigException extends RuntimeException {
        public InvalidConfigException(Verification v) {
            super("The config file is invalid!\n" + String.join("\n", v.getErrors()));
        }
        public InvalidConfigException(Throwable cause) {
            super(cause);
        }
    }
}
