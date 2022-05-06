package com.tisawesomeness.minecord;

import com.tisawesomeness.minecord.config.config.BotListConfig;
import com.tisawesomeness.minecord.config.config.Config;
import com.tisawesomeness.minecord.util.Mth;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.OptionalInt;

/**
 * Holds secrets that are either pulled from the config or environment variables.
 */
@Getter
@Slf4j
public class Secrets {

    public static final String TOKEN_ENV_VAR = "MINECORD_TOKEN";
    public static final String PW_TOKEN_ENV_VAR = "MINECORD_PW_TOKEN";
    public static final String ORG_TOKEN_ENV_VAR = "MINECORD_ORG_TOKEN";
    public static final String WEBHOOK_URL_ENV_VAR = "MINECORD_WEBHOOK_URL";
    public static final String WEBHOOK_PORT_ENV_VAR = "MINECORD_WEBHOOK_PORT";
    public static final String WEBHOOK_AUTH_ENV_VAR = "MINECORD_WEBHOOK_AUTH";

    public static final String REDACTED = "[redacted]";
    private static final int MAX_PORT = 65536;

    private final @NonNull String token;
    private final @Nullable String pwToken;
    private final @Nullable String orgToken;
    private final @Nullable String webhookUrl;
    private final int webhookPort;
    private final @Nullable String webhookAuth;

    /**
     * Initializes the secrets registry
     * @param config The config to fall back to if environment variables are not present
     */
    public Secrets(@NonNull Config config) {
        BotListConfig blc = config.getBotListConfig();

        token = from(TOKEN_ENV_VAR, config.getToken());
        if (blc != null) {
            pwToken = from(PW_TOKEN_ENV_VAR, blc.getPwToken());
            orgToken = from(ORG_TOKEN_ENV_VAR, blc.getOrgToken());
        } else {
            pwToken = from(PW_TOKEN_ENV_VAR);
            orgToken = from(ORG_TOKEN_ENV_VAR);
        }

        if (blc != null && blc.isReceiveVotes()) {
            String webhookUrlTemp = parseWebhookUrl(blc);
            int webhookPortTemp = parseWebhookPort(blc);
            if (webhookUrlTemp != null && webhookPortTemp != 0) {
                webhookUrl = webhookUrlTemp;
                webhookPort = webhookPortTemp;
                webhookAuth = from(WEBHOOK_AUTH_ENV_VAR, blc.getWebhookAuth());
                return;
            }
        }
        webhookUrl = null;
        webhookPort = 0;
        webhookAuth = null;
    }

    private static String parseWebhookUrl(BotListConfig blc) {
        String webhookUrl = from(WEBHOOK_URL_ENV_VAR, blc.getWebhookUrl());
        if (webhookUrl == null) {
            log.warn("If receiveVotes is true, you must also set webhookUrl. Webhook disabled.");
        }
        return webhookUrl;
    }
    private static int parseWebhookPort(BotListConfig blc) {
        String webhookPortStr = from(WEBHOOK_PORT_ENV_VAR);
        if (webhookPortStr == null) {
            return blc.getWebhookPort();
        }
        OptionalInt oi = Mth.safeParseInt(WEBHOOK_PORT_ENV_VAR);
        if (!oi.isPresent()) {
            return blc.getWebhookPort();
        }
        int webhookPort = oi.getAsInt();
        if (webhookPort <= 0 || MAX_PORT < webhookPort) {
            String msg = String.format("If receiveVotes is true, then webhookPort must be between 0 and %s. " +
                    "Webhook disabled.", MAX_PORT);
            log.warn(msg);
            return 0;
        }
        return webhookPort;
    }

    private static String from(String envVarName) {
        return from(envVarName, null);
    }
    private static String from(String envVarName, String fallback) {
        String env = System.getenv(envVarName);
        if (env != null) {
            log.info("Found env var override for " + envVarName);
            return env;
        }
        return fallback;
    }

    /**
     * Cleans secrets from a string. <b>THIS WILL NOT CATCH ALL ATTEMPTS TO LEAK THE TOKEN!
     * DO NOT USE THIS METHOD AS A REPLACEMENT FOR PROPER SECURITY!</b>
     * @param str The input string
     * @return The string with secrets replaced with {@link #REDACTED}
     */
    public @NonNull String clean(@NonNull String str) {
        String[] arr = {token, pwToken, orgToken, webhookUrl, webhookAuth};
        return Arrays.stream(arr).reduce(str, Secrets::replaceIfNonNull);
    }
    private static String replaceIfNonNull(String result, CharSequence naughty) {
        if (naughty == null) {
            return result;
        }
        return result.replace(naughty, REDACTED);
    }

    @Override
    public String toString() {
        String settings = yn(token) + yn(pwToken) + yn(orgToken) + yn(webhookUrl) + yn(webhookPort) + yn(webhookAuth);
        return String.format("Secrets{%s#%d}", settings, hashCode());
    }
    private static String yn(String str) {
        return str == null ? "N" : "Y";
    }
    private static String yn(int n) {
        return n == 0 ? "N" : "Y";
    }

}
