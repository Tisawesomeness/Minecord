package com.tisawesomeness.minecord.command.config;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.setting.parse.SettingCommandParser;
import com.tisawesomeness.minecord.setting.parse.SettingCommandType;

import lombok.NonNull;

public class SetCommand extends AbstractConfigCommand {

    public @NonNull String getId() {
        return "set";
    }
    public CommandInfo getInfo() {
        return new Command.CommandInfo(
                false,
                false,
                false
        );
    }

    public Result run(CommandContext ctx) {
        return new SettingCommandParser(ctx, SettingCommandType.SET).parse();
    }
}
