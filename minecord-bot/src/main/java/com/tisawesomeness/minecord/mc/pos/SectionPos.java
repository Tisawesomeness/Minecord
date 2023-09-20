package com.tisawesomeness.minecord.mc.pos;

public class SectionPos extends Vec3i {
    public SectionPos(int x, int y, int z) {
        super(x, y, z);
    }
    public SectionPos(Vec3i vec) {
        super(vec.getX(), vec.getY(), vec.getZ());
    }

    public String getRegionFileName() {
        Vec2i regionVec = horizontal().floorDiv(32);
        return String.format("r.%d.%d.mca", regionVec.getX(), regionVec.getZ());
    }
    public Vec2i getPosWithinRegion() {
        return horizontal().floorMod(32);
    }
}
