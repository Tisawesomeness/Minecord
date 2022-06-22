package com.tisawesomeness.minecord.command.config;

import com.tisawesomeness.minecord.command.meta.CommandContext;
import com.tisawesomeness.minecord.setting.parse.SettingCommandParser;
import com.tisawesomeness.minecord.setting.parse.SettingCommandType;

import lombok.NonNull;

public class SettingsCommand extends AbstractConfigCommand {

    public @NonNull String getId() {
        return "settings";
    }

    public void run(String[] args, CommandContext ctx) {
        new SettingCommandParser(ctx, SettingCommandType.QUERY).parse();
    }

}