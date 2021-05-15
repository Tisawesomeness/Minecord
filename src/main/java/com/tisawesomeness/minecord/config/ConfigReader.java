package com.tisawesomeness.minecord.config;

import com.tisawesomeness.minecord.util.IO;
import com.tisawesomeness.minecord.util.type.Verification;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidNullException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import lombok.NonNull;
import org.jetbrains.annotations.TestOnly;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Utility class to read the bot config files.
 */
public class ConfigReader {
    private static final YAMLFactory factory = new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);
    private static final ObjectMapper mapper = buildMapper();

    private static ObjectMapper buildMapper() {
        return JsonMapper.builder(factory)
                .defaultSetterInfo(JsonSetter.Value.forValueNulls(Nulls.FAIL))
                .serializationInclusion(JsonInclude.Include.NON_NULL)
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
    public static <K extends VerifiableConfig> @NonNull K read(@NonNull Path path, Class<K> clazz)
            throws IOException, InvalidConfigException {
        K obj;
        try {
            obj = mapper.readValue(path.toFile(), clazz);
        } catch (InvalidNullException ex) {
            String msg = String.format("The config file is invalid!%n\"%s\" was either not present or null.",
                    ex.getPropertyName());
            throw new InvalidConfigException(msg);
        } catch (JsonProcessingException ex) {
            throw new InvalidConfigException(ex);
        }
        Verification v = obj.verify();
        if (v.isValid()) {
            return obj;
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
    public static <K extends VerifiableConfig> @NonNull K readFromResources(@NonNull String resource, Class<K> clazz)
            throws JsonProcessingException {
        return mapper.readValue(IO.loadResource(resource), clazz);
    }

}
