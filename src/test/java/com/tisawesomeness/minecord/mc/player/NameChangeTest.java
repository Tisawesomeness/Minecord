package com.tisawesomeness.minecord.mc.player;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class NameChangeTest {

    private static final Username TESTING_USERNAME = Username.from("Tis_awesomeness").orElseThrow();
    private static final Username ORIGINAL_USERNAME = Username.from("tis_awesomeness").orElseThrow();
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
        List<NameChange> history = List.of(changed, original);
        assertThat(sort(history)).containsExactly(original, changed);
    }

    @Test
    @DisplayName("Sorting a list of name changes orders earlier timestamps first")
    public void testComparisonTimestamp() {
        NameChange first = NameChange.withTimestamp(TESTING_USERNAME, TESTING_TIMESTAMP);
        NameChange second = NameChange.withTimestamp(TESTING_USERNAME, TESTING_TIMESTAMP + 1L);
        NameChange third = NameChange.withTimestamp(TESTING_USERNAME, TESTING_TIMESTAMP + 2L);
        List<NameChange> history = List.of(second, third, first);
        assertThat(sort(history)).containsExactly(first, second, third);
    }

    @Test
    @DisplayName("Sorting a list of name changes orders names alphabetically")
    public void testComparisonName() {
        Username nameA = Username.from("a").orElseThrow();
        Username nameB = Username.from("b").orElseThrow();
        Username nameC = Username.from("c").orElseThrow();
        NameChange first = NameChange.withTimestamp(nameA, TESTING_TIMESTAMP);
        NameChange second = NameChange.withTimestamp(nameB, TESTING_TIMESTAMP);
        NameChange third = NameChange.withTimestamp(nameC, TESTING_TIMESTAMP);
        List<NameChange> history = List.of(third, second, first);
        assertThat(sort(history)).containsExactly(first, second, third);
    }

    // seriously why does Collections.sort() modify the list?
    // and why is the argument List<T> and not List<? extends T>?
    // java wack
    private static <T extends Comparable<? super T>> List<T> sort(Collection<? extends T> list) {
        return list.stream().sorted().collect(Collectors.toList());
    }

}
