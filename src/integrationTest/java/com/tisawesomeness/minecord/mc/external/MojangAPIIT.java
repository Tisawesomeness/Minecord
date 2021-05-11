package com.tisawesomeness.minecord.mc.external;

import com.tisawesomeness.minecord.config.config.Config;
import com.tisawesomeness.minecord.mc.player.NameChange;
import com.tisawesomeness.minecord.mc.player.Profile;
import com.tisawesomeness.minecord.mc.player.Username;
import com.tisawesomeness.minecord.network.APIClient;
import com.tisawesomeness.minecord.testutil.PlayerTests;
import com.tisawesomeness.minecord.testutil.Resources;
import com.tisawesomeness.minecord.testutil.annotation.MojangAPITest;
import com.tisawesomeness.minecord.util.Lists;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@MojangAPITest
public class MojangAPIIT {

    private static MojangAPI api;

    @BeforeAll
    private static void initAPI() throws JsonProcessingException {
        Config config = Resources.config();
        APIClient client = new APIClient(config.getAdvancedConfig().getHttpConfig());
        api = new MojangAPIImpl(client);
    }

    @Test
    @DisplayName("Mojang API Username --> UUID endpoint works")
    public void testUsernameToUUID() throws IOException {
        List<Optional<UUID>> list = Lists.of(
                api.getUUID(PlayerTests.TIS_USERNAME),
                api.getUUID(PlayerTests.JEB_USERNAME),
                api.getUUID(PlayerTests.NOTCH_USERNAME)
        );
        // Test will fail if any player lookup fails (throws an exception)
        boolean[] asserts = {
                PlayerTests.TIS_STEVE_UUID.equals(list.get(0).orElse(null)),
                PlayerTests.JEB_ALEX_UUID.equals(list.get(1).orElse(null)),
                PlayerTests.NOTCH_STEVE_UUID.equals(list.get(2).orElse(null))
        };
        // At least one username out of Tis_awesomeness, jeb_ and Notch must stay the same for the test to pass
        // It is very unlikely that all three will change at the same time (especially because I am Tis lol)
        assertThat(asserts).contains(true);
    }

    @ParameterizedTest(name = "{index} ==> UUID {0} has original name {1}")
    @DisplayName("Mojang API UUID --> name history endpoint works")
    @MethodSource("com.tisawesomeness.minecord.testutil.PlayerTests#originalNameTestCaseProvider")
    public void testNameHistory(UUID uuid, Username username) throws IOException {
        assertThat(api.getNameHistory(uuid))
            .isNotEmpty()
            .last()
            .extracting(NameChange::getUsername)
            .isEqualTo(username);
    }

    @ParameterizedTest(name = "{index} ==> UUID {0} is not legacy")
    @DisplayName("Mojang API UUID --> name history endpoint works")
    @MethodSource("com.tisawesomeness.minecord.testutil.PlayerTests#uuidProvider")
    public void testProfile(UUID uuid) throws IOException {
        assertThat(api.getProfile(uuid))
                .isNotEmpty()
                .get()
                .extracting(Profile::isLegacy)
                .isEqualTo(false);
    }

}
