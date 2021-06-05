package com.tisawesomeness.minecord.config.config;

import com.tisawesomeness.minecord.util.type.Verification;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class LinkedDeletionConfig {
    @JsonProperty("maxDeletes")
    int maxDeletes;
    @JsonProperty("initialCapacity")
    int initialCapacity;
    @JsonProperty("loadFactor")
    float loadFactor;
    @JsonProperty("concurrencyLevel")
    int concurrencyLevel;

    public Verification verify() {
        return Verification.combineAll(
                verifyMaxDeletes(),
                verifyInitialCapacity(),
                verifyLoadFactor(),
                verifyConcurrencyLevel()
        );
    }

    private Verification verifyMaxDeletes() {
        return Verification.verify(maxDeletes > 0, "The maximum replies to delete must be positive");
    }
    private Verification verifyInitialCapacity() {
        return Verification.verify(initialCapacity > 0, "The initial capacity must be positive");
    }
    private Verification verifyLoadFactor() {
        return Verification.verify(0.0 <= loadFactor && loadFactor <= 1.0, "The load factor must be between 0 and 1");
    }
    private Verification verifyConcurrencyLevel() {
        return Verification.verify(concurrencyLevel > 0, "The concurrency level must be positive");
    }

}
