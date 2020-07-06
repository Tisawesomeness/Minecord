package com.tisawesomeness.minecord;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LangTest {

    @Test
    @DisplayName("Lang resources load properly")
    public void testLang() {
        String key = "lang.countryName";
        assertEquals("USA", Lang.EN_US.get(key));
        assertEquals("Germany", Lang.DE_DE.get(key));
        assertEquals("Brazil", Lang.PT_BR.get(key));
    }
    @Test
    @DisplayName("The default lang is not in development")
    public void testDefaultLang() {
        assertFalse(Lang.getDefault().isInDevelopment());
    }
    @Test
    @DisplayName("Putting the lang's code in Lang.from() returns the same lang")
    public void testFromCode() {
        for (Lang lang : Lang.values()) {
            Optional<Lang> langFromCode = Lang.from(lang.getCode());
            assertTrue(langFromCode.isPresent());
            assertEquals(lang, langFromCode.get());
        }
    }
    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = {"nonsense", "en", "EN", "us", "US", " ", "_", "lang"})
    @DisplayName("Trying to get a lang from nonsense fails")
    public void testFromNonsense(String candidate) {
        assertEquals(Optional.empty(), Lang.from(candidate));
    }

}
