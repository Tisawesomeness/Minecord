package com.tisawesomeness.minecord;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("Main")
public class MainTest {

    @Test
    @DisplayName("Tests are working")
    public void testMain() {
        assertEquals(1, 1);
    }

}
