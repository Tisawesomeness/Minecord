package com.tisawesomeness.minecord.command.general;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.database.DbGuild;
import com.tisawesomeness.minecord.setting.Setting;
import com.tisawesomeness.minecord.setting.SettingRegistry;
import com.tisawesomeness.minecord.util.DiscordUtils;
import com.tisawesomeness.minecord.util.type.Validation;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;

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
        String[] args = txt.args;
        MessageReceivedEvent e = txt.e;

        // If the author used the admin keyword and is an elevated user
        String sourcePrefix = txt.prefix;
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
            elevated = true;
        } else {
            if (!e.isFromGuild()) {
                return new Result(Outcome.WARNING, ":warning: This command is not available in DMs.");
            }
            gid = e.getGuild().getIdLong();
        }
		DbGuild guild = txt.bot.getDatabase().getCache().getGuild(gid);
        SettingRegistry settings = txt.bot.getSettings();

        // Build embed with list of settings
        if (args.length == 0) {
            String desc = String.format("`%ssettings <setting> <value>` - Change a setting.", sourcePrefix);
            EmbedBuilder eb = new EmbedBuilder()
                    .setTitle("Minecord Settings")
                    .setDescription(desc);
            String tag = e.getJDA().getSelfUser().getAsTag();
            for (Setting<?> setting : settings.settingsList) {
                String field = setting.getDescription(sourcePrefix, tag) +
                        String.format("\nCurrent: **`%s`**", setting.getEffective(guild));
                eb.addField(setting.getDisplayName(), field, false);
            }
            return new Result(Outcome.SUCCESS, txt.brand(eb).build());
        
        // Change setting
        } else if (args.length > 1) {

            // Check if user is elevated or has the manage messages permission
            if (elevated || !e.getMember().hasPermission(e.getTextChannel(), Permission.MANAGE_SERVER)) {
                return new Result(Outcome.WARNING, ":warning: You must have manage server permissions!");
            }

            // Adds arguments to a string until that string matches a setting
            // This is so user input with multiple words (like "use menus") can be detected
            StringBuilder settingName = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                settingName.append(args[i]);
                Optional<Setting<?>> settingOpt = settings.getSetting(settingName.toString());
                if (settingOpt.isPresent()) {
                    Setting<?> setting = settingOpt.get();

                    if (i == args.length - 1) {
                        return new Result(Outcome.WARNING, ":warning: You must specify a setting value.");
                    }

                    String settingValue = String.join("\n", Arrays.copyOfRange(args, i + 1, args.length));
                    try {
                        Validation<String> attempt = setting.tryToSet(guild, settingValue);
                        if (attempt.isValid()) {
                            return new Result(Outcome.SUCCESS, attempt.getValue());
                        }
                        return new Result(Outcome.WARNING, ":warning: " + attempt.getErrorMessage());
                    } catch (SQLException ex) {
                        ex.printStackTrace(); // Not printing exception to the user just to be safe
                    }
                    return new Result(Outcome.ERROR, ":x: There was an internal error.");

                }
                settingName.append(" ");
            }
            return new Result(Outcome.WARNING, ":warning: That setting does not exist.");

        }

        return new Result(Outcome.WARNING, ":warning: You must specify a setting value.");

    }

}