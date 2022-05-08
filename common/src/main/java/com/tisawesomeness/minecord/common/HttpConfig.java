package com.tisawesomeness.minecord.common;

import com.tisawesomeness.minecord.common.config.VerifiableConfig;
import com.tisawesomeness.minecord.common.util.Verification;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

/**
 * Contains the config values for the HTTP client.
 * <br>This class assumes it is being parsed with the
 * {@link com.tisawesomeness.minecord.common.config.ConfigReader} settings.
 */
@Value
public class HttpConfig implements VerifiableConfig {
    @JsonProperty("maxRequestsPerHost")
    int maxRequestsPerHost;
    @JsonProperty("maxIdleConnections")
    int maxIdleConnections;
    @JsonProperty("keepAlive")
    int keepAlive;

    /**
     * Checks if this config is valid.
     * <br><b>Do not run the bot with an invalid config!</b>
     * @return A valid Verification only if this config is valid
     */
    public Verification verify() {
        return Verification.combineAll(
                verifyMaxRequestsPerHost(),
                verifyMaxIdleConnections(),
                verifyKeepAlive()
        );
    }

    private Verification verifyMaxRequestsPerHost() {
        return Verification.verify(maxRequestsPerHost > 0, "Max requests per host must be positive");
    }
    private Verification verifyMaxIdleConnections() {
        return Verification.verify(maxRequestsPerHost > 0, "Max idle connections must be positive");
    }
    private Verification verifyKeepAlive() {
        return Verification.verify(keepAlive > 0, "Keep alive time must be positive");
    }

}
