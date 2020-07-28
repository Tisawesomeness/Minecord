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
                false,
                false,
                false
        );
    }

    public String getHelp() {
        return "Changes one of the bot's settings.\n" +
                "See `{&}settings` for a list of settings and their possible values.\n" +
                "**Requires Manage Server permissions.**\n" +
                "\n" +
                "`<context>` can be:\n" +
                "- `server` - Settings changed for the server are used everywhere in that server...\n" +
                "- `channel <channel>` - ...unless you create a channel override.\n" +
                "- `dm` - Settings can also be changed in DMs.\n" +
                "\n" +
                "Examples:\n" +
                "- `{&}set server prefix mc!` - Change the server prefix to `mc!`.\n" +
                "- `{&}set channel #general prefix &` - Change the server prefix to `&` only in #general.\n" +
                "- `{&}set server use menus enabled` - Enable menus for the server.\n" +
                "- `{&}set dm use menus disabled` - Disable menus in DMs.\n";
    }

    public String getAdminHelp() {
        return "See `{&}help set` for regular help.\n" +
                "\n" +
                "`<context>` can be:\n" +
                "- `guild <guild id>`\n" +
                "- `channel <channel>`\n" +
                "- `user <user id>`\n" +
                "\n" +
                "Examples:\n" +
                "- `{&}set admin server 347765748577468416 prefix mc!`\n" +
                "- `{&}set admin channel 347909541264097281 prefix &`\n" +
                "- `{&}set admin channel #general use menus enabled`\n" +
                "- `{&}set admin user 211261249386708992 use menus disabled`\n";
    }

    public Result run(CommandContext ctx) {
        return new SettingCommandParser(ctx, SettingCommandType.SET).parse();
    }
}
