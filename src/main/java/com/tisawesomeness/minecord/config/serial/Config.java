package com.tisawesomeness.minecord.config.serial;

import com.tisawesomeness.minecord.util.type.Verification;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import javax.annotation.Nullable;
import java.util.List;

@Value
public class Config {
    @JsonProperty("token")
    String token;
    @JsonProperty("shardCount")
    int shardCount;
    @JsonProperty("owners")
    List<Long> owners;
    @JsonProperty("logChannel")
    long logChannel;
    @JsonProperty("invite")
    String invite;
    @JsonProperty("presence")
    PresenceConfig presence;
    @JsonProperty("settings")
    SettingsConfig settings;
    @JsonProperty("flags")
    FlagsConfig flags;
    @JsonProperty("botLists")
    BotListConfig botLists;
    @JsonProperty("database")
    DatabaseConfig database;

    public Verification verify() {
        return Verification.combineAll(
                verifyShards(),
                presence.verify(),
                botLists.verify()
        );
    }
    private Verification verifyShards() {
        if (shardCount <= 0) {
            return Verification.invalid("The shard count must be positive!");
        }
        return Verification.valid();
    }

    public boolean isOwner(long id) {
        return owners.contains(id);
    }
    public boolean isOwner(@Nullable String id) {
        if (id == null) {
            return false;
        }
        return owners.stream()
                .map(Object::toString)
                .anyMatch(s -> s.equals(id));
    }
}
