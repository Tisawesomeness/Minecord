package com.tisawesomeness.minecord.mc.pos;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class Vec2i {
    protected final int x;
    protected final int z;

    public Vec2i floorDiv(int n) {
        return new Vec2i(Math.floorDiv(x, n), Math.floorDiv(z, n));
    }
    public Vec2i floorMod(int n) {
        return new Vec2i(Math.floorMod(x, n), Math.floorMod(z, n));
    }

    public Vec3i withY(int y) {
        return new Vec3i(x, y, z);
    }

    @Override
    public String toString() {
        return x + ", " + z;
    }
}
