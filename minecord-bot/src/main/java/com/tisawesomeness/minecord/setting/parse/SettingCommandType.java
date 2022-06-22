package com.tisawesomeness.minecord.setting.parse;

/**
 * Represents the type of setting command and what it does to the setting when executed.
 */
public enum SettingCommandType {
    /**
     * {@code &settings} lists or displays settings
     */
    QUERY(),
    /**
     * {@code &set} changes settings
     */
    SET(),
    /**
     * {@code &reset} resets settings (not set to default)
     */
    RESET();
}
