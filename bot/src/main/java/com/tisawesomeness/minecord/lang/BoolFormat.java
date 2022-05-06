package com.tisawesomeness.minecord.lang;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;

import java.util.EnumSet;

/**
 * A format, such as true/false or on/off, for a boolean.
 */
public enum BoolFormat {
    /**
     * True/False
     */
    TRUE("true", "false", true),
    /**
     * Yes/No
     */
    YES("yes", "no", true),
    /**
     * Enabled/Disabled
     */
    ENABLED("enabled", "disabled", true),
    /**
     * T/F
     */
    TRUE_SHORT("trueShort", "falseShort", false),
    /**
     * Y/N
     */
    YES_SHORT("yesShort", "noShort", false),
    /**
     * On/Off
     */
    ON("on", "off", false);

    /**
     * All formats
     */
    public static final EnumSet<BoolFormat> ALL = EnumSet.allOf(BoolFormat.class);
    /**
     * All formats except short formats
     */
    public static final EnumSet<BoolFormat> ALL_LONG = EnumSet.of(TRUE, YES, ENABLED, ON);
    /**
     * True/False, Yes/No and short versions
     */
    public static final EnumSet<BoolFormat> TRUE_OR_YES = EnumSet.of(TRUE, TRUE_SHORT, YES, YES_SHORT);
    /**
     * Only True/False and Yes/No
     */
    public static final EnumSet<BoolFormat> TRUE_OR_YES_LONG = EnumSet.of(TRUE, YES);
    /**
     * Enabled/Disabled and On/Off
     */
    public static final EnumSet<BoolFormat> ENABLING = EnumSet.of(ENABLED, ON);

    @Getter(AccessLevel.PROTECTED) private final @NonNull String trueKey;
    @Getter(AccessLevel.PROTECTED) private final @NonNull String falseKey;
    @Getter private final boolean includeDefaultLang;
    BoolFormat(@NonNull String t, @NonNull String f, boolean includeDefaultLang) {
        trueKey = "general." + t;
        falseKey = "general." + f;
        this.includeDefaultLang = includeDefaultLang;
    }

}
