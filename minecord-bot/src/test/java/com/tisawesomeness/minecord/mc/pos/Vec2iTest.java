package com.tisawesomeness.minecord.mc.pos;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class Vec2iTest {

    @Test
    public void testNew() {
        assertThat(new Vec2i(0, 0))
                .extracting(Vec2i::getX, Vec2i::getZ)
                .containsExactly(0, 0);
    }

    @Test
    public void testDivide() {
        assertThat(new Vec2i(-4, 11).floorDiv(4))
                .isEqualTo(new Vec2i(-1, 2));
    }
    @Test
    public void testModulo() {
        assertThat(new Vec2i(-2, 15).floorMod(7))
                .isEqualTo(new Vec2i(5, 1));
    }

    @Test
    public void testToString() {
        assertThat(new Vec2i(27, -205))
                .hasToString("27, -205");
    }

}
