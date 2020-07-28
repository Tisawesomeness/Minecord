package com.tisawesomeness.minecord.config.serial;

import com.tisawesomeness.minecord.util.type.Verification;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Contains all the values that changes how the bot function, mirroring {@code config.yml}.
 * <br>This class assumes it is being parsed with nulls failing by default, which can be set with:
 * <pre>{@code
 *     ObjectMapper mapper = ...;
 *     mapper.setDefaultSetterInfo(JsonSetter.Value.forValueNulls(Nulls.FAIL))
 * }</pre>
 * Optional fields are annotated with {@code @JsonSetter(nulls = Nulls.SET)}.
 */
@Value
public class Config {
    @JsonProperty("token")
    String token;
    @JsonProperty("shardCount")
    int shardCount;
    @JsonProperty("owners")
    List<Long> owners;
    @JsonProperty("logChannelId")
    long logChannelId;
    @JsonProperty("inviteLink")
    String inviteLink;
    @JsonProperty("presence")
    PresenceConfig presenceConfig;
    @JsonProperty("settings")
    SettingsConfig settingsConfig;
    @JsonProperty("flags")
    FlagConfig flagConfig;
    @JsonProperty("botLists")
    BotListConfig botListConfig;
    @JsonProperty("database")
    DatabaseConfig databaseConfig;
    @JsonProperty("commands")
    CommandConfig commandConfig;

    /**
     * Checks if this config is valid.
     * <br>Missing or misformatted fields are handled by the YAML parser, while this method handles everything else.
     * <br><b>Do not run the bot with an invalid config!</b>
     * @return A valid Verification only if this config is valid
     */
    public Verification verify() {
        return Verification.combineAll(
                verifyShards(),
                presenceConfig.verify(),
                settingsConfig.verify(),
                botListConfig.verify()
        );
    }
    private Verification verifyShards() {
        if (shardCount <= 0) {
            return Verification.invalid("The shard count must be positive!");
        }
        return Verification.valid();
    }

    /**
     * Determines if the given ID is listed in the config as an owner.
     * <br>The config is not guarenteed to have any owners.
     * @param id The 17-20 digit ID, though invalid IDs return false
     */
    public boolean isOwner(long id) {
        return owners.contains(id);
    }
    /**
     * Determines if the given ID is listed in the config as an owner.
     * <br>The config is not guarenteed to have any owners.
     * @param id The 17-20 digit string ID (this method is safe for any input)
     */
    public boolean isOwner(@Nullable String id) {
        if (id == null) {
            return false;
        }
        return owners.stream()
                .map(Object::toString)
                .anyMatch(s -> s.equals(id));
    }
}
