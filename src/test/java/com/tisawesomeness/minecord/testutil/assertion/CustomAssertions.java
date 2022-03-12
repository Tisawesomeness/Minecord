package com.tisawesomeness.minecord.testutil.assertion;

import com.tisawesomeness.minecord.util.type.OptionalBool;

public class CustomAssertions {

    public static OptionalBoolAssert assertThat(OptionalBool actual) {
        return OptionalBoolAssert.assertThat(actual);
    }

}
