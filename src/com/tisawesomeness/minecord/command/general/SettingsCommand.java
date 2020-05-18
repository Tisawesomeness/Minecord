package com.tisawesomeness.minecord.command.general;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.database.Database;
import com.tisawesomeness.minecord.util.DiscordUtils;
import com.tisawesomeness.minecord.util.MessageUtils;

import java.awt.Color;
import java.util.Arrays;

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

    @Override
    public Result run(String[] args, MessageReceivedEvent e) throws Exception {

        // If the author used the admin keyword and is an elevated user
        long gid = e.getGuild().getIdLong();
		if (args.length > 1 && args[1].equals("admin") && Database.isElevated(e.getAuthor().getIdLong())) {
            if (!args[0].matches(DiscordUtils.idRegex)) {
                return new Result(Outcome.WARNING, ":warning: Not a valid ID!");
            }
            if (Bot.shardManager.getGuildById(args[0]) == null) {
                return new Result(Outcome.WARNING, ":warning: Minecord does not know that guild ID!");
            }
            gid = Long.valueOf(args[0]);
            args = Arrays.copyOfRange(args, 2, args.length);
        }

        // Build embed with list of settings
        if (args.length == 0) {
            EmbedBuilder eb = new EmbedBuilder()
                .setTitle("Minecord Settings")
                .setColor(Color.GREEN)
                .addField("prefix",
                    String.format(
                        "The prefix used before every command.\n" +
                        "`@%s command` will work regardless of prefix.\n" +
                        "Possible values: Any text between 1-16 characters.\n" +
                        "Current: **`%s`**",
                        e.getJDA().getSelfUser().getAsTag(), Database.getPrefix(gid)),
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
                        Database.getPrefix(gid), Database.getPrefix(gid), isEnabled(Database.getUseMenu(gid))
                    ),
                false)
                .setDescription(String.format(
                    "`%ssettings <setting> <value>` - Change a setting.",
                    Database.getPrefix(gid), Database.getPrefix(gid)
                ));
            return new Result(Outcome.SUCCESS, MessageUtils.addFooter(eb).build());
        
        // Change setting
        } else if (args.length > 1) {

            // Check if user is elevated or has the manage messages permission
            if (!Database.isElevated(e.getAuthor().getIdLong())
                    && !e.getMember().hasPermission(e.getTextChannel(), Permission.MESSAGE_MANAGE)) {
                return new Result(Outcome.WARNING,
                        ":warning: You must have permission to manage messages in this channel!");
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
                if (args[1].equals(Database.getPrefix(gid))) {
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
                return new Result(Outcome.SUCCESS, String.format(":arrows_counterclockwise: Command Deletion changed to `%s`.", args[1]));
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
                return new Result(Outcome.SUCCESS, String.format(":arrows_counterclockwise: Menus changed to `%s`.", args[1]));
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

    private static Boolean parseBoolSetting(String input) {
        if (input.equalsIgnoreCase("enabled")) {
            return true;
        }
        return input.equalsIgnoreCase("disabled") ? false : null;
    }

}