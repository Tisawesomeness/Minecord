package com.tisawesomeness.minecord.util.type;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class BooleanConsumerTest {

    @Test
    public void testAccept() {
        List<Boolean> list = new ArrayList<>();
        BooleanConsumer consumer = list::add;
        consumer.accept(true);
        assertThat(list).containsExactly(true);
    }

    @Test
    public void testAndThen() {
        List<Boolean> list = new ArrayList<>();
        BooleanConsumer consumer1 = list::add;
        BooleanConsumer consumer2 = consumer1.andThen(list::add);
        consumer2.accept(true);
        assertThat(list).containsExactly(true, true);
    }

}