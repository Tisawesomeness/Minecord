package com.tisawesomeness.minecord.testutil.assertion;

import com.tisawesomeness.minecord.common.util.Either;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.InstanceOfAssertFactory;
import org.assertj.core.api.ObjectAssert;

import javax.annotation.CheckReturnValue;

public class EitherAssert<L, R> extends AbstractAssert<EitherAssert<L, R>, Either<L, R>> {

    protected EitherAssert(Either<L, R> actual) {
        super(actual, EitherAssert.class);
    }
    public static <L, R> EitherAssert<L, R> assertThat(Either<L, R> actual) {
        return new EitherAssert<>(actual);
    }

    public EitherAssert<L, R> isLeft() {
        isNotNull();
        if (!actual.isLeft()) {
            failWithMessage("Expected Either to be a Left but was " + actual);
        }
        return myself;
    }
    public EitherAssert<L, R> isRight() {
        isNotNull();
        if (!actual.isRight()) {
            failWithMessage("Expected Either to be a Right but was " + actual);
        }
        return myself;
    }

    @CheckReturnValue
    public ObjectAssert<L> asLeft() {
        isNotNull();
        isLeft();
        return new ObjectAssert<>(actual.getLeft());
    }
    @CheckReturnValue
    public <ASSERT extends AbstractAssert<ASSERT, L>> ASSERT asLeft(InstanceOfAssertFactory<?, ASSERT> assertFactory) {
        return asLeft().asInstanceOf(assertFactory);
    }
    @CheckReturnValue
    public ObjectAssert<R> asRight() {
        isNotNull();
        isRight();
        return new ObjectAssert<>(actual.getRight());
    }
    @CheckReturnValue
    public <ASSERT extends AbstractAssert<ASSERT, L>> ASSERT asRight(InstanceOfAssertFactory<?, ASSERT> assertFactory) {
        return asRight().asInstanceOf(assertFactory);
    }

}
