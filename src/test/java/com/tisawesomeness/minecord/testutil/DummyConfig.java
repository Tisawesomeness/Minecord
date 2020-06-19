package com.tisawesomeness.minecord.testutil;

import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.util.RequestUtils;

import java.io.IOException;

public class DummyConfig extends Config {
    /**
     * Reads data from the config file.
     * @throws IOException When the config file couldn't be loaded.
     */
    public DummyConfig() throws IOException {
        super(RequestUtils.loadJSONResource("config.json"), null);
    }
}
