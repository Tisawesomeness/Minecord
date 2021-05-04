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
        if (maxRequestsPerHost < 1) {
            return Verification.invalid("Max requests per host must be positive");
        }
        return Verification.valid();
    }
    private Verification verifyMaxIdleConnections() {
        if (maxIdleConnections < 1) {
            return Verification.invalid("Max idle connections must be positive");
        }
        return Verification.valid();
    }
    private Verification verifyKeepAlive() {
        if (keepAlive < 1) {
            return Verification.invalid("Keep alive time must be positive");
        }
        return Verification.valid();
    }

}
