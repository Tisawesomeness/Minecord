package com.tisawesomeness.minecord.config.config;

import com.tisawesomeness.minecord.util.type.Verification;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.NonNull;
import lombok.Value;

import javax.annotation.Nullable;

@Value
public class AdvancedConfig {
    @JsonProperty("http")
    HttpConfig httpConfig;
    @JsonProperty("mojangAPI")
    MojangAPIConfig mojangAPIConfig;
    @JsonProperty("databaseCache")
    CacheConfig databaseCacheConfig;
    @JsonProperty("discord")
    AdvDiscordConfig advDiscordConfig;
    @JsonProperty("linkedDeletion") @JsonSetter(nulls = Nulls.SET)
    @Nullable LinkedDeletionConfig linkedDeletionConfig;

    public Verification verify(FlagConfig flagConfig) {
        return Verification.combineAll(
                httpConfig.verify(),
                mojangAPIConfig.verify(flagConfig),
                databaseCacheConfig.verify(),
                advDiscordConfig.verify(),
                verifyLinkedDeletion(flagConfig.isLinkedDeletion())
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

    public static Verification verifyCacheLifetime(long lifetime, @NonNull String cacheName) {
        return Verification.verify(lifetime >= 0, cacheName + " lifetime cannot be negative");
    }
    public static Verification verifyCacheMaxSize(long maxSize, @NonNull String cacheName) {
        return Verification.verify(maxSize >= -1, cacheName + " cache max size must be -1, 0, or positive");
    }

}
