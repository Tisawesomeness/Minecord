package com.tisawesomeness.minecord;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

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

}
