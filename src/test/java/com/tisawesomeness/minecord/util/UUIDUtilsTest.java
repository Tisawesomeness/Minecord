package com.tisawesomeness.minecord.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class UUIDUtilsTest {

    private static final UUID TESTING_UUID = UUID.fromString("f6489b79-7a9f-49e2-980e-265a05dbc3af");
    private static final int[] TESTING_ARRAY = {-163013767,2057259490,-1743903142,98288559};

    private static final String[] invalidUuids = {
            " ", "   ", "\n", "\t", // Whitespace
            "Tis_awesomeness", // Username
            "jeb_",
            "81c978f3-7973-44cf-a1ac-664b329cf0e", // 31 chars
            "6d18209f89c743bba06771cb3e2cf89",
            "045d8f0a-38c9-41ff-99c2-9a01dee954803", // 33 chars
            "38eb7e2de0294273bccd18be389bf6db3",
            "2d9ab0dg-a2d5-4a73-a5a0-27e139cfcc6e", // Invalid character
            "9cc0c774-9494-4a98=b138-f9885b9b1f74",
            "eda22505-8457-3ea8-aee3-128ad4f060d2", // Not correct version
            "217c6718-ccc4-5dfc-8f46-8711f2f6f7ef",
            "4ac45d89-4c62-4c49-7498-fbb97030fdf9", // Not correct variant
            "e16ad46a-babf-46b9-ce17-4006de04cf1f",
            "a957-a311-7f15-4aa9-b9cd-39c6-7952-2b84", // Too many dashes
            "aed0c3d0e4e0-4361-930c-1c968f5c1189", // Too few dashes
            "02264bd9--8a60-46e7-950d-9ee6c3a66017" // Double dash
    };

    @ParameterizedTest(name = "{index} ==> Short string {0} maps to a UUID")
    @ValueSource(strings = {
            "11a5fc8d671b437d94c016b9e1cac57f",
            "7f516da36c034bd4885836e24c7b740e",
            "f7e9d6a2a55d492d8a8827eb37dd9781",
            "09177efe45994f48938623ffe9c10a59",
            "61be5ed6bc0e48a59e4c5c98188a020d",
            "0ec4c9cF44b04c748511be3eac31b622", // mixed case
            "7535348FD7F44A63A738FBE75E0BAEDD",
            "BE483D833CBF48208DE668EE1887A327"
    })
    @DisplayName("Short strings are correctly mapped to a UUID")
    public void testFromStringShort(String candidate) {
        assertThat(UUIDUtils.fromString(candidate))
                .isPresent()
                .map(UUIDUtils::toShortString)
                .get().asString()
                .isEqualToIgnoringCase(candidate);
    }

    @ParameterizedTest(name = "{index} ==> Long string {0} maps to a UUID")
    @ValueSource(strings = {
            "b82d4448-11ba-4da3-8e2f-e010309fcb95",
            "50c3ed25-ed57-4b66-a95a-b6ff67b923cb",
            "7adc1910-2d7b-4893-9119-7022e15f2028",
            "a8dc21ed-5fbe-491b-8338-6e0d390762f7",
            "af94c224-8cd3-4380-ae80-796a94a863bc",
            "cc739741-baE6-4286-98eb-1f45c4116cf9", // mixed case
            "8FF3E250-78CC-4609-87EC-B601AF05FB64",
            "5D351014-AAF9-409D-A608-6F996D0BCF34"
    })
    @DisplayName("Long strings are correctly mapped to a UUID")
    public void testFromStringLong(String candidate) {
        assertThat(UUIDUtils.fromString(candidate))
                .isPresent()
                .map(UUIDUtils::toLongString)
                .get().asString()
                .isEqualToIgnoringCase(candidate);
    }

    @ParameterizedTest(name = "{index} ==> String {0} is an invalid UUID")
    @MethodSource("invalidUUIDProvider")
    // These are excluded from testing fromGuarenteedShortString since
    // these cases are valid UUIDs for other versions and have no dashes
    @ValueSource(strings = {
            "e72109b8e639cc7cb224ef1fd3a4a436", // Not correct version
            "ca3f2ec22de44c9168117dca326db097" // Not correct variant
    })
    @EmptySource
    @DisplayName("Using fromString() on an invalid UUID string returns empty")
    public void testFromStringInvalid(String candidate) {
        assertThat(UUIDUtils.fromString(candidate)).isEmpty();
    }

    @ParameterizedTest(name = "{index} ==> String {0} stripped of dashes is a valid short UUID")
    @ValueSource(strings = {
            "1ebe4f10-32a4-4fee-910c-2792d1aba0f6",
            "58b11ba5-aaa0-407e-9e91-980dcc748ec6",
            "346565d2-adf0-4db1-83c3-53e6c1a5b0e8",
            "7023aa42-8de6-4838-b4d3-f836d06dc849",
            "b3d0d862-f791-4e65-9790-1f1454fb9a4d",
            "39024eCe-6680-4342-96e0-0afE08627465", // mixed case
            "A1F04240-A7F8-48E9-903B-2A21AEA93E73",
            "F65882AC-26E1-42ED-9631-EEFAF81544D3"
    })
    @DisplayName("fromGuarenteedShortString() correctly maps short strings to UUIDs")
    public void testFromGuarenteedShortStringValid(String candidate) {
        UUID uuid = UUIDUtils.fromGuarenteedShortString(candidate.replace("-", ""));
        assertThat(UUIDUtils.toLongString(uuid)).isEqualToIgnoringCase(candidate);
    }

    @ParameterizedTest(name =
            "{index} ==> String {0} is an invalid UUID and fromGuarenteedShortString throws IllegalArgumentException")
    @MethodSource("invalidUUIDProvider")
    @EmptySource
    @DisplayName("fromGuarenteedShortString() throws IllegalArgumentException if the input string is not a valid UUID")
    public void testFromGuarenteedShortStringInvalid(String candidate) {
        assertThatThrownBy(() -> UUIDUtils.fromGuarenteedShortString(candidate))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest(name = "{index} ==> UUID {0} without dashes is {1}")
    @CsvSource({
            "d174deab-8971-4891-9a5b-85fa4315bd1a, d174deab897148919a5b85fa4315bd1a",
            "ce22ccd3-18a1-4530-b738-653d7a349d68, ce22ccd318a14530b738653d7a349d68",
            "ddc660a7-d9fe-4fc0-ac8b-dd4cb9877d50, ddc660a7d9fe4fc0ac8bdd4cb9877d50",
            "2992cc77-9b2d-4c5c-ae83-a19f4870a8e9, 2992cc779b2d4c5cae83a19f4870a8e9",
            "c7236357-e005-4598-aef9-5ef60727aae8, c7236357e0054598aef95ef60727aae8",
            "e35aAd43-9797-454D-8464-9283a306ddb6, e35aad439797454d84649283a306ddb6", // mixed case
            "3048B123-F912-4320-923D-F2E1D3989DDD, 3048B123F9124320923DF2E1D3989DDD",
            "B7B97BC2-336C-4BD9-A035-35AF2C0344E0, B7B97BC2336C4BD9A03535AF2C0344E0"
    })
    @DisplayName("toShortString() returns the UUID with dashes stripped")
    public void testToShortString(UUID candidate, CharSequence expected) {
        assertThat(UUIDUtils.toShortString(candidate)).isEqualToIgnoringCase(expected);
    }

    @ParameterizedTest(name = "{index} ==> String {0} is the same as its UUID long string")
    @ValueSource(strings = {
            "d159d894-81e1-4adb-b725-6592e8f15544",
            "d6981b4b-0d53-4512-8be8-449eff507c9d",
            "f7d79fef-4495-4d83-8aa0-6f6249adac84",
            "ab6c104e-9da2-418c-8d8c-76ea1913387d",
            "e7381ed5-5d45-4b78-af1a-2675a5c2f968",
            "02139004-765d-4f0a-aC38-142b5eBa0a3b", // mixed case
            "703037A0-9EFB-4DFB-9A65-C617C00C33FE",
            "D40DF87B-106E-4661-8F73-4D5E02953791"
    })
    @DisplayName("Converting to and from UUID does not modify the UUID")
    public void testToLongString(String candidate) {
        assertThat(UUIDUtils.toLongString(UUID.fromString(candidate))).isEqualToIgnoringCase(candidate);
    }

    @Test
    @DisplayName("Most/least NBT is generated correctly")
    public void testMostLeast() {
        assertThat(UUIDUtils.toMostLeastString(TESTING_UUID))
                .isEqualTo("UUIDMost:-700138796005504542,UUIDLeast:-7490006962183355473");
    }

    @Test
    @DisplayName("Int array is generated correctly")
    public void testIntArray() {
        assertThat(UUIDUtils.toIntArray(TESTING_UUID)).isEqualTo(TESTING_ARRAY);
    }

    @Test
    @DisplayName("Int arrays are correctly converted to UUIDs")
    public void testFromIntArray() {
        assertThat(UUIDUtils.fromIntArray(TESTING_ARRAY)).isEqualTo(TESTING_UUID);
    }

    @Test
    @DisplayName("Int array NBT is generated correctly")
    public void testIntArrayString() {
        assertThat(UUIDUtils.toIntArrayString(TESTING_UUID))
                .isEqualTo("[I;-163013767,2057259490,-1743903142,98288559]");
    }

    private static Stream<String> invalidUUIDProvider() {
        return Arrays.stream(invalidUuids);
    }

}
