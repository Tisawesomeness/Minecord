package com.tisawesomeness.minecord.mc.pos;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class Vec2 extends Vec {

    private final double x;
    private final double z;

    public Vec2i round() {
        return new Vec2i((int) Math.round(x), (int) Math.round(z));
    }

    @Override
    public String toString() {
        return x + ", " + z;
    }

}
