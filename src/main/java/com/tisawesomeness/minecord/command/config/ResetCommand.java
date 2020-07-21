package com.tisawesomeness.minecord.command.config;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.setting.parse.SettingCommandParser;
import com.tisawesomeness.minecord.setting.parse.SettingCommandType;

public class ResetCommand extends Command {
    public Command.CommandInfo getInfo() {
        return new Command.CommandInfo(
                "reset",
                "Reset the bot's settings.",
                "<context> <setting>",
                null,
                0,
                false,
                false,
                false
        );
    }

    public Command.Result run(CommandContext txt) {
        return new SettingCommandParser(txt, SettingCommandType.RESET).parse();
    }
}
