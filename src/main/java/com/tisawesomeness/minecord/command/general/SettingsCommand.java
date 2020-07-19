package com.tisawesomeness.minecord.command.general;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.database.dao.DbChannel;
import com.tisawesomeness.minecord.database.dao.DbGuild;
import com.tisawesomeness.minecord.database.dao.DbUser;
import com.tisawesomeness.minecord.database.dao.SettingContainer;
import com.tisawesomeness.minecord.setting.Setting;
import com.tisawesomeness.minecord.setting.SettingRegistry;
import com.tisawesomeness.minecord.util.type.Either;
import com.tisawesomeness.minecord.util.type.Validation;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.TextChannel;

import javax.annotation.Nullable;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SettingsCommand extends Command {

    private final static Pattern DISCORD_ID = Pattern.compile("\\d{17,20}");
    private final static List<String> USER_WORDS = Arrays.asList("user", "dm", "dms");
    private final static List<String> GUILD_WORDS = Arrays.asList("guild", "server");

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
        String[] args = txt.args;

        if (args.length == 0) {
            return displayCurrentSettings(txt);
        }

        boolean admin = false;
        int currentArg = 0;
        if ("admin".equalsIgnoreCase(args[0])) {
            if (!txt.isElevated) {
                return new Result(Outcome.WARNING,
                        ":warning: You do not have permission to use elevated commands.");
            }
            if (args.length == 1) {
                String msg = String.format(":warning: Incorrect arguments. See `%shelp settings admin`.", txt.prefix);
                return new Result(Outcome.WARNING, msg);
            }
            admin = true;
            currentArg++;
        }

        String contextArg = args[currentArg];
        if ("list".equalsIgnoreCase(contextArg)) {
            return parseList(txt, currentArg, admin);
        }
        if (GUILD_WORDS.contains(contextArg.toLowerCase())) {
            return parseGuild(txt, currentArg, admin);
        }
        if ("channel".equalsIgnoreCase(contextArg)) {
            return parseChannel(txt, currentArg, admin);
        }
        if (USER_WORDS.contains(contextArg.toLowerCase())) {
            return parseUser(txt, currentArg, admin);
        }
        if (admin) {
            return parseAdminFallthrough(txt, contextArg);
        }
        String msg = String.format(":warning: Incorrect arguments. See `%shelp settings`.", txt.prefix);
        return new Result(Outcome.WARNING, msg);
    }

    // ===================== Terminal query subcommands =====================

    private static Result parseList(CommandContext txt, int currentArg, boolean admin) {
        long gid = txt.e.getGuild().getIdLong();
        if (admin && currentArg + 1 < txt.args.length) {
            String guildArg = txt.args[currentArg + 1];
            if (!DISCORD_ID.matcher(guildArg).matches()) {
                return new Result(Outcome.WARNING, ":warning: Not a valid guild id.");
            }
            gid = Long.parseLong(guildArg);
        } else if (!txt.e.isFromGuild()) {
            String msg = String.format(":warning: `%ssettings list` cannot be used in DMs.", txt.prefix);
            return new Result(Outcome.WARNING, msg);
        }
        return listSettings(txt, gid);
    }

    private static Result parseAdminFallthrough(CommandContext txt, String contextArg) {
        if (DISCORD_ID.matcher(contextArg).matches()) {
            long cid = Long.parseLong(contextArg);
            Optional<DbChannel> dbChannelOpt = txt.getChannel(cid);
            if (dbChannelOpt.isPresent()) {
                DbChannel dbChannel = dbChannelOpt.get();
                return displayCurrentSettings(txt, dbChannel.getId(), dbChannel.getGuildId());
            }
            GuildChannel c = txt.bot.getShardManager().getTextChannelById(cid);
            if (c != null) {
                return displayCurrentSettings(txt, cid, c.getGuild().getIdLong());
            }
            return new Result(Outcome.WARNING, ":warning: That channel does not exist.");
        }
        List<TextChannel> mentioned = txt.e.getMessage().getMentionedChannels();
        if (!mentioned.isEmpty()) {
            GuildChannel c = mentioned.get(0);
            return displayCurrentSettings(txt, c.getIdLong(), c.getGuild().getIdLong());
        }
        return new Result(Outcome.WARNING, ":warning: Not a valid channel format. Use a `#channel` mention or a valid ID.");
    }

    // ===================== Context Parsing =====================

    private static Result parseGuild(CommandContext txt, int currentArg, boolean admin) {
        if (!admin && !txt.e.isFromGuild()) {
            return new Result(Outcome.WARNING,
                    String.format(":warning: `%ssettings guild` cannot be used in DMs.", txt.prefix));
        }

        long gid;
        if (admin && currentArg + 1 < txt.args.length) {
            String guildArg = txt.args[currentArg + 1];
            if (!DISCORD_ID.matcher(guildArg).matches()) {
                return new Result(Outcome.WARNING, ":warning: Not a valid guild id.");
            }
            gid = Long.parseLong(guildArg);
        } else if (admin) {
            return new Result(Outcome.WARNING, ":warning: You must specify a guild id.");
        } else {
            gid = txt.e.getGuild().getIdLong();
        }

        DbGuild guild = txt.getGuild(gid);
        return displayOrParse(txt, currentArg, guild);
    }

    private static Result parseChannel(CommandContext txt, int currentArg, boolean admin) {
        if (!admin && !txt.e.isFromGuild()) {
            return new Result(Outcome.WARNING,
                    String.format(":warning: `%ssettings channel` cannot be used in DMs.", txt.prefix));
        }

        if (currentArg + 1 >= txt.args.length) {
            if (admin) {
                return new Result(Outcome.WARNING, ":warning: You must specify a channel id.");
            }
            return displaySettings(txt, txt.getChannel(txt.e.getTextChannel().getIdLong(), txt.e.getGuild().getIdLong()));
        }

        int channelArgIndex = currentArg + 1;
        String channelArg = txt.args[channelArgIndex];
        Either<String, Long> maybeCid = admin ? getChannelIdAdmin(txt, channelArg) : getChannelId(txt, channelArg);
        if (!maybeCid.isRight()) {
            return new Result(Outcome.WARNING, ":warning: " + maybeCid.getLeft());
        }
        long cid = maybeCid.getRight();

        if (admin) {
            Optional<DbChannel> channelOpt = txt.getChannel(cid);
            if (!channelOpt.isPresent()) {
                return new Result(Outcome.WARNING, ":warning: That channel is not in the database.");
            }
            DbChannel channel = channelOpt.get();
            return displayOrParse(txt, channelArgIndex, channel);
        }
        DbChannel channel = txt.getChannel(cid, txt.e.getGuild().getIdLong());
        return displayOrParse(txt, channelArgIndex, channel);
    }
    private static Either<String, Long> getChannelId(CommandContext txt, String input) {
        long gid = txt.e.getGuild().getIdLong();
        if (DISCORD_ID.matcher(input).matches()) {
            TextChannel c = txt.bot.getShardManager().getTextChannelById(input);
            if (c == null || c.getGuild().getIdLong() != gid) {
                return Either.left(
                        "That channel does not exist in the current guild or is not visible to you.");
            }
            return Either.right(c.getIdLong());
        }
        List<TextChannel> mentioned = txt.e.getMessage().getMentionedChannels();
        if (mentioned.isEmpty()) {
            return Either.left(
                    "Not a valid channel format. Use a `#channel` mention or a valid ID.");
        }
        TextChannel c = mentioned.get(0);
        if (c.getGuild().getIdLong() != gid) {
            return Either.left(
                    "That channel does not exist in the current guild or is not visible to you.");
        }
        return Either.right(c.getIdLong());
    }
    private static Either<String, Long> getChannelIdAdmin(CommandContext txt, String input) {
        if (DISCORD_ID.matcher(input).matches()) {
            return Either.right(Long.parseLong(input));
        }
        List<TextChannel> mentioned = txt.e.getMessage().getMentionedChannels();
        if (!mentioned.isEmpty()) {
            return Either.right(mentioned.get(0).getIdLong());
        }
        return Either.left("Not a valid channel format. Use a `#channel` mention or a valid ID.");
    }

    private static Result parseUser(CommandContext txt, int currentArg, boolean admin) {
        long uid = txt.e.getAuthor().getIdLong();
        if (admin && currentArg + 1 < txt.args.length) {
            String userArg = txt.args[currentArg + 1];
            if (!DISCORD_ID.matcher(userArg).matches()) {
                return new Result(Outcome.WARNING, ":warning: Not a valid user id.");
            }
            uid = Long.parseLong(userArg);
        }
        DbUser user = txt.getUser(uid);
        return displayOrParse(txt, currentArg, user);
    }

    private static Result displayOrParse(CommandContext txt, int currentArg, SettingContainer obj) {
        if (currentArg + 1 < txt.args.length) {
            return parseSetting(txt, currentArg + 1, obj);
        }
        return displaySettings(txt, obj);
    }

    // ===================== Setting Display =====================

    private static Result listSettings(CommandContext txt, long gid) {
        EmbedBuilder eb = new EmbedBuilder().setTitle("Minecord Settings");

        String guildField = txt.bot.getSettings().stream()
                .map(s -> String.format("%s: `%s`", s.getDisplayName(), s.getDisplay(txt.getGuild(gid))))
                .collect(Collectors.joining("\n"));
        eb.addField("Guild", guildField, false);

        List<DbChannel> channels = txt.getChannelsInGuild(gid);
        if (!channels.isEmpty()) {
            Guild g = txt.bot.getShardManager().getGuildById(gid);
            for (DbChannel channel : channels) {
                String field = txt.bot.getSettings().stream()
                        .map(s -> String.format("%s: `%s`", s.getDisplayName(), s.getDisplay(channel)))
                        .collect(Collectors.joining("\n"));
                eb.addField(getTitle(channel.getId(), g), field, false);
            }
        }
        return new Result(Outcome.SUCCESS, txt.brand(eb).build());
    }
    private static String getTitle(long cid, @Nullable Guild g) {
        if (g == null) {
            return String.valueOf(cid);
        }
        TextChannel c = g.getTextChannelById(cid);
        if (c == null) {
            return String.valueOf(cid);
        }
        return "#" + c.getName();
    }

    private static Result displayCurrentSettings(CommandContext txt) {
        EmbedBuilder eb = new EmbedBuilder().setTitle("Minecord Settings");
        String tag = txt.e.getJDA().getSelfUser().getAsTag();

        for (Setting<?> setting : txt.bot.getSettings()) {
            String field = setting.getDescription(txt.prefix, tag) +
                    String.format("\nCurrent: **`%s`**", setting.getDisplay(txt));
            eb.addField(setting.getDisplayName(), field, false);
        }
        return new Result(Outcome.SUCCESS, txt.brand(eb).build());
    }
    private static Result displayCurrentSettings(CommandContext txt, long cid, long gid) {
        EmbedBuilder eb = new EmbedBuilder().setTitle("Minecord Settings");
        String tag = txt.e.getJDA().getSelfUser().getAsTag();

        for (Setting<?> setting : txt.bot.getSettings()) {
            String field = setting.getDescription(txt.prefix, tag) +
                    String.format("\nCurrent: **`%s`**", setting.getDisplay(txt.getCache(), cid, gid));
            eb.addField(setting.getDisplayName(), field, false);
        }
        return new Result(Outcome.SUCCESS, txt.brand(eb).build());
    }
    private static Result displaySettings(CommandContext txt, SettingContainer obj) {
        EmbedBuilder eb = new EmbedBuilder().setTitle("Minecord Settings");
        String tag = txt.e.getJDA().getSelfUser().getAsTag();

        for (Setting<?> setting : txt.bot.getSettings()) {
            String field = setting.getDescription(txt.prefix, tag) +
                    String.format("\nCurrent: **`%s`**", setting.getDisplay(obj));
            eb.addField(setting.getDisplayName(), field, false);
        }
        return new Result(Outcome.SUCCESS, txt.brand(eb).build());
    }

    // ===================== Setting Parsing =====================

    private static Result parseSetting(CommandContext txt, int currentArg, SettingContainer obj) {
        String[] args = txt.args;
        SettingRegistry settings = txt.bot.getSettings();

        StringBuilder settingName = new StringBuilder();
        for (int i = currentArg; i < args.length; i++) {
            settingName.append(args[i]);
            Optional<Setting<?>> settingOpt = settings.getSetting(settingName.toString());
            if (settingOpt.isPresent()) {
                if (i == args.length - 1) {
                    return new Result(Outcome.WARNING, ":warning: You must specify a setting value.");
                }
                Setting<?> setting = settingOpt.get();
                return changeSetting(txt, currentArg + 1, setting, obj);
            }
        }
        return new Result(Outcome.WARNING, ":warning: That setting does not exist.");
    }

    // ===================== Value Parsing =====================

    private static Result changeSetting(CommandContext txt, int currentArg, Setting<?> setting, SettingContainer obj) {
        String[] args = txt.args;

        String settingValue = String.join(" ", Arrays.copyOfRange(args, currentArg, args.length));
        try {
            Validation<String> attempt = setting.tryToSet(obj, settingValue);
            return attemptToResult(attempt);
        } catch (SQLException ex) {
            ex.printStackTrace(); // Not printing exception to the user just to be safe
        }
        return new Result(Outcome.ERROR, ":x: There was an internal error.");
    }

    // ===================== Check for Success =====================

    private static Result attemptToResult(Validation<String> attempt) {
        if (attempt.isValid()) {
            return new Result(Outcome.SUCCESS, attempt.getValue());
        }
        String errorMsg = String.join("\n", attempt.getErrors());
        return new Result(Outcome.WARNING, ":warning: " + errorMsg);
    }

}