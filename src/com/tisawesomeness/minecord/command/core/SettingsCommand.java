package com.tisawesomeness.minecord.command.core;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.LegacyCommand;
import com.tisawesomeness.minecord.database.Database;
import com.tisawesomeness.minecord.util.MessageUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class SettingsCommand extends LegacyCommand {

    @Override
    public CommandInfo getInfo() {
        return new CommandInfo(
                "settings",
                "Change the bot's settings, including prefix.",
                "[<setting> <value>]",
                1000,
                true,
                false
        );
    }

    @Override
    public String[] getAliases() {
        return new String[]{"config"};
    }

    @Override
    public String getHelp() {
        return "`{&}settings` - Show all current settings and their possible values.\n" +
                "`{&}settings <setting> <value>` - Sets `<setting>` to `<value>`. The user must have **Manage Server** permissions.\n" +
                "\n" +
                "Examples:\n" +
                "- `{&}settings prefix mc!`\n" +
                "- {@}` settings prefix &`\n" +
                "- `{&}settings deleteCommands disabled`\n" +
                "- `{&}settings useMenus enabled`\n";
    }

    @Override
    public Result run(String[] args, MessageReceivedEvent e) throws Exception {
        String prefix = MessageUtils.getPrefix(e);
        return run(args, e, prefix, prefix, e.getGuild().getIdLong(), false);
    }

    public static Result run(String[] args, MessageReceivedEvent e, String sourcePrefix, String targetPrefix, long gid, boolean elevated) throws SQLException {

        // Build embed with list of settings
        if (args.length == 0) {
            EmbedBuilder eb = new EmbedBuilder()
                    .setTitle("Minecord Settings")
                    .setColor(Bot.color)
                    .addField("prefix",
                            String.format(
                                    "The prefix used before every command.\n" +
                                            "`@%s command` will work regardless of prefix.\n" +
                                            "Possible values: Any text between 1-16 characters.\n" +
                                            "Current: **`%s`**",
                                    e.getJDA().getSelfUser().getEffectiveName(), targetPrefix),
                            false)
                    .addField("deleteCommands",
                            String.format(
                                    "If enabled, the bot will delete command messages to clear up space.\n" +
                                            "Requires Manage Message permissions.\n" +
                                            "Possible values: `enabled`, `disabled`\n" +
                                            "Current: **%s**",
                                    isEnabled(Database.getDeleteCommands(gid))
                            ),
                            false)
                    .addField("useMenus",
                            String.format(
                                    "If enabled, the bot will use a reaction menu for `%srecipe` and `%singredient` if possible.\n" +
                                            "Requires Manage Message and Add Reaction permissions.\n" +
                                            "Possible values: `enabled`, `disabled`\n" +
                                            "Current: **%s**",
                                    sourcePrefix, sourcePrefix, isEnabled(Database.getUseMenu(gid))
                            ),
                            false)
                    .setDescription(String.format(
                            "`%ssettings <setting> <value>` - Change a setting.",
                            sourcePrefix
                    ));
            return new Result(Outcome.SUCCESS, MessageUtils.addFooter(eb).build());

            // Change setting
        } else if (args.length > 1) {

            // Check if user is elevated or has the manage messages permission
            if (elevated || !e.getMember().hasPermission(e.getGuildChannel(), Permission.MANAGE_SERVER)) {
                return new Result(Outcome.WARNING, ":warning: You must have manage server permissions!");
            }

            if (args[0].equalsIgnoreCase("prefix")) {
                //No prefixes longer than 16 characters
                if (args[1].length() > 16) {
                    return new Result(Outcome.WARNING, ":warning: The prefix you specified is too long!");
                }
                //Easter egg for those naughty bois
                if (args.length == 4 && args[1].equals("'") && args[2].equals("OR") && args[3].equals("1=1")) {
                    return new Result(Outcome.WARNING, "Nice try.");
                }
                // Check for duplicate
                if (args[1].equals(targetPrefix)) {
                    return new Result(Outcome.SUCCESS, "That is the current prefix.");
                }
                //Set new prefix
                Database.changePrefix(gid, args[1]);
                return new Result(Outcome.SUCCESS, String.format(":arrows_counterclockwise: Prefix changed to `%s`.", args[1]));
            } else if (args[0].equalsIgnoreCase("deleteCommands")) {
                Boolean toSet = parseBoolSetting(args[1]);
                if (toSet == null) {
                    return new Result(Outcome.WARNING, ":warning: Not a valid value!");
                }
                // Check for duplicate
                if (toSet == Database.getDeleteCommands(gid)) {
                    return new Result(Outcome.SUCCESS, String.format("`deleteCommands` is already %s.", isEnabledText(toSet)));
                }
                Database.changeDeleteCommands(gid, toSet);
                return new Result(Outcome.SUCCESS, String.format(":arrows_counterclockwise: Command Deletion changed to `%s`.", isEnabledText(toSet)));
            } else if (args[0].equalsIgnoreCase("useMenus")) {
                Boolean toSet = parseBoolSetting(args[1]);
                if (toSet == null) {
                    return new Result(Outcome.WARNING, ":warning: Not a valid value!");
                }
                // Check for duplicate
                if (toSet == Database.getUseMenu(gid)) {
                    return new Result(Outcome.SUCCESS, String.format("`useMenus` is already %s.", isEnabledText(toSet)));
                }
                Database.changeUseMenu(gid, toSet);
                return new Result(Outcome.SUCCESS, String.format(":arrows_counterclockwise: Menus changed to `%s`.", isEnabledText(toSet)));
            } else {
                return new Result(Outcome.WARNING, ":warning: That setting does not exist.");
            }

        }

        return new Result(Outcome.WARNING, ":warning: You must specify a value!");

    }

    private static String isEnabled(boolean setting) {
        return setting ? ":white_check_mark: Enabled" : ":x: Disabled";
    }
    private static String isEnabledText(boolean setting) {
        return setting ? "enabled" : "disabled";
    }

    private static final List<String> truthy = Arrays.asList("enabled", "yes", "y", "true", "t", "on", "1");
    private static final List<String> falsy = Arrays.asList("disabled", "no", "n", "false", "f", "off", "0");
    private static Boolean parseBoolSetting(String input) {
        if (truthy.contains(input.toLowerCase())) {
            return true;
        }
        return falsy.contains(input.toLowerCase()) ? false : null;
    }

}
