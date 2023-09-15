package com.tisawesomeness.minecord.mc;

import com.tisawesomeness.minecord.common.util.IO;
import com.tisawesomeness.minecord.util.Strings;
import com.tisawesomeness.minecord.util.type.Dimensions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.tisawesomeness.minecord.testutil.assertion.CustomAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

public class FaviconTest {

    private static final String HYPIXEL_FAVICON = IO.loadResource("hypixelFavicon.txt", FaviconTest.class);

    @Test
    public void testFromEmpty() {
        Favicon icon = Favicon.from(new byte[0]);
        assertThat(icon.getData()).isEmpty();
        assertThat(icon.usesNewlines()).isFalse();
        assertThat(icon.validate()).asLeft().isEqualTo(Favicon.PngError.TOO_SHORT);
    }

    @Test
    public void testParse() {
        Optional<Favicon> iconOpt = Favicon.parse(HYPIXEL_FAVICON);
        assertThat(iconOpt).isNotEmpty();
        Favicon icon = iconOpt.get();
        assertThat(icon.usesNewlines()).isFalse();
        assertThat(icon.validate())
                .asRight(InstanceOfAssertFactories.type(Dimensions.class))
                .extracting(Dimensions::getWidth, Dimensions::getHeight)
                .containsExactly(Favicon.EXPECTED_SIZE, Favicon.EXPECTED_SIZE);
    }
    @Test
    public void testParseNewline() {
        Optional<Favicon> iconOpt = Favicon.parse(HYPIXEL_FAVICON + "\n");
        assertThat(iconOpt).isNotEmpty();
        Favicon icon = iconOpt.get();
        assertThat(icon.usesNewlines()).isTrue();
        assertThat(icon.validate())
                .asRight(InstanceOfAssertFactories.type(Dimensions.class))
                .extracting(Dimensions::getWidth, Dimensions::getHeight)
                .containsExactly(Favicon.EXPECTED_SIZE, Favicon.EXPECTED_SIZE);
    }
    @Test
    public void testParseEmpty() {
        assertThat(Favicon.parse("")).isEmpty();
    }
    @Test
    public void testParseInvalid() {
        assertThat(Favicon.parse("data:image/png;base64,!")).isEmpty();
    }
    @Test
    public void testParseBadSignature() {
        String modified = Strings.replaceCharAt(HYPIXEL_FAVICON, 22, 'j');
        Optional<Favicon> iconOpt = Favicon.parse(modified);
        assertThat(iconOpt).isNotEmpty();
        Favicon icon = iconOpt.get();
        assertThat(icon.validate())
                .asLeft()
                .isEqualTo(Favicon.PngError.BAD_SIGNATURE);
    }

}
