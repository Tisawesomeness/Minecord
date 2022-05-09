package com.tisawesomeness.minecord.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class EitherTest {

    @Test
    @DisplayName("Left factory method does not alter value")
    public void testLeft() {
        Object o = new Object();
        Either<Object, Object> either = Either.left(o);
        assertThat(either.isLeft()).isTrue();
        assertThat(either.isRight()).isFalse();
        assertThat(either.getLeft()).isEqualTo(o);
    }
    @Test
    @DisplayName("Right factory method does not alter value")
    public void testRight() {
        Object o = new Object();
        Either<Object, Object> either = Either.right(o);
        assertThat(either.isLeft()).isFalse();
        assertThat(either.isRight()).isTrue();
        assertThat(either.getRight()).isEqualTo(o);
    }

    @Test
    @DisplayName("getLeft() throws IllegalStateException if Either is a Right")
    public void testGetLeft() {
        Either<Object, Object> either = Either.right(new Object());
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(either::getLeft);
    }
    @Test
    @DisplayName("getRight() throws IllegalStateException if Either is a Left")
    public void testGetRight() {
        Either<Object, Object> either = Either.left(new Object());
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(either::getRight);
    }

    @Test
    @DisplayName("Calling mapLeft() on a Left applies the mapper")
    public void testMapLeft() {
        Function<Integer, Integer> mapper = x -> x * 2;
        int i = 2;
        Either<Integer, Integer> either = Either.left(i);
        Either<Integer, Integer> mappedEither = either.mapLeft(mapper);
        assertThat(mappedEither.isLeft()).isTrue();
        assertThat(mappedEither.isRight()).isFalse();
        assertThat(mappedEither.getLeft()).isEqualTo(mapper.apply(i));
    }
    @Test
    @DisplayName("Calling mapLeft() on a Right keeps the value")
    public void testMapLeftOnRight() {
        Function<Integer, Integer> mapper = x -> x * 2;
        int i = 2;
        Either<Integer, Integer> either = Either.right(i);
        Either<Integer, Integer> mappedEither = either.mapLeft(mapper);
        assertThat(mappedEither.isLeft()).isFalse();
        assertThat(mappedEither.isRight()).isTrue();
        assertThat(mappedEither.getRight()).isEqualTo(i);
    }
    @Test
    @DisplayName("Calling mapRight() on a Right applies the mapper")
    public void testMapRight() {
        Function<Integer, Integer> mapper = x -> x * 2;
        int i = 2;
        Either<Integer, Integer> either = Either.right(i);
        Either<Integer, Integer> mappedEither = either.mapRight(mapper);
        assertThat(mappedEither.isLeft()).isFalse();
        assertThat(mappedEither.isRight()).isTrue();
        assertThat(mappedEither.getRight()).isEqualTo(mapper.apply(i));
    }
    @Test
    @DisplayName("Calling mapRight() on a Left keeps the value")
    public void testMapRightOnLeft() {
        Function<Integer, Integer> mapper = x -> x * 2;
        int i = 2;
        Either<Integer, Integer> either = Either.left(i);
        Either<Integer, Integer> mappedEither = either.mapRight(mapper);
        assertThat(mappedEither.isLeft()).isTrue();
        assertThat(mappedEither.isRight()).isFalse();
        assertThat(mappedEither.getLeft()).isEqualTo(i);
    }

    @Test
    @DisplayName("Folding a Left applies the left mapper")
    public void testFoldOnLeft() {
        Function<String, Integer> mapper = String::length;
        Function<String, Integer> wrongMapper = i -> -1;
        String s = "A string";
        Either<String, String> either = Either.left(s);
        assertThat(either.fold(mapper, wrongMapper)).isEqualTo(s.length());
    }
    @Test
    @DisplayName("Folding a Right applies the right mapper")
    public void testFoldOnRight() {
        Function<String, Integer> mapper = String::length;
        Function<String, Integer> wrongMapper = i -> -1;
        String s = "A string";
        Either<String, String> either = Either.right(s);
        assertThat(either.fold(wrongMapper, mapper)).isEqualTo(s.length());
    }

    @Test
    @DisplayName("A Left is equal to itself")
    public void testEqualsReflexiveLeft() {
        Either<Object, Object> either = Either.left(new Object());
        assertThat(either).isEqualTo(either);
    }
    @Test
    @DisplayName("A Right is equal to itself")
    public void testEqualsReflexiveRight() {
        Either<Object, Object> either = Either.right(new Object());
        assertThat(either).isEqualTo(either);
    }
    @Test
    @DisplayName("Identical Lefts are symmetrically equal")
    public void testEqualsSymmetricLeft() {
        Object o = new Object();
        Either<Object, Object> either1 = Either.left(o);
        Either<Object, Object> either2 = Either.left(o);
        assertThat(either1).isEqualTo(either2).hasSameHashCodeAs(either2);
        assertThat(either2).isEqualTo(either1);
    }
    @Test
    @DisplayName("Identical Rights are symmetrically equal")
    public void testEqualsSymmetricRight() {
        Object o = new Object();
        Either<Object, Object> either1 = Either.right(o);
        Either<Object, Object> either2 = Either.right(o);
        assertThat(either1).isEqualTo(either2).hasSameHashCodeAs(either2);
        assertThat(either2).isEqualTo(either1);
    }
    @Test
    @DisplayName("Two Lefts with different values are not equal")
    public void testNotEqualsLeft() {
        Either<Object, Object> either1 = Either.left(new Object());
        Either<Object, Object> either2 = Either.left(new Object());
        assertThat(either1).isNotEqualTo(either2);
    }
    @Test
    @DisplayName("Two Rights with different values are not equal")
    public void testNotEqualsRight() {
        Either<Object, Object> either1 = Either.right(new Object());
        Either<Object, Object> either2 = Either.right(new Object());
        assertThat(either1).isNotEqualTo(either2);
    }
    @Test
    @DisplayName("A Left never equals a Right")
    public void testLeftNotEqualsRight() {
        Object o = new Object();
        Either<Object, Object> left = Either.left(o);
        Either<Object, Object> right = Either.right(o);
        assertThat(left).isNotEqualTo(right);
    }

}
