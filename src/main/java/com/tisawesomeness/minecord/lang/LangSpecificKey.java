package com.tisawesomeness.minecord.lang;

import lombok.NonNull;
import lombok.Value;

import java.text.Normalizer;

/**
 * A key for use in hash-based collections that is different per language but the same for equivalent Unicode strings,
 * ignoring case. For example, \u00C5 ({@code 00C5}) = \u0041\u030A ({@code 0041 030A}).
 * @see Normalizer
 */
@Value
public class LangSpecificKey {
    /**
     * The normalized form of the input string
     */
    @NonNull String normalized;
    /**
     * The language
     */
    Lang lang;

    /**
     * Creates a new lang-specific key.
     * @param str The input string
     * @param lang The language to attach to this key, also used to handle case
     */
    public LangSpecificKey(@NonNull String str, Lang lang) {
        // W3C recommends NFC
        if (Normalizer.isNormalized(str, Normalizer.Form.NFC)) {
            normalized = str.toLowerCase(lang.getLocale());
        } else {
            normalized = Normalizer.normalize(str, Normalizer.Form.NFC).toLowerCase(lang.getLocale());
        }
        this.lang = lang;
    }

    /**
     * Two keys are equal if they have the same normalized string and same language.
     * @param o Another object
     * @return Whether this key is equal to the other object
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof LangSpecificKey)) {
            return false;
        }
        LangSpecificKey other = (LangSpecificKey) o;
        // since the strings are normalized, no need for lang-specific equals
        return lang == other.lang && normalized.equals(other.normalized);
    }
    /**
     * Two keys are equal if they have the same normalized string and same language.
     * @return The hash code for this key
     */
    @Override
    public int hashCode() {
        return 31 * normalized.hashCode() + lang.hashCode();
    }

}
