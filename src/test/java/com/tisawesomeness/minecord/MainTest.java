package com.tisawesomeness.minecord;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("Main")
public class MainTest {

    @Test
    @DisplayName("Tests are working")
    public void testMain() {
        assertThat(1).isEqualTo(1);
    }

}
