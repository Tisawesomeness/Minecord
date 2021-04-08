package com.tisawesomeness.minecord.config;

import com.tisawesomeness.minecord.config.serial.Config;
import com.tisawesomeness.minecord.util.IOUtils;
import com.tisawesomeness.minecord.util.type.Verification;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidNullException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import org.jetbrains.annotations.TestOnly;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Utility class to read the bot config file.
 */
public class ConfigReader {
    private static final YAMLFactory factory = new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);
    private static final ObjectMapper mapper = buildMapper();

    private static ObjectMapper buildMapper() {
        return JsonMapper.builder(factory)
                .defaultSetterInfo(JsonSetter.Value.forValueNulls(Nulls.FAIL))
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .build();
    }

    /**
     * Reads the config from the file located by the given path.
     * @param path The path to the config file
     * @return The config object
     * @throws IOException If an I/O error occurs
     * @throws InvalidConfigException If the config is invalid, either because the YAML isn't formatted properly
     * or one of the config fields has an invalid value.
     */
    public static Config read(Path path) throws IOException, InvalidConfigException {
        Config config;
        try {
            config = mapper.readValue(path.toFile(), Config.class);
        } catch (InvalidNullException ex) {
            String msg = String.format("The config file is invalid!%n\"%s\" was either not present or null.",
                    ex.getPropertyName());
            throw new InvalidConfigException(msg);
        } catch (JsonProcessingException ex) {
            throw new InvalidConfigException(ex);
        }
        Verification v = config.verify();
        if (v.isValid()) {
            return config;
        }
        throw new InvalidConfigException(v);
    }

    /**
     * Reads the config from the default in resources
     * @return The default config
     * @throws JsonProcessingException If the config is invalid, either because the YAML isn't formatted properly
     * or one of the config fields has an invalid value.
     */
    @TestOnly
    public static Config readFromResources() throws JsonProcessingException {
        return mapper.readValue(IOUtils.loadResource("config.yml"), Config.class);
    }

}
