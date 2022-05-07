package com.tisawesomeness.minecord.config.config;

import com.tisawesomeness.minecord.mc.external.MojangAPI;
import com.tisawesomeness.minecord.share.util.Verification;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Value;

import javax.annotation.Nullable;

@Value
public class MojangAPIConfig {
    @JsonProperty("mojangUuidLifetime")
    int mojangUuidLifetime;
    @JsonProperty("mojangPlayerLifetime")
    int mojangPlayerLifetime;
    @JsonProperty("gappleStatusLifetime") @JsonSetter(nulls = Nulls.SET)
    @Nullable Integer gappleStatusLifetime;

    /** Null if electroid API disabled */
    @JsonProperty("electroidCircuitBreaker") @JsonSetter(nulls = Nulls.SET)
    @Nullable CircuitBreakerConfig electroidCircuitBreaker;
    /** Null if gapple API disabled */
    @JsonProperty("gappleCircuitBreaker") @JsonSetter(nulls = Nulls.SET)
    @Nullable CircuitBreakerConfig gappleCircuitBreaker;

    public Verification verify(FlagConfig flagConfig) {
        return Verification.combineAll(
                verifyUuid(),
                verifyPlayer(),
                verifyGapple(flagConfig),
                verifyElectroidBreaker(flagConfig),
                verifyGappleBreaker(flagConfig)
        );
    }

    private Verification verifyUuid() {
        if (mojangUuidLifetime >= MojangAPI.PROFILE_RATELIMIT) {
            return Verification.valid();
        }
        return Verification.invalid(
                "UUIDs must be in the cache for at least " + MojangAPI.PROFILE_RATELIMIT + " seconds");
    }
    private Verification verifyPlayer() {
        if (mojangUuidLifetime >= MojangAPI.PROFILE_RATELIMIT) {
            return Verification.valid();
        }
        return Verification.invalid(
                "Players must be in the cache for at least " + MojangAPI.PROFILE_RATELIMIT + " seconds");
    }
    private Verification verifyGapple(FlagConfig flagConfig) {
        if (gappleStatusLifetime != null) {
            return AdvancedConfig.verifyCacheLifetime(gappleStatusLifetime, "Gapple status");
        }
        return Verification.verify(!flagConfig.isUseGappleAPI(),
                "The gapple status lifetime must be present if the Electroid API is enabled");
    }

    private Verification verifyElectroidBreaker(FlagConfig flagConfig) {
        if (electroidCircuitBreaker != null) {
            return electroidCircuitBreaker.verify();
        }
        return Verification.verify(!flagConfig.isUseElectroidAPI(),
                "The electroid circuit breaker config must be present if the Electroid API is enabled");
    }
    private Verification verifyGappleBreaker(FlagConfig flagConfig) {
        if (gappleCircuitBreaker != null) {
            return gappleCircuitBreaker.verify();
        }
        return Verification.verify(!flagConfig.isUseGappleAPI(),
                "The gapple circuit breaker config must be present if the Gapple API is enabled");
    }

}
