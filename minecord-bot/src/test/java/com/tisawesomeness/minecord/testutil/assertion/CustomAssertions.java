package com.tisawesomeness.minecord.testutil.assertion;

import com.tisawesomeness.minecord.common.util.Either;
import com.tisawesomeness.minecord.testutil.runner.TestContext;
import com.tisawesomeness.minecord.testutil.runner.TestContextAssert;
import com.tisawesomeness.minecord.util.type.OptionalBool;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class CustomAssertions {

    public static OptionalBoolAssert assertThat(OptionalBool actual) {
        return OptionalBoolAssert.assertThat(actual);
    }
    public static <L, R> EitherAssert<L, R> assertThat(Either<L, R> actual) {
        return EitherAssert.assertThat(actual);
    }
    public static TestContextAssert assertThat(TestContext actual) {
        return TestContextAssert.assertThat(actual);
    }
    public static MessageEmbedAssert assertThat(MessageEmbed actual) {
        return MessageEmbedAssert.assertThat(actual);
    }

}
