package com.tisawesomeness.minecord.mc.external;

import com.tisawesomeness.minecord.config.config.Config;
import com.tisawesomeness.minecord.mc.player.AccountStatus;
import com.tisawesomeness.minecord.mc.player.Player;
import com.tisawesomeness.minecord.mc.player.Username;
import com.tisawesomeness.minecord.network.APIClient;
import com.tisawesomeness.minecord.network.OkAPIClient;
import com.tisawesomeness.minecord.testutil.Futures;
import com.tisawesomeness.minecord.testutil.PlayerTests;
import com.tisawesomeness.minecord.testutil.Reflect;
import com.tisawesomeness.minecord.testutil.Resources;
import com.tisawesomeness.minecord.testutil.annotation.MojangAPITest;
import com.tisawesomeness.minecord.util.Lists;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class PlayerProviderIT {

    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    private static APIClient client;
    private static PlayerProvider provider;

    @BeforeAll
    private static void initAPI() throws JsonProcessingException {
        Config config = Resources.config();
        client = new OkAPIClient(config.getAdvancedConfig().getHttpConfig());
        provider = new DualPlayerProvider(client, config);
    }

    @Test
    @DisplayName("Mojang/Electroid API username --> UUID endpoint works")
    @MojangAPITest
    public void testUsernameToUUID() {
        List<Optional<UUID>> list = Lists.of(
                provider.getUUID(PlayerTests.TIS_USERNAME),
                provider.getUUID(PlayerTests.JEB_USERNAME),
                provider.getUUID(PlayerTests.NOTCH_USERNAME)
        ).stream()
                .map(future -> Futures.joinTimeout(future, TIMEOUT))
                .collect(Collectors.toList());
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

    @Test
    @DisplayName("Mojang/Electroid API UUID --> player endpoint works")
    @MojangAPITest
    public void testUUIDToPlayer() {
        List<Optional<Player>> list = Lists.of(
                provider.getPlayer(PlayerTests.TIS_STEVE_UUID),
                provider.getPlayer(PlayerTests.JEB_ALEX_UUID),
                provider.getPlayer(PlayerTests.NOTCH_STEVE_UUID)
        ).stream()
                .map(future -> Futures.joinTimeout(future, TIMEOUT))
                .collect(Collectors.toList());
        boolean[] asserts = {
                playerHasName(list.get(0), PlayerTests.TIS_USERNAME),
                playerHasName(list.get(1), PlayerTests.JEB_USERNAME),
                playerHasName(list.get(2), PlayerTests.NOTCH_USERNAME)
        };
        assertThat(asserts).contains(true);
    }

    @Test
    @DisplayName("Mojang/Electroid API username --> player endpoint works")
    @MojangAPITest
    public void testUsernameToPlayer() {
        List<Optional<Player>> list = Lists.of(
                provider.getPlayer(PlayerTests.TIS_USERNAME),
                provider.getPlayer(PlayerTests.JEB_USERNAME),
                provider.getPlayer(PlayerTests.NOTCH_USERNAME)
        ).stream()
                .map(future -> Futures.joinTimeout(future, TIMEOUT))
                .collect(Collectors.toList());
        boolean[] asserts = {
                playerHasName(list.get(0), PlayerTests.TIS_USERNAME),
                playerHasName(list.get(1), PlayerTests.JEB_USERNAME),
                playerHasName(list.get(2), PlayerTests.NOTCH_USERNAME)
        };
        assertThat(asserts).contains(true);
    }

    private static boolean playerHasName(Optional<Player> playerOpt, Username name) {
        if (!playerOpt.isPresent()) {
            return false;
        }
        return playerOpt.get().getUsername().equals(name);
    }

    @Test
    @MojangAPITest
    @DisplayName("Gapple account status endpoint works")
    public void testAccountStatus() {
        assertThat(provider.getAccountStatus(PlayerTests.TIS_STEVE_UUID))
                .succeedsWithin(TIMEOUT)
                .asInstanceOf(InstanceOfAssertFactories.optional(AccountStatus.class))
                .isNotEmpty();
    }

    @Test
    @DisplayName("Gapple account status can be enabled in config")
    public void testGappleEnabled() throws JsonProcessingException, NoSuchFieldException {
        Config config = Resources.config();
        Reflect.setField(config.getFlagConfig(), "useGappleAPI", true);
        PlayerProvider provider = new DualPlayerProvider(client, config);

        assertThat(provider.areStatusAPIsEnabled()).isTrue();
    }
    @Test
    @DisplayName("Cannot request gapple account status if disabled")
    public void testGappleDisabled() throws JsonProcessingException, NoSuchFieldException {
        Config config = Resources.config();
        Reflect.setField(config.getFlagConfig(), "useGappleAPI", false);
        PlayerProvider provider = new DualPlayerProvider(client, config);

        assertThat(provider.areStatusAPIsEnabled()).isFalse();
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> provider.getAccountStatus(PlayerTests.TIS_STEVE_UUID));
    }

}
