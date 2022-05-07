package com.tisawesomeness.minecord;

import com.tisawesomeness.minecord.config.config.Config;
import com.tisawesomeness.minecord.testutil.Resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class SecretsTest {

    private static final String DUMMY_TOKEN = "dummyToken";

    private static Secrets secrets;

    @BeforeAll
    private static void initConfig() throws JsonProcessingException {
        Config config = Resources.config();
        secrets = new Secrets(config, DUMMY_TOKEN);
    }

    @Test
    @DisplayName("Secrets takes token from constructor")
    public void testTokenConfig() {
        assertThat(secrets.getToken())
                .withFailMessage("Secrets token did not match dummy")
                .isEqualTo(DUMMY_TOKEN);
    }

    @ParameterizedTest(name = "{index} ==> Secret {index} is cleansed")
    @DisplayName("All sensitive secrets are cleansed")
    @MethodSource("sensitiveProvider")
    public void testClean(String sensitive) {
        String dirty = "<" + sensitive + ">";
        String expected = "<" + Secrets.REDACTED + ">";
        assertThat(secrets.clean(dirty))
                .withFailMessage("Cleaned string was not " + expected)
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("toString() does not leak any secrets")
    public void testNoToStringLeak() {
        List<String> sensitive = sensitiveProvider().collect(Collectors.toList());
        assertThat(secrets.toString())
                .withFailMessage("toString() contained a sensitive secret")
                .doesNotContain(sensitive);
    }

    private static Stream<String> sensitiveProvider() {
        return Stream.of(
                secrets.getToken(),
                secrets.getPwToken(),
                secrets.getOrgToken(),
                secrets.getWebhookUrl(),
                secrets.getWebhookAuth()
        ).filter(Objects::nonNull);
    }

}
