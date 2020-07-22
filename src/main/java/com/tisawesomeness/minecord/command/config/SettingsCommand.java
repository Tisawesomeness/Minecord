package com.tisawesomeness.minecord.command.config;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.setting.parse.SettingCommandParser;
import com.tisawesomeness.minecord.setting.parse.SettingCommandType;

public class SettingsCommand extends Command {

    public CommandInfo getInfo() {
        return new CommandInfo(
                "settings",
                "Show all the bot's settings and their current values.",
                "[context/list]",
                new String[]{"config", "conf"},
                0,
                false,
                false,
                false
        );
    }

    public String getHelp() {
        return "Show all the bot's settings and their current values.\n" +
                "`{&}settings` - Shows what settings the bot is using for the current channel.\n" +
                "`{&}settings list` - Lists all server settings and any channel overrides.  **Requires Manage Server permissions.**\n" +
                "`{&}settings <context>` - View settings for the current server, the specified channel, or in DMs.\n" +
                "\n" +
                "`<context>` can be:\n" +
                "- `server` - Settings changed for the server are used everywhere in that server...\n" +
                "- `channel [channel]` - ...unless you create a channel override.\n" +
                "- `dm` - Settings can also be changed in DMs.\n" +
                "\n" +
                "Examples:\n" +
                "- `{&}settings server`\n" +
                "- `{&}settings channel`\n" +
                "- `{&}settings channel #general`\n" +
                "- `{&}settings dm`\n";
    }

    public String getAdminHelp() {
        return "See `{&}help settings` for regular help.\n" +
                "`{&}settings admin <channel id>` - Shows what settings the bot is using for the current channel.\n" +
                "`{&}settings admin list [guild id]` - Lists all server settings for the specified guild (default current) and any channel overrides.\n" +
                "`{&}settings admin <context>` - View settings for the a guild, channel, or user (DMs).\n" +
                "\n" +
                "`<context>` can be:\n" +
                "- `guild <guild id>`\n" +
                "- `channel <channel>`\n" +
                "- `user <user id>`\n" +
                "\n" +
                "Examples:\n" +
                "- `{&}settings admin list 347765748577468416`\n" +
                "- `{&}settings admin 347909541264097281`\n" +
                "- `{&}settings admin #general`\n" +
                "- `{&}settings admin server 347765748577468416`\n" +
                "- `{&}settings admin channel 347909541264097281`\n" +
                "- `{&}settings admin channel #general`\n" +
                "- `{&}settings admin user 211261249386708992`\n";
    }

    public Result run(CommandContext txt) {
        return new SettingCommandParser(txt, SettingCommandType.QUERY).parse();
    }

}