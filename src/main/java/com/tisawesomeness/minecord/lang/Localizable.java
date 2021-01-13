package com.tisawesomeness.minecord.lang;

import lombok.NonNull;

/**
 * Marks an object that can be localized for any language.
 */
public interface Localizable {

    /**
     * @return The case-sensitive localization key
     */
    @NonNull String getTranslationKey();

    /**
     * @return The arguments used to format the localized message, may be empty
     */
    Object[] getTranslationArgs();

}
