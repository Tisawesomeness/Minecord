package com.tisawesomeness.minecord.mc.pos;

public class RegionPos extends Vec2i {

    public RegionPos(Vec2i vec) {
        super(vec.getX(), vec.getZ());
    }

    public SectionPos getSectionPos() {
        return new SectionPos(scale(32).withY(0));
    }
    public String getFileName() {
        return String.format("r.%d.%d.mca", x, z);
    }

}
