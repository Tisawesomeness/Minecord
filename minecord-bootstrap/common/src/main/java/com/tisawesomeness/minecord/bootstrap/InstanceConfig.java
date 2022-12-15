package com.tisawesomeness.minecord.bootstrap;

import com.tisawesomeness.minecord.common.HttpConfig;
import com.tisawesomeness.minecord.common.LoadingActivity;
import com.tisawesomeness.minecord.common.config.VerifiableConfig;
import com.tisawesomeness.minecord.common.util.Verification;

import ch.qos.logback.classic.Level;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.ToString;
import lombok.Value;
import org.jetbrains.annotations.NonNls;

import javax.annotation.Nullable;

/**
 * Contains all the values needed to log into Discord, mirroring {@code instance.yml}
 * <br>This class assumes it is being parsed with the
 * {@link com.tisawesomeness.minecord.common.config.ConfigReader} settings.
 */
@Value
public class InstanceConfig implements VerifiableConfig {
    @NonNls
    private static final String DEFAULT_TOKEN = "your token here";

    @JsonProperty("token") @ToString.Exclude
    String token;
    @JsonProperty("shardCount")
    int shardCount;
    @JsonProperty("logLevel")
    Level logLevel;

    @JsonProperty("http")
    HttpConfig httpConfig;
    @JsonProperty("loadingActivity") @JsonSetter(nulls = Nulls.SET)
    @Nullable LoadingActivity loadingActivity;

    /**
     * @return if the token has not been changed from the default dummy value
     */
    public boolean isTokenDefault() {
        return token.equals(DEFAULT_TOKEN);
    }

    /**
     * Checks if this config is valid.
     * <br><b>Do not run the bot with an invalid config!</b>
     * @return A valid Verification only if this config is valid
     */
    public Verification verify() {
        return Verification.combineAll(verifyShards(), httpConfig.verify());
    }
    private Verification verifyShards() {
        return Verification.verify(shardCount > 0, "The shard count must be positive!");
    }

}
