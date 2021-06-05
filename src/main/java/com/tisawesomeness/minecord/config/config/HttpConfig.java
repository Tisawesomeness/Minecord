package com.tisawesomeness.minecord.config.config;

import com.tisawesomeness.minecord.util.type.Verification;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class HttpConfig {
    @JsonProperty("maxRequestsPerHost")
    int maxRequestsPerHost;
    @JsonProperty("maxIdleConnections")
    int maxIdleConnections;
    @JsonProperty("keepAlive")
    int keepAlive;

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
