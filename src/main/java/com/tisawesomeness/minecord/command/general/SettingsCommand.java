package com.tisawesomeness.minecord.command.general;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.setting.parse.SettingCommandParser;
import com.tisawesomeness.minecord.setting.parse.SettingCommandType;

public class SettingsCommand extends Command {

    public CommandInfo getInfo() {
        return new CommandInfo(
                "settings",
                "Change the bot's settings, including prefix.",
                "[setting] [value]",
                new String[]{"config", "conf"},
                0,
                false,
                false,
                false
        );
    }

    // TODO change help to reflect new syntax
    public String getHelp() {
        return "`{&}settings` - Show all current settings and their possible values.\n" +
                "`{&}settings <setting> <value>` - Sets <setting> to <value>. The user must have **Manage Server** permissions.\n" +
                "\n" +
                "Examples:\n" +
                "- `{&}settings prefix mc!`\n" +
                "- {@}` settings prefix &`\n" +
                "- `{&}settings useMenus enabled`\n";
    }

    public String getAdminHelp() {
        return "`{&}settings` - Show all current settings and their possible values.\n" +
                "`{&}settings <setting> <value>` - Sets <setting> to <value>. The user must have **Manage Server** permissions.\n" +
                "`{&}settings <guild id> admin` - View settings for another guild.\n" +
                "`{&}settings <guild id> admin <setting> <value>` - Changes settings in another guild.\n" +
                "\n" +
                "Examples:\n" +
                "- `{&}settings prefix mc!`\n" +
                "- {@}` settings prefix &`\n" +
                "- `{&}settings useMenus enabled`\n" +
                "- `{&}settings 347765748577468416 admin`\n" +
                "- `{&}settings 347765748577468416 admin prefix mc!`\n";
    }

    public Result run(CommandContext txt) {
        return new SettingCommandParser(txt, SettingCommandType.QUERY).parse();
    }

}