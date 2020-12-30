package com.tisawesomeness.minecord.util.network;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

public class URLUtilsTest {

    @Test
    @DisplayName("Http addresses can be converted to https")
    public void testHttps() {
        assertThat(URLUtils.httpToHttps("http://example.com/")).isEqualTo("https://example.com/");
    }

    @ParameterizedTest(name = "{index} ==> String URL {0} is unmodified")
    @ValueSource(strings = {
            "sample text",
            "https://example.com/",
            "ftp://example.com/"
    })
    @EmptySource
    @DisplayName("httpToHttps() leaves non-http urls unmodified")
    public void testNotHttps(String candidate) {
        assertThat(URLUtils.httpToHttps(candidate)).isEqualTo(candidate);
    }

}
