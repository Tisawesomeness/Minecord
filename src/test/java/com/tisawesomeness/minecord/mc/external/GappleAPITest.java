package com.tisawesomeness.minecord.mc.external;

import com.tisawesomeness.minecord.mc.player.AccountStatus;
import com.tisawesomeness.minecord.testutil.mc.MockGappleAPI;
import com.tisawesomeness.minecord.util.UUIDs;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class GappleAPITest {

    private static final UUID FAKE_UUID = UUID.fromString("f6489b79-7a9f-49e2-980e-265a05dbc3ae");
    private static final UUID INVALID_UUID = UUID.fromString("e72109b8-e639-cc7c-b224-ef1fd3a4a436");

    private static final UUID[] UUIDS = buildUuids();
    private static UUID[] buildUuids() {
        return Arrays.stream(new String[]{
                "aae41b6ff5ce48ba9598f55ed2c81fa3",
                "c7b3d49c580c4af2a824ca07b37ff2f9",
                "78abd133b85b4a91a3141e937eb23055",
                "416e3d9f71db49ec8c787e8eec740506",
                "bec2968f48ab4504a7b4bde54a789bdb",
                "1185081a0042432588af257b1ee91bb0"
        }).map(UUIDs::fromGuaranteedShortString).toArray(UUID[]::new);
    }
    private static final String[] RESPONSES = {
            "{\"username\":\"FunClock1564643\",\"uuid\":\"aae41b6ff5ce48ba9598f55ed2c81fa3\",\"status\":\"new_msa\"}",
            "{\"username\":\"D__G\",\"uuid\":\"c7b3d49c580c4af2a824ca07b37ff2f9\",\"status\":\"migrated_msa\"}",
            "{\"username\":\"SeeSaw\",\"uuid\":\"78abd133b85b4a91a3141e937eb23055\",\"status\":\"migrated_msa_from_legacy\"}",
            "{\"username\":\"RyanToysReview\",\"uuid\":\"416e3d9f71db49ec8c787e8eec740506\",\"status\":\"msa\"}",
            "{\"username\":\"Ined\",\"uuid\":\"bec2968f48ab4504a7b4bde54a789bdb\",\"status\":\"mojang\"}",
            "{\"username\":\"CREAPER_12\",\"uuid\":\"1185081a0042432588af257b1ee91bb0\",\"status\":\"legacy\"}"
    };
    private static final AccountStatus[] STATUSES = {
            AccountStatus.NEW_MICROSOFT,
            AccountStatus.MIGRATED_MICROSOFT,
            AccountStatus.MIGRATED_MICROSOFT_FROM_LEGACY,
            AccountStatus.UNKNOWN_MICROSOFT,
            AccountStatus.MINECRAFT,
            AccountStatus.LEGACY
    };

    private static GappleAPI api;

    @BeforeAll
    private static void buildAPI() {
        MockGappleAPI mockAPI = new MockGappleAPI();
        for (int i = 0; i < UUIDS.length; i++) {
            mockAPI.mapAccountStatus(UUIDS[i], RESPONSES[i]);
        }
        api = mockAPI;
    }

    @ParameterizedTest(name = "UUID {0} has account status {1}")
    @MethodSource("accountStatusIndexProvider")
    @DisplayName("GappleAPI responses are parsed to the correct status")
    public void testAccountStatus(UUID input, AccountStatus expected) throws IOException {
        assertThat(api.getAccountStatus(input)).contains(expected);
    }
    @Test
    @DisplayName("GappleAPI returns empty for non-existent UUID")
    public void testAccountStatusNonExistent() throws IOException {
        assertThat(api.getAccountStatus(FAKE_UUID)).isEmpty();
    }
    @Test
    @DisplayName("GappleAPI throws on invalid UUID")
    public void testAccountStatusInvalid() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> api.getAccountStatus(INVALID_UUID));
    }

    public static Stream<Arguments> accountStatusIndexProvider() {
        return IntStream.range(0, UUIDS.length)
                .mapToObj(i -> Arguments.of(UUIDS[i], STATUSES[i]));
    }

}
