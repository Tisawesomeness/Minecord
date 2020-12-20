package com.tisawesomeness.minecord.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class UUIDUtilsTest {

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
    public void testToLongString(String candidate) {
        assertThat(UUIDUtils.toLongString(UUID.fromString(candidate))).isEqualToIgnoringCase(candidate);
    }

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
    public void testFromStringLong(String candidate) {
        assertThat(UUIDUtils.fromString(candidate))
                .isPresent()
                .map(UUIDUtils::toLongString)
                .get().asString()
                .isEqualToIgnoringCase(candidate);
    }

    @ParameterizedTest(name = "{index} ==> String {0} is an invalid UUID")
    @ValueSource(strings = {
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
            "e72109b8e639cc7cb224ef1fd3a4a436",
            "4ac45d89-4c62-4c49-7498-fbb97030fdf9", // Not correct variant
            "e16ad46a-babf-46b9-ce17-4006de04cf1f",
            "ca3f2ec22de44c9168117dca326db097",
            "a957-a311-7f15-4aa9-b9cd-39c6-7952-2b84", // Too many dashes
            "aed0c3d0e4e0-4361-930c-1c968f5c1189", // Too few dashes
            "02264bd9--8a60-46e7-950d-9ee6c3a66017" // Double dash
    })
    @EmptySource
    public void testFromStringInvalid(String candidate) {
        assertThat(UUIDUtils.fromString(candidate)).isEmpty();
    }

}
