package com.tisawesomeness.minecord.util.type;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.tisawesomeness.minecord.testutil.assertion.CustomAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class OptionalBoolTest {

    @Test
    public void testTrue() {
        assertThat(OptionalBool.of(true)).isPresent().isNotEmpty().hasValue(true).isTrue();
    }
    @Test
    public void testFalse() {
        assertThat(OptionalBool.of(false)).isPresent().isNotEmpty().hasValue(false).isFalse();
    }
    @Test
    public void testEmpty() {
        assertThat(OptionalBool.empty()).isEmpty().isNotPresent();
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> OptionalBool.empty().getAsBool());
    }

    @Test
    public void testIfPresent() {
        AtomicBoolean changed = new AtomicBoolean(false);
        OptionalBool.of(true).ifPresent(changed::set);
        assertThat(changed).isTrue();
    }
    @Test
    public void testOrElse() {
        assertThat(OptionalBool.of(true).orElse(false)).isTrue();
        assertThat(OptionalBool.of(false).orElse(true)).isFalse();
        assertThat(OptionalBool.empty().orElse(true)).isTrue();
    }
    @Test
    public void testOrElseGet() {
        assertThat(OptionalBool.of(true).orElseGet(() -> false)).isTrue();
        assertThat(OptionalBool.of(false).orElseGet(() -> true)).isFalse();
        assertThat(OptionalBool.empty().orElseGet(() -> true)).isTrue();
    }
    @Test
    public void testOrElseThrow() {
        assertThat(OptionalBool.of(true).orElseThrow(IllegalStateException::new)).isTrue();
        assertThat(OptionalBool.of(false).orElseThrow(IllegalStateException::new)).isFalse();
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> OptionalBool.empty().orElseThrow(IllegalStateException::new));
    }

    @Test
    public void testEquals() {
        assertThat(OptionalBool.of(true))
                .isEqualTo(OptionalBool.of(true)).hasSameHashCodeAs(OptionalBool.of(true))
                .isNotEqualTo(OptionalBool.of(false))
                .isNotEqualTo(OptionalBool.empty());
        assertThat(OptionalBool.of(false))
                .isNotEqualTo(OptionalBool.of(true))
                .isEqualTo(OptionalBool.of(false)).hasSameHashCodeAs(OptionalBool.of(false))
                .isNotEqualTo(OptionalBool.empty());
        assertThat(OptionalBool.empty())
                .isNotEqualTo(OptionalBool.of(true))
                .isNotEqualTo(OptionalBool.of(false))
                .isEqualTo(OptionalBool.empty()).hasSameHashCodeAs(OptionalBool.empty());
    }
    @Test
    public void testToString() {
        assertThat(OptionalBool.of(true)).hasToString("OptionalBool[true]");
        assertThat(OptionalBool.of(false)).hasToString("OptionalBool[false]");
        assertThat(OptionalBool.empty()).hasToString("OptionalBool.empty");
    }

}
