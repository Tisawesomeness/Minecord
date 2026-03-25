package com.tisawesomeness.minecord.util.type;

import lombok.Value;

import java.util.Iterator;

@Value
public class IntRange implements Iterable<Integer> {
    int min;
    int max;

    public IntRange(int value) {
        this.min = value;
        this.max = value;
    }

    public IntRange(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException(String.format("min must be less than or equal to max, but was %d > %d", min, max));
        }
        this.min = min;
        this.max = max;
    }

    public boolean contains(int value) {
        return value >= min && value <= max;
    }

    @Override
    public Iterator<Integer> iterator() {
        return new Iter();
    }

    private class Iter implements Iterator<Integer> {
        private int current = min;
        private boolean done = false;

        @Override
        public boolean hasNext() {
            return done || current <= max;
        }

        @Override
        public Integer next() {
            int result = current;
            if (current >= max) {
                done = true;
            } else {
                current++;
            }
            return result;
        }
    }
}
