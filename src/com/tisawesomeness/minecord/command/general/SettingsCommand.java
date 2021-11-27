package com.tisawesomeness.minecord.command.general;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.database.Database;
import com.tisawesomeness.minecord.util.DiscordUtils;
import com.tisawesomeness.minecord.util.MessageUtils;

import java.util.Arrays;
import java.util.List;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SettingsCommand extends Command {

    @Override
    public CommandInfo getInfo() {
        return new CommandInfo(
            "settings",
            "Change the bot's settings, including prefix.",
            "[setting] [value]",
            new String[]{"config"},
            0,
            false,
            false,
            false
        );
    }

    public String getHelp() {
        return "`{&}settings` - Show all current settings and their possible values.\n" +
            "`{&}settings <setting> <value>` - Sets <setting> to <value>. The user must have **Manage Server** permissions.\n" +
            "\n" +
            "Examples:\n" +
            "- `{&}settings prefix mc!`\n" +
            "- {@}` settings prefix &`\n" +
            "- `{&}settings deleteCommands disabled`\n" +
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
            "- `{&}settings deleteCommands disabled`\n" +
            "- `{&}settings useMenus enabled`\n" +
            "- `{&}settings 347765748577468416 admin`\n" +
            "- `{&}settings 347765748577468416 admin prefix mc!`\n";
    }

    @Override
    public Result run(String[] args, MessageReceivedEvent e) throws Exception {

        // If the author used the admin keyword and is an elevated user
        String sourcePrefix = MessageUtils.getPrefix(e);
        String targetPrefix;
        long gid;
        boolean elevated = false;
		if (args.length > 1 && args[1].equals("admin") && Database.isElevated(e.getAuthor().getIdLong())) {
            if (!args[0].matches(DiscordUtils.idRegex)) {
                return new Result(Outcome.WARNING, ":warning: Not a valid ID!");
            }
            if (Bot.shardManager.getGuildById(args[0]) == null) {
                return new Result(Outcome.WARNING, ":warning: Minecord does not know that guild ID!");
            }
            gid = Long.parseLong(args[0]);
            args = Arrays.copyOfRange(args, 2, args.length);
            targetPrefix = Database.getPrefix(gid);
            elevated = true;
        } else {
            if (!e.isFromGuild()) {
                return new Result(Outcome.WARNING, ":warning: This command is not available in DMs.");
            }
            gid = e.getGuild().getIdLong();
            targetPrefix = sourcePrefix;
        }

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
                        e.getJDA().getSelfUser().getAsTag(), targetPrefix),
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
            if (elevated || !e.getMember().hasPermission(e.getTextChannel(), Permission.MANAGE_SERVER)) {
                return new Result(Outcome.WARNING, ":warning: You must have manage server permissions!");
            }
            
            if (args[0].equalsIgnoreCase("prefix")) {
                //No prefixes longer than 16 characters
                if (args[1].length() > 16) {
                    return new Result(Outcome.WARNING, ":warning: The prefix you specified is too long!");
                }
                //Easter egg for those naughty bois
                if (args[1].equals("'") && args[2].equals("OR") && args[3].equals("1=1")) {
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

    private static List<String> truthy = Arrays.asList("enabled", "yes", "y", "true", "t", "on", "1");
    private static List<String> falsy = Arrays.asList("disabled", "no", "n", "false", "f", "off", "0");
    private static Boolean parseBoolSetting(String input) {
        if (truthy.contains(input.toLowerCase())) {
            return true;
        }
        return falsy.contains(input.toLowerCase()) ? false : null;
    }

}