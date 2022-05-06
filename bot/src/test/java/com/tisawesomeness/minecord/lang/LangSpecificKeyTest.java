package com.tisawesomeness.minecord.lang;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LangSpecificKeyTest {

    @Test
    @DisplayName("A key is equal to itself")
    public void testEqualsReflexive() {
        LangSpecificKey key = new LangSpecificKey("key", Lang.getDefault());
        assertThat(key).isEqualTo(key);
    }
    @Test
    @DisplayName("Two keys with the same str and lang are symmetrically equal")
    public void testEqualsSymmetric() {
        LangSpecificKey key1 = new LangSpecificKey("key", Lang.getDefault());
        LangSpecificKey key2 = new LangSpecificKey("key", Lang.getDefault());
        assertThat(key1).isEqualTo(key2).hasSameHashCodeAs(key2);
        assertThat(key2).isEqualTo(key1);
    }
    @Test
    @DisplayName("Two keys with the same lang and Unicode-equivalent strings are symmetrically equal")
    public void testEqualsNormalized() {
        LangSpecificKey key1 = new LangSpecificKey("\u00C5", Lang.EN_US);
        LangSpecificKey key2 = new LangSpecificKey("\u0041\u030A", Lang.EN_US);
        assertThat(key1).isEqualTo(key2).hasSameHashCodeAs(key2);
        assertThat(key2).isEqualTo(key1);
    }
    @Test
    @DisplayName("Key equals is transitive")
    public void testEqualsTransitive() {
        LangSpecificKey key1 = new LangSpecificKey("\u00C5\u00C5", Lang.EN_US);
        LangSpecificKey key2 = new LangSpecificKey("\u00C5\u0041\u030A", Lang.EN_US);
        LangSpecificKey key3 = new LangSpecificKey("\u0041\u030A\u00C5", Lang.EN_US);
        assertThat(key1).isEqualTo(key2).hasSameHashCodeAs(key2);
        assertThat(key2).isEqualTo(key3).hasSameHashCodeAs(key3);
        assertThat(key1).isEqualTo(key3).hasSameHashCodeAs(key3);
    }
    @Test
    @DisplayName("Two keys with different str or lang are not equal")
    public void testNotEquals() {
        LangSpecificKey key1 = new LangSpecificKey("key", Lang.EN_US);
        LangSpecificKey key2 = new LangSpecificKey("key2", Lang.EN_US);
        LangSpecificKey key3 = new LangSpecificKey("key", Lang.DE_DE);
        assertThat(key1).isNotEqualTo(key2).isNotEqualTo(key3);
        assertThat(key2).isNotEqualTo(key3);
    }

}
