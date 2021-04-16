package com.tisawesomeness.minecord.mc.player;

import com.tisawesomeness.minecord.util.Lists;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class NameChangeTest {

    private static final Username TESTING_USERNAME = new Username("Tis_awesomeness");
    private static final Username ORIGINAL_USERNAME = new Username("tis_awesomeness");
    private static final long TESTING_TIMESTAMP = 1438695830000L;

    @Test
    @DisplayName("withTimestamp() creates a name change with username and timestamp")
    public void testWithTimestamp() {
        NameChange nc = NameChange.withTimestamp(TESTING_USERNAME, TESTING_TIMESTAMP);
        assertThat(Optional.of(nc.getUsername())).get().isEqualTo(TESTING_USERNAME);
        assertThat(nc.isOriginal()).isFalse();
        assertThat(nc.getTime())
                .isPresent()
                .map(Instant::toEpochMilli)
                .get().isEqualTo(TESTING_TIMESTAMP);
    }

    @Test
    @DisplayName("original() creates a name change with only a username")
    public void testOriginal() {
        NameChange nc = NameChange.original(ORIGINAL_USERNAME);
        assertThat(Optional.of(nc.getUsername())).get().isEqualTo(ORIGINAL_USERNAME);
        assertThat(nc.isOriginal()).isTrue();
        assertThat(nc.getTime()).isEmpty();
    }

    @Test
    @DisplayName("Sorting a list of name changes orders original names first")
    public void testComparisonOriginal() {
        NameChange original = NameChange.original(ORIGINAL_USERNAME);
        NameChange changed = NameChange.withTimestamp(TESTING_USERNAME, TESTING_TIMESTAMP);
        List<NameChange> history = Lists.of(changed, original);
        assertThat(Lists.sort(history)).containsExactly(original, changed);
    }

    @Test
    @DisplayName("Sorting a list of name changes orders earlier timestamps first")
    public void testComparisonTimestamp() {
        NameChange first = NameChange.withTimestamp(TESTING_USERNAME, TESTING_TIMESTAMP);
        NameChange second = NameChange.withTimestamp(TESTING_USERNAME, TESTING_TIMESTAMP + 1L);
        NameChange third = NameChange.withTimestamp(TESTING_USERNAME, TESTING_TIMESTAMP + 2L);
        List<NameChange> history = Lists.of(second, third, first);
        assertThat(Lists.sort(history)).containsExactly(first, second, third);
    }

    @Test
    @DisplayName("Sorting a list of name changes orders names alphabetically")
    public void testComparisonName() {
        Username nameA = new Username("a");
        Username nameB = new Username("b");
        Username nameC = new Username("c");
        NameChange first = NameChange.withTimestamp(nameA, TESTING_TIMESTAMP);
        NameChange second = NameChange.withTimestamp(nameB, TESTING_TIMESTAMP);
        NameChange third = NameChange.withTimestamp(nameC, TESTING_TIMESTAMP);
        List<NameChange> history = Lists.of(third, second, first);
        assertThat(Lists.sort(history)).containsExactly(first, second, third);
    }

}
