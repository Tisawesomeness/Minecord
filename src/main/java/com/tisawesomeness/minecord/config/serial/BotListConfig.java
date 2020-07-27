package com.tisawesomeness.minecord.config.serial;

import com.tisawesomeness.minecord.util.type.Verification;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Value;

@Value
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BotListConfig {
    public static final int MAX_PORT = 65536;

    @JsonProperty("sendServerCount")
    boolean sendServerCount;
    @JsonProperty("sendGuildsInterval") @JsonSetter(nulls = Nulls.SET)
    int sendGuildsInterval;
    @JsonProperty("pwToken") @JsonSetter(nulls = Nulls.SET)
    String pwToken;
    @JsonProperty("orgToken") @JsonSetter(nulls = Nulls.SET)
    String orgToken;
    @JsonProperty("receiveVotes")
    boolean receiveVotes;
    @JsonProperty("webhookUrl") @JsonSetter(nulls = Nulls.SET)
    String webhookUrl;
    @JsonProperty("webhookPort") @JsonSetter(nulls = Nulls.SET)
    int webhookPort;
    @JsonProperty("webhookAuth") @JsonSetter(nulls = Nulls.SET)
    String webhookAuth;

    public Verification verify() {
        return Verification.combineAll(
                verifySendGuildsInterval(),
                verifyWebhookUrl(),
                verifyWebhookPort()
        );
    }
    private Verification verifySendGuildsInterval() {
        if (sendServerCount && (sendGuildsInterval < -1 || sendGuildsInterval == 0)) {
            return Verification.invalid(
                    "If sendServerCount is true, then sendGuildsInterval must be -1 or positive.");
        }
        return Verification.valid();
    }
    private Verification verifyWebhookUrl() {
        if (receiveVotes && webhookUrl == null) {
            return Verification.invalid("If receiveVotes is true, you must also set webhookUrl.");
        }
        return Verification.valid();
    }
    private Verification verifyWebhookPort() {
        if (receiveVotes && (webhookPort <= 0 || MAX_PORT < webhookPort)) {
            String msg = String.format(
                    "If receiveVotes is true, then webhookPort must be between 0 and %s.", MAX_PORT);
            return Verification.invalid(msg);
        }
        return Verification.valid();
    }
}
