package com.tisawesomeness.minecord.mc.pos;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class Vec3iTest {

    @Test
    public void testNew() {
        assertThat(new Vec3i(0, 0, 0))
                .extracting(Vec3i::getX, Vec3i::getY, Vec3i::getZ)
                .containsExactly(0, 0, 0);
    }

    @Test
    public void testDivide() {
        assertThat(new Vec3i(-4, 0, 11).floorDiv(4))
                .isEqualTo(new Vec3i(-1, 0, 2));
    }
    @Test
    public void testModulo() {
        assertThat(new Vec3i(-2, 14, 15).floorMod(7))
                .isEqualTo(new Vec3i(5, 0, 1));
    }

    @Test
    public void testToString() {
        assertThat(new Vec3i(27, 95, -205))
                .hasToString("27, 95, -205");
    }

}
