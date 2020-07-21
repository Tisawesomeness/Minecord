package com.tisawesomeness.minecord.command.config;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.setting.parse.SettingCommandParser;
import com.tisawesomeness.minecord.setting.parse.SettingCommandType;

public class SetCommand extends Command {
    public Command.CommandInfo getInfo() {
        return new Command.CommandInfo(
                "set",
                "Change the bot's settings, including prefix.",
                "<context> <setting> <value>",
                null,
                0,
                false,
                false,
                false
        );
    }

    public Result run(CommandContext txt) {
        return new SettingCommandParser(txt, SettingCommandType.SET).parse();
    }
}
