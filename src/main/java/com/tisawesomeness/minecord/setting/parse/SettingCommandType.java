package com.tisawesomeness.minecord.setting.parse;

import com.tisawesomeness.minecord.command.CommandContext;

public enum SettingCommandType {
    QUERY(),
    SET(),
    RESET();

    public SettingCommandParser getParser(CommandContext txt) {
        return new SettingCommandParser(txt, this);
    }
}
