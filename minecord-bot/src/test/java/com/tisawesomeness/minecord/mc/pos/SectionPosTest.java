package com.tisawesomeness.minecord.mc.pos;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionPosTest {

    @Test
    public void testRegion() {
        assertThat(new SectionPos(1500, 0, -600).getRegionFileName())
                .isEqualTo("r.46.-19.mca");
    }
    @Test
    public void testRegionZero() {
        assertThat(new SectionPos(0, 0, 0).getRegionFileName())
                .isEqualTo("r.0.0.mca");
    }

    @Test
    public void testWithinRegion() {
        assertThat(new SectionPos(30, 0, -3).getPosWithinRegion())
                .isEqualTo(new Vec2i(30, 29));
    }
    @Test
    public void testWithinRegionZero() {
        assertThat(new SectionPos(0, 0, 0).getPosWithinRegion())
                .isEqualTo(new Vec2i(0, 0));
    }

    @Test
    public void testToString() {
        assertThat(new SectionPos(27, 95, -205))
                .hasToString("27, 95, -205");
    }

}
