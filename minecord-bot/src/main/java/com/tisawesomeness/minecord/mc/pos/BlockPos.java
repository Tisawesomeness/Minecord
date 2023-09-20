package com.tisawesomeness.minecord.mc.pos;

import com.tisawesomeness.minecord.util.Mth;

/**
 * A block position within a Minecraft world.
 */
public class BlockPos extends Vec3i {

    /** Max world border distance from origin, 1 chunk size from 30 mil limit */
    public static final int MAX_BORDER_DISTANCE = 29_999_984;
    /** Max distance from origin nether portals can generate, 128 blocks from max border */
    public static final int MAX_PORTAL_DISTANCE = 29_999_872;

    public BlockPos(int x, int y, int z) {
        super(x, y, z);
    }
    public BlockPos(Vec3i vec) {
        super(vec.getX(), vec.getY(), vec.getZ());
    }

    /**
     * Converts overworld to nether coordinates. Y values are clamped to nether height.
     * @return nether block pos
     */
    public BlockPos overworldToNether() {
        int netherY = Mth.clamp(y, 0, 127);
        return new BlockPos(horizontal().floorDiv(8).withY(netherY));
    }
    /**
     * Converts nether to overworld coordinates. X/Z values are clamped to world border.
     * @return overworld block pos
     */
    public BlockPos netherToOverworld() {
        int overworldX = Mth.clamp(x * 8, -MAX_PORTAL_DISTANCE, MAX_PORTAL_DISTANCE);
        int overworldZ = Mth.clamp(z * 8, -MAX_PORTAL_DISTANCE, MAX_PORTAL_DISTANCE);
        return new BlockPos(overworldX, y, overworldZ);
    }

    public SectionPos getSection() {
        return new SectionPos(floorDiv(16));
    }
    public Vec3i getPosWithinSection() {
        return floorMod(16);
    }

    public boolean isInBounds() {
        return -MAX_BORDER_DISTANCE <= x && x <= MAX_BORDER_DISTANCE &&
                -MAX_BORDER_DISTANCE <= z && z <= MAX_BORDER_DISTANCE;
    }

}
