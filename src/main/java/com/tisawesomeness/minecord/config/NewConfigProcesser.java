package com.tisawesomeness.minecord.config;

import com.tisawesomeness.minecord.config.serial.Config;
import com.tisawesomeness.minecord.util.RequestUtils;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

public final class NewConfigProcesser {
    public static void main(String[] args) {
        YAMLFactory factory = new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);
        ObjectMapper mapper = JsonMapper.builder(factory)
                .defaultSetterInfo(JsonSetter.Value.forValueNulls(Nulls.FAIL))
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .build();
        String input = RequestUtils.loadResource("newConfig.yml");

        Config config = null;
        try {
            config = mapper.readValue(input, Config.class);
            System.out.println(mapper.writeValueAsString(config));
        } catch (JsonProcessingException ex) {
            System.out.println(ex.getMessage());
            System.exit(1); // this isn't prod code, I can do whatever I want
        }

        config.verify().getErrors().forEach(System.err::println);
    }
}
