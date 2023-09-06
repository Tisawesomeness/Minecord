package com.tisawesomeness.minecord.testutil.assertion;

import com.tisawesomeness.minecord.util.type.OptionalBool;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.error.BasicErrorMessageFactory;
import org.assertj.core.internal.Failures;

import static org.assertj.core.error.OptionalShouldBePresent.shouldBePresent;

// Adapted from AssertJ OptionalIntAssert implementation
public class OptionalBoolAssert extends AbstractAssert<OptionalBoolAssert, OptionalBool> {

    protected OptionalBoolAssert(OptionalBool actual) {
        super(actual, OptionalBoolAssert.class);
    }
    public static OptionalBoolAssert assertThat(OptionalBool actual) {
        return new OptionalBoolAssert(actual);
    }

    public OptionalBoolAssert isPresent() {
        isNotNull();
        if (!actual.isPresent()) {
            throwAssertionError(shouldBePresent(actual));
        }
        return this;
    }
    public OptionalBoolAssert isNotPresent() {
        return isEmpty();
    }

    public OptionalBoolAssert isEmpty() {
        isNotNull();
        if (actual.isPresent()) {
            throwAssertionError(OptionalShouldBeEmpty.shouldBeEmpty(actual));
        }
        return myself;
    }
    public OptionalBoolAssert isNotEmpty() {
        return isPresent();
    }

    public OptionalBoolAssert hasValue(boolean expectedValue) {
        isNotNull();
        if (!actual.isPresent()) {
            throwAssertionError(OptionalShouldContain.shouldContain(expectedValue));
        }
        if (expectedValue != actual.getAsBool()) {
            throw Failures.instance().failure(info, OptionalShouldContain.shouldContain(actual, expectedValue),
                    actual.getAsBool(), expectedValue);
        }
        return myself;
    }

    public OptionalBoolAssert isTrue() {
        return hasValue(true);
    }
    public OptionalBoolAssert isFalse() {
        return hasValue(false);
    }

    private static class OptionalShouldBeEmpty extends BasicErrorMessageFactory {

        private OptionalShouldBeEmpty(Object optionalValue) {
            super("%nExpecting an empty " + OptionalBool.class.getSimpleName() +
                    " but was containing value: %s", optionalValue);
        }
        public static OptionalShouldBeEmpty shouldBeEmpty(OptionalBool optional) {
            return new OptionalShouldBeEmpty(optional.getAsBool());
        }

    }

    private static class OptionalShouldContain extends BasicErrorMessageFactory {

        private static final String EXPECTING_TO_CONTAIN = "%nExpecting actual:%n  %s%nto contain:%n  %s%nbut did not.";

        private OptionalShouldContain(String message, Object actual, Object expected) {
            super(message, actual, expected);
        }
        private OptionalShouldContain(Object expected) {
            super("%nExpecting Optional to contain:%n  %s%nbut was empty.", expected);
        }

        public static OptionalShouldContain shouldContain(Object expectedValue) {
            return new OptionalShouldContain(expectedValue);
        }
        public static OptionalShouldContain shouldContain(OptionalBool optional, boolean expectedValue) {
            return optional.isPresent()
                    ? new OptionalShouldContain(EXPECTING_TO_CONTAIN, optional, expectedValue)
                    : shouldContain(expectedValue);
        }

    }

}
