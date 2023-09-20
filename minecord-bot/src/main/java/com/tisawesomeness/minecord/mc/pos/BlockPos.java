package com.tisawesomeness.minecord.mc.pos;

import com.tisawesomeness.minecord.util.Mth;

public class BlockPos extends Vec3i {

    // Max distance from origin nether portals can generate, 128 blocks from max border
    public static final int MAX_PORTAL_DISTANCE = 29_999_872;

    public BlockPos(int x, int y, int z) {
        super(x, y, z);
    }
    public BlockPos(Vec3i vec) {
        super(vec.getX(), vec.getY(), vec.getZ());
    }

    public BlockPos overworldToNether() {
        int netherY = Mth.clamp(y, 0, 127);
        return new BlockPos(horizontal().floorDiv(8).withY(netherY));
    }
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
}
