package com.tisawesomeness.minecord.config.config;

import com.tisawesomeness.minecord.util.type.Verification;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Value;

import javax.annotation.Nullable;

@Value
public class AdvancedConfig {
    @JsonProperty("cache")
    CacheConfig cacheConfig;
    @JsonProperty("linkedDeletion") @JsonSetter(nulls = Nulls.SET)
    @Nullable LinkedDeletionConfig linkedDeletionConfig;
    @JsonProperty("http")
    HttpConfig httpConfig;

    public Verification verify(boolean linkedDeletion) {
        return Verification.combineAll(
                cacheConfig.verify(),
                verifyLinkedDeletion(linkedDeletion),
                httpConfig.verify()
        );
    }
    private Verification verifyLinkedDeletion(boolean linkedDeletion) {
        if (!linkedDeletion) {
            return Verification.valid();
        }
        if (linkedDeletionConfig == null) {
            return Verification.invalid("The linked deletion config must be present if linked deletion is enabled");
        }
        return linkedDeletionConfig.verify();
    }

}
