package com.tisawesomeness.minecord.mc.pos;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

public class Vec3Test {

    @ParameterizedTest
    @ValueSource(strings = {
            "37, -5, 1056",
            "37,-5,1056",
            "37 / -5 / 1056",
            "37 -5 1056",
            "37  -5  1056",
            "+37, -5, +1056",
            "x = 37, y = -5, z = 1056",
            "x=37, y=-5, z=1056",
            "x=37,y=-5,z=1056",
            "X=37, Y=-5, Z=1056",
            "x:37, y:-5, z:1056",
            "x: 37, y: -5, z: 1056",
            "y: -5, z: 1056, x: 37",
            "x37y-5z1056",
            "(37, -5, 1056)",
            "(37 -5 1056)",
            "<37, -5, 1056>",
            "[37, -5, 1056]",
            "{37, -5, 1056}",
            "(x:37, y:-5, z:1056)",
            "37.0, -5.0, 1056.0",
            "37.2, -4.9, 1055.5"
    })
    public void testParse(String input) {
        assertThat(Vec3.parse(input).map(Vec3::round))
                .contains(new Vec3i(37, -5, 1056));
    }
    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "nonsense",
            "x",
            "x:",
            "37, -5",
            "37,-51056",
            "37, -5, 1056, 783",
            "37, -, 1056",
            "a, -5, 1056",
            "37, a, 1056",
            "37, -5, a",
            "=37, y=-5, z=1056",
            "x:37, :-5, z:1056",
            "y:37, y:-5, z:1056",
            "(37, -5, 1056>",
            "((37, -5, 1056)",
            "(37, -5, 1056))",
            "37, -5, 1056)",
            "(37, -5, 1056",
            "(invalid37, -5, 1056)",
            "37,,-5,1056",
            "x:37 ,-5.0 / 1056 ",
            "37, y=-5, z=1056",
            "NaN, NaN, NaN",
            "Infinity, Infinity, Infinity"
    })
    public void testParseInvalid(String input) {
        assertThat(Vec3.parse(input))
                .isEmpty();
    }

    @Test
    public void testRound() {
        assertThat(new Vec3(2.3, 4.5, -1.7).round())
                .isEqualTo(new Vec3i(2, 5, -2));
    }
    @Test
    public void testRoundZero() {
        assertThat(new Vec3(0.0, 0.0, 0.0).round())
                .isEqualTo(new Vec3i(0, 0, 0));
    }

}
