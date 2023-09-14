package com.tisawesomeness.minecord.util.type;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class DimensionsTest {

    @Test
    public void test() {
        assertThat(new Dimensions(2, 1))
                .extracting(Dimensions::getWidth, Dimensions::getHeight)
                .containsExactly(2, 1);
    }
    @Test
    public void testZero() {
        assertThat(new Dimensions(0, 0))
                .extracting(Dimensions::getWidth, Dimensions::getHeight)
                .containsExactly(0, 0);
    }
    @Test
    public void testNegative() {
        assertThatThrownBy(() -> new Dimensions(1, -1))
                .isInstanceOf(IllegalArgumentException.class);
    }
    @Test
    public void testNegative2() {
        assertThatThrownBy(() -> new Dimensions(-1, 1))
                .isInstanceOf(IllegalArgumentException.class);
    }

}