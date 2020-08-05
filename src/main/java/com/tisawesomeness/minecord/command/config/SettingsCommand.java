package com.tisawesomeness.minecord.command.config;

import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.command.Result;
import com.tisawesomeness.minecord.setting.parse.SettingCommandParser;
import com.tisawesomeness.minecord.setting.parse.SettingCommandType;

import lombok.NonNull;

public class SettingsCommand extends AbstractConfigCommand {

    public @NonNull String getId() {
        return "settings";
    }

    public Result run(String[] args, CommandContext ctx) {
        return new SettingCommandParser(ctx, SettingCommandType.QUERY).parse();
    }

}