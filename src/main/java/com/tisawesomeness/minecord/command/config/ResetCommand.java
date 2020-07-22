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

    public String getHelp() {
        return "Changes one of the bot's settings to the default.\n" +
                "See `{&}settings` for a list of settings and their possible values.\n" +
                "**Requires Manage Server permissions.**\n" +
                "\n" +
                "`<context>` can be:\n" +
                "- `server` - Settings changed for the server are used everywhere in that server...\n" +
                "- `channel <channel>` - ...unless you create a channel override.\n" +
                "- `dm` - Settings can also be changed in DMs.\n" +
                "\n" +
                "Examples:\n" +
                "- `{&}reset server prefix` - Reset the server prefix.\n" +
                "- `{&}reset channel #general prefix` - Reset the server prefix for #general. If a server prefix is set, that will be used instead.\n" +
                "- `{&}reset dm use menus` - Resets whether menus are used in DMs.\n";
    }

    public String getAdminHelp() {
        return "See `{&}help reset` for regular help.\n" +
                "\n" +
                "`<context>` can be:\n" +
                "- `guild <guild id>`\n" +
                "- `channel <channel>`\n" +
                "- `user <user id>`\n" +
                "\n" +
                "Examples:\n" +
                "- `{&}reset admin server 347765748577468416 prefix`\n" +
                "- `{&}reset admin channel 347909541264097281 prefix`\n" +
                "- `{&}reset admin channel #general use menus`\n" +
                "- `{&}reset admin user 211261249386708992 use menus`\n";
    }

    public Command.Result run(CommandContext ctx) {
        return new SettingCommandParser(ctx, SettingCommandType.RESET).parse();
    }
}
