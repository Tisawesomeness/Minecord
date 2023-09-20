package com.tisawesomeness.minecord.mc.pos;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BlockPosTest {

    @Test
    public void testOverworldToNether() {
        assertThat(new BlockPos(1872, 62, -5661).overworldToNether())
                .isEqualTo(new BlockPos(234, 62, -708));
    }
    @Test
    public void testOverworldToNetherZero() {
        assertThat(new BlockPos(0, 0, 0).overworldToNether())
                .isEqualTo(new BlockPos(0, 0, 0));
    }
    @Test
    public void testOverworldToNetherLimit() {
        assertThat(new BlockPos(0, -16, 0).overworldToNether())
                .isEqualTo(new BlockPos(0, 0, 0));
    }
    @Test
    public void testOverworldToNetherLimit2() {
        assertThat(new BlockPos(0, 306, 0).overworldToNether())
                .isEqualTo(new BlockPos(0, 127, 0));
    }

    @Test
    public void testNetherToOverworld() {
        assertThat(new BlockPos(1872, 62, -5661).netherToOverworld())
                .isEqualTo(new BlockPos(14976, 62, -45288));
    }
    @Test
    public void testNetherToOverworldZero() {
        assertThat(new BlockPos(0, 0, 0).netherToOverworld())
                .isEqualTo(new BlockPos(0, 0, 0));
    }
    @Test
    public void testNetherToOverworldLimit() {
        assertThat(new BlockPos(10_000_000, 64, -10_000_000).netherToOverworld())
                .isEqualTo(new BlockPos(BlockPos.MAX_PORTAL_DISTANCE, 64, -BlockPos.MAX_PORTAL_DISTANCE));
    }

    @Test
    public void testSection() {
        assertThat(new BlockPos(-115, 60, -30).getSection())
                .isEqualTo(new SectionPos(-8, 3, -2));
    }
    @Test
    public void testSectionZero() {
        assertThat(new BlockPos(0, 0, 0).getSection())
                .isEqualTo(new SectionPos(0, 0, 0));
    }

    @Test
    public void testWithinSection() {
        assertThat(new BlockPos(-115, 60, -30).getPosWithinSection())
                .isEqualTo(new Vec3i(13, 12, 2));
    }
    @Test
    public void testWithinSectionZero() {
        assertThat(new BlockPos(0, 0, 0).getPosWithinSection())
                .isEqualTo(new Vec3i(0, 0, 0));
    }

    @Test
    public void testInBounds() {
        assertThat(new BlockPos(29_999_900, 64, 29_999_900).isInBounds())
                .isTrue();
    }
    @Test
    public void testOutOfBounds() {
        assertThat(new BlockPos(30_000_000, 64, 29_999_900).isInBounds())
                .isFalse();
    }

    @Test
    public void testToString() {
        assertThat(new BlockPos(27, 95, -205))
                .hasToString("27, 95, -205");
    }

}
