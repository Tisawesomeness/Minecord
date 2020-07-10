package com.tisawesomeness.minecord.util.type;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EitherTest {

    @Test
    @DisplayName("Left factory method does not alter value")
    public void testLeft() {
        Object o = new Object();
        Either<Object, Object> either = Either.left(o);
        assertFalse(either.isRight());
        assertEquals(o, either.getLeft());
    }
    @Test
    @DisplayName("Right factory method does not alter value")
    public void testRight() {
        Object o = new Object();
        Either<Object, Object> either = Either.right(o);
        assertTrue(either.isRight());
        assertEquals(o, either.getRight());
    }

    @Test
    @DisplayName("getLeft() throws IllegalStateException if Either is a Right")
    public void testGetLeft() {
        Either<Object, Object> either = Either.right(new Object());
        assertThrows(IllegalStateException.class, either::getLeft);
    }
    @Test
    @DisplayName("getRight() throws IllegalStateException if Either is a Left")
    public void testGetRight() {
        Either<Object, Object> either = Either.left(new Object());
        assertThrows(IllegalStateException.class, either::getRight);
    }

    @Test
    @DisplayName("Calling mapLeft() on a Left applies the mapper")
    public void testMapLeft() {
        Function<Integer, Integer> mapper = x -> x * 2;
        int i = 2;
        Either<Integer, Integer> either = Either.left(i);
        Either<Integer, Integer> mappedEither = either.mapLeft(mapper);
        assertFalse(mappedEither.isRight());
        assertEquals(mapper.apply(i), mappedEither.getLeft());
    }
    @Test
    @DisplayName("Calling mapLeft() on a Right keeps the value")
    public void testMapLeftOnRight() {
        Function<Integer, Integer> mapper = x -> x * 2;
        int i = 2;
        Either<Integer, Integer> either = Either.right(i);
        Either<Integer, Integer> mappedEither = either.mapLeft(mapper);
        assertTrue(mappedEither.isRight());
        assertEquals(i, mappedEither.getRight());
    }
    @Test
    @DisplayName("Calling mapRight() on a Right applies the mapper")
    public void testMapRight() {
        Function<Integer, Integer> mapper = x -> x * 2;
        int i = 2;
        Either<Integer, Integer> either = Either.right(i);
        Either<Integer, Integer> mappedEither = either.mapRight(mapper);
        assertTrue(mappedEither.isRight());
        assertEquals(mapper.apply(i), mappedEither.getRight());
    }
    @Test
    @DisplayName("Calling mapRight() on a Left keeps the value")
    public void testMapRightOnLeft() {
        Function<Integer, Integer> mapper = x -> x * 2;
        int i = 2;
        Either<Integer, Integer> either = Either.left(i);
        Either<Integer, Integer> mappedEither = either.mapRight(mapper);
        assertFalse(mappedEither.isRight());
        assertEquals(i, mappedEither.getLeft());
    }

    @Test
    @DisplayName("Folding a Left applies the left mapper")
    public void testFoldOnLeft() {
        Function<String, Integer> mapper = String::length;
        Function<String, Integer> wrongMapper = i -> -1;
        String s = "A string";
        Either<String, String> either = Either.left(s);
        assertEquals(s.length(), either.fold(mapper, wrongMapper));
    }
    @Test
    @DisplayName("Folding a Right applies the right mapper")
    public void testFoldOnRight() {
        Function<String, Integer> mapper = String::length;
        Function<String, Integer> wrongMapper = i -> -1;
        String s = "A string";
        Either<String, String> either = Either.right(s);
        assertEquals(s.length(), either.fold(wrongMapper, mapper));
    }

}
