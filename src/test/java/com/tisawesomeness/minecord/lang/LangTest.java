package com.tisawesomeness.minecord.lang;

import com.tisawesomeness.minecord.lang.Lang;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class LangTest {

    @Test
    @DisplayName("The default lang is not in development")
    public void testDefaultLang() {
        assertThat(Lang.getDefault().isInDevelopment()).isFalse();
    }
    @Test
    @DisplayName("Putting the lang's code in Lang.from() returns the same lang")
    public void testFromCode() {
        for (Lang lang : Lang.values()) {
            Optional<Lang> langFromCode = Lang.from(lang.getCode());
            assertThat(langFromCode).contains(lang);
        }
    }
    @ParameterizedTest(name = "{index} ==> Registering lang from nonsense ''{0}''")
    @EmptySource
    @ValueSource(strings = {"nonsense", "en", "EN", "us", "US", " ", "_", "lang"})
    @DisplayName("Trying to get a lang from nonsense fails")
    public void testFromNonsense(String candidate) {
        assertThat(Lang.from(candidate)).isNotPresent();
    }

}
