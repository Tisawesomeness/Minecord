package com.tisawesomeness.minecord;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MainIT {

    @Test
    @DisplayName("Integration tests are working")
    public void testMain() {
        assertThat(1).isEqualTo(1);
    }

}
