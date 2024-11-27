package com.tisawesomeness.minecord;

/**
 * Stores the emotes used in menus and their string codepoints
 */
public enum Emote {
    STAR("U+2b50"),
    FULL_BACK("U+23ee"),
    SKIP_BACK("U+23ea"),
    BACK("U+25c0"),
    FORWARD("U+25b6"),
    SKIP_FORWARD("U+23e9"),
    FULL_FORWARD("U+23ed"),
    T("U+1f1f9", ":regional_indicator_t:"),
    UP("U+1f53c", ":arrow_up_small:"),
    N1("U+31U+fe0fU+20e3", ":one:"),
    N2("U+32U+fe0fU+20e3", ":two:"),
    N3("U+33U+fe0fU+20e3", ":three:"),
    N4("U+34U+fe0fU+20e3", ":four:"),
    N5("U+35U+fe0fU+20e3", ":five:"),
    N6("U+36U+fe0fU+20e3", ":six:"),
    N7("U+37U+fe0fU+20e3", ":seven:"),
    N8("U+38U+fe0fU+20e3", ":eight:"),
    N9("U+39U+fe0fU+20e3", ":nine:"),
    MORE("U+1f504", ":arrows_counterclockwise:");

    private final String codepoint;
    private final String text;

    Emote(String codepoint) {
        this(codepoint, null);
    }

    Emote(String codepoint, String text) {
        this.codepoint = codepoint;
        this.text = text;
    }

    /**
     * @return The string codepoint of the emote
     */
    public String getCodepoint() {
        return codepoint;
    }

    /**
     * @return The text used to show the emote in a message
     */
    public String getText() {
        return text;
    }

    /**
     * @return The Emote associated with a number 1-9
     */
    public static Emote valueOf(int i) {
        switch (i) {
            case 1:
                return N1;
            case 2:
                return N2;
            case 3:
                return N3;
            case 4:
                return N4;
            case 5:
                return N5;
            case 6:
                return N6;
            case 7:
                return N7;
            case 8:
                return N8;
            case 9:
                return N9;
        }
        return null;
    }
}
