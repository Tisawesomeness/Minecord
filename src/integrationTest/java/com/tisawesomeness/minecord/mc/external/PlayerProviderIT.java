package com.tisawesomeness.minecord.mc.external;

import com.tisawesomeness.minecord.config.config.Config;
import com.tisawesomeness.minecord.mc.player.Player;
import com.tisawesomeness.minecord.mc.player.Username;
import com.tisawesomeness.minecord.network.APIClient;
import com.tisawesomeness.minecord.testutil.PlayerTests;
import com.tisawesomeness.minecord.testutil.Resources;
import com.tisawesomeness.minecord.util.Lists;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class PlayerProviderIT {

    private static PlayerProvider provider;

    @BeforeAll
    private static void initAPI() throws JsonProcessingException {
        Config config = Resources.config();
        APIClient client = new APIClient(config.getAdvancedConfig().getHttpConfig());
        provider = new DualPlayerProvider(client, config);
    }

    @Test
    public void testProfile() {
        List<Optional<Player>> list = Lists.of(
                provider.getPlayer(PlayerTests.TIS_STEVE_UUID),
                provider.getPlayer(PlayerTests.JEB_ALEX_UUID),
                provider.getPlayer(PlayerTests.NOTCH_STEVE_UUID)
        ).stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
        // Test will fail if any player lookup fails (throws an exception)
        boolean[] asserts = {
                playerHasName(list.get(0), PlayerTests.TIS_USERNAME),
                playerHasName(list.get(1), PlayerTests.JEB_USERNAME),
                playerHasName(list.get(2), PlayerTests.NOTCH_USERNAME)
        };
        // At least one username out of Tis_awesomeness, jeb_ and Notch must stay the same for the test to pass
        // It is very unlikely that all three will change at the same time (especially because I am Tis lol)
        assertThat(asserts).contains(true);
    }
    private static boolean playerHasName(Optional<Player> playerOpt, Username name) {
        if (!playerOpt.isPresent()) {
            return false;
        }
        return playerOpt.get().getUsername().equals(name);
    }

}
