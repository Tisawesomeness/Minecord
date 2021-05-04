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
        if (maxDeletes < 1) {
            return Verification.invalid("The maximum replies to delete must be positive");
        }
        return Verification.valid();
    }
    private Verification verifyInitialCapacity() {
        if (initialCapacity < 1) {
            return Verification.invalid("The initial capacity must be positive");
        }
        return Verification.valid();
    }
    private Verification verifyLoadFactor() {
        if (0.0 <= loadFactor && loadFactor <= 1.0) {
            return Verification.valid();
        }
        return Verification.invalid("The load factor must be between 0 and 1");
    }
    private Verification verifyConcurrencyLevel() {
        if (concurrencyLevel < 1) {
            return Verification.invalid("The concurrency level must be positive");
        }
        return Verification.valid();
    }

}
