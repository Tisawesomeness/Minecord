package com.tisawesomeness.minecord.config.config;

import com.tisawesomeness.minecord.mc.external.MojangAPI;
import com.tisawesomeness.minecord.util.type.Verification;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class MojangAPIConfig {
    @JsonProperty("mojangUuidLifetime")
    int mojangUuidLifetime;
    @JsonProperty("mojangPlayerLifetime")
    int mojangPlayerLifetime;
    @JsonProperty("gappleStatusLifetime")
    int gappleStatusLifetime;

    public Verification verify(boolean useGappleAPI) {
        return Verification.combineAll(
                verifyUuid(),
                verifyPlayer(),
                verifyGapple(useGappleAPI)
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
    private Verification verifyGapple(boolean useGappleAPI) {
        if (!useGappleAPI) {
            return Verification.valid();
        }
        return AdvancedConfig.verifyCacheLifetime(gappleStatusLifetime, "Gapple status");
    }

}
