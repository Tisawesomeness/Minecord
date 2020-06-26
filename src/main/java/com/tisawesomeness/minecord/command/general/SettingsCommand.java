package com.tisawesomeness.minecord.command.general;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.setting.ServerSetting;
import com.tisawesomeness.minecord.util.DiscordUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.List;

public class SettingsCommand extends Command {

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

    public Result run(CommandContext txt) throws Exception {
        String[] args = txt.args;
        MessageReceivedEvent e = txt.e;

        // If the author used the admin keyword and is an elevated user
        String sourcePrefix = txt.prefix;
        String targetPrefix;
        long gid;
        boolean elevated = false;
		if (args.length > 1 && args[1].equals("admin") && txt.isElevated) {
            if (!args[0].matches(DiscordUtils.idRegex)) {
                return new Result(Outcome.WARNING, ":warning: Not a valid ID!");
            }
            if (txt.bot.getShardManager().getGuildById(args[0]) == null) {
                return new Result(Outcome.WARNING, ":warning: Minecord does not know that guild ID!");
            }
            gid = Long.valueOf(args[0]);
            args = Arrays.copyOfRange(args, 2, args.length);
            targetPrefix = txt.bot.getSettings().prefix.getEffectiveGuild(gid);
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
            String desc = String.format("`%ssettings <setting> <value>` - Change a setting.", sourcePrefix);
            EmbedBuilder eb = new EmbedBuilder()
                    .setTitle("Minecord Settings")
                    .setDescription(desc);
            String tag = e.getJDA().getSelfUser().getAsTag();
            for (ServerSetting<?> setting : txt.bot.getSettings().serverSettings) {
                String field = setting.getDescription(sourcePrefix, tag) +
                        String.format("\nCurrent: **`%s`**", setting.getEffectiveGuild(gid));
                eb.addField(setting.getDisplayName(), field, false);
            }
            return new Result(Outcome.SUCCESS, txt.brand(eb).build());
        
        // Change setting
        } else if (args.length > 1) {

            // Check if user is elevated or has the manage messages permission
            if (elevated || !e.getMember().hasPermission(e.getTextChannel(), Permission.MANAGE_SERVER)) {
                return new Result(Outcome.WARNING, ":warning: You must have manage server permissions!");
            }

            for (ServerSetting<?> setting : txt.bot.getSettings().serverSettings) {
                if (setting.isAlias(args[0])) {
                    return new Result(Outcome.SUCCESS, setting.setGuild(gid, args[1]));
                }
            }
            return new Result(Outcome.WARNING, ":warning: That setting does not exist.");

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