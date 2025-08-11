package com.tisawesomeness.minecord.mc.pos;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class Vec3i {

    protected final int x;
    protected final int y;
    protected final int z;

    public Vec3i scale(int n) {
        return new Vec3i(x * n, y * n, z * n);
    }
    public Vec3i floorDiv(int n) {
        return new Vec3i(Math.floorDiv(x, n), Math.floorDiv(y, n), Math.floorDiv(z, n));
    }
    public Vec3i floorMod(int n) {
        return new Vec3i(Math.floorMod(x, n), Math.floorMod(y, n), Math.floorMod(z, n));
    }

    public Vec2i horizontal() {
        return new Vec2i(x, z);
    }

    @Override
    public String toString() {
        return x + ", " + y + ", " + z;
    }
}
