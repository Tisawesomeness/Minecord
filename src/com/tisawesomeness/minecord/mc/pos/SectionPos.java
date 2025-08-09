package com.tisawesomeness.minecord.mc.pos;

/**
 * A 16x16x16 section of a Minecraft world. {@link #horizontal()} gives the chunk coordinates.
 */
public class SectionPos extends Vec3i {

    public SectionPos(int x, int y, int z) {
        super(x, y, z);
    }
    public SectionPos(Vec3i vec) {
        super(vec.getX(), vec.getY(), vec.getZ());
    }

    public BlockPos getBlockPos() {
        return new BlockPos(scale(16));
    }
    public RegionPos getRegionPos() {
        return new RegionPos(horizontal().floorDiv(32));
    }
    public Vec2i getPosWithinRegion() {
        return horizontal().floorMod(32);
    }

}
