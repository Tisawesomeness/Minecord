package com.tisawesomeness.minecord.config.config;

import com.tisawesomeness.minecord.config.VerifiableConfig;
import com.tisawesomeness.minecord.util.type.Verification;

import ch.qos.logback.classic.Level;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.ToString;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Contains all the values that changes how the bot functions, mirroring {@code config.yml}.
 * <br>This class assumes it is being parsed with nulls failing by default, which can be set with:
 * <pre>{@code
 *     ObjectMapper mapper = ...;
 *     mapper.setDefaultSetterInfo(JsonSetter.Value.forValueNulls(Nulls.FAIL))
 * }</pre>
 * Optional fields are annotated with {@code @JsonSetter(nulls = Nulls.SET)}.
 */
@Value
@Slf4j
public class Config implements VerifiableConfig {
    @JsonProperty("token") @ToString.Exclude
    String token;
    @JsonProperty("shardCount")
    int shardCount;
    @JsonProperty("owners")
    List<Long> owners;
    @JsonProperty("logChannelId")
    long logChannelId;
    @JsonProperty("logLevel")
    Level logLevel;
    @JsonProperty("isSelfHosted")
    boolean isSelfHosted;

    @JsonProperty("supportedMCVersion")
    String supportedMCVersion;

    @JsonProperty("settings")
    SettingsConfig settingsConfig;
    @JsonProperty("flags")
    FlagConfig flagConfig;
    @JsonProperty("botLists") @JsonSetter(nulls = Nulls.SET)
    @Nullable BotListConfig botListConfig;
    @JsonProperty("database")
    DatabaseConfig databaseConfig;
    @JsonProperty("commands")
    CommandConfig commandConfig;
    @JsonProperty("advanced")
    AdvancedConfig advancedConfig;

    /**
     * Checks if this config is valid.
     * <br>Missing or misformatted fields are handled by the YAML parser, while this method handles everything else.
     * <br><b>Do not run the bot with an invalid config!</b>
     * @return A valid Verification only if this config is valid
     */
    public Verification verify() {
        if (owners.isEmpty()) {
            log.warn("The list of owners in the config is empty. Add your user ID to get access to admin commands.");
        }
        return Verification.combineAll(
                verifyShards(),
                settingsConfig.verify(),
                verifyBotListConfig(),
                commandConfig.verify(),
                advancedConfig.verify(flagConfig)
        );
    }
    private Verification verifyShards() {
        return Verification.verify(shardCount > 0, "The shard count must be positive!");
    }
    private Verification verifyBotListConfig() {
        if (botListConfig == null) {
            return Verification.valid();
        }
        return botListConfig.verify();
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
