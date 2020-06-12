package com.tisawesomeness.minecord;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("Main")
public class TestMain {
    /**
     * Make sure running tests is not JVM-specific
     */
    @BeforeAll
    public static void setLocale() {
        Locale.setDefault(Locale.US);
    }
    @Test
    @DisplayName("Tests are working")
    public void testMain() {
        assertEquals(1, 1);
    }
    @Test
    @DisplayName("Lang resources load properly")
    public void testLang() {
        assertEquals("USA", Main.getDefaultLang("ignore").getString("CountryName"));
        assertEquals("Germany", Main.getLang(new Locale("de", "DE")).getString("CountryName"));
        assertEquals("Brazil", Main.getLang(new Locale("pt", "BR")).getString("CountryName"));
    }
}
