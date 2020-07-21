package com.tisawesomeness.minecord.setting.parse;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.database.dao.DbChannel;
import com.tisawesomeness.minecord.setting.Setting;
import com.tisawesomeness.minecord.util.DiscordUtils;

import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The class that handles {@code &settings list}, listing all settings in a guild and the channel overrides.
 */
public class ListSubcommand {
    private final @NonNull CommandContext txt;
    private final boolean isAdmin;
    private final int currentArg;

    public ListSubcommand(SettingContextParser prev) {
        txt = prev.getTxt();
        isAdmin = prev.isAdmin();
        currentArg = prev.getCurrentArg();
    }

    /**
     * Lists settings for the specified guild if admin, otherwise the current guild if not in DMs.
     * @return The result of the command
     */
    public Command.Result parse() {
        if (isAdmin) {
            return parseAdminList();
        } else if (!txt.e.isFromGuild()) {
            String msg = String.format(":warning: `%ssettings list` cannot be used in DMs.", txt.prefix);
            return new Command.Result(Command.Outcome.WARNING, msg);
        } else if (!Objects.requireNonNull(txt.e.getMember()).hasPermission(Permission.MANAGE_SERVER)) {
            return new Command.Result(Command.Outcome.WARNING, ":warning: You do not have Manage Server permissions.");
        }
        return listSettings("All Channel Overrides", txt.e.getGuild().getIdLong());
    }
    private Command.Result parseAdminList() {
        if (currentArg < txt.args.length) {
            return parseGuildAndList();
        } else if (!txt.e.isFromGuild()) {
            String msg = String.format(
                    ":warning: `%ssettings admin list` with no guild id cannot be used in DMs.", txt.prefix);
            return new Command.Result(Command.Outcome.WARNING, msg);
        }
        return listSettings("All Channel Overrides", txt.e.getGuild().getIdLong());
    }

    private Command.Result parseGuildAndList() {
        String guildArg = txt.args[currentArg];
        if (!DiscordUtils.isDiscordId(guildArg)) {
            return new Command.Result(Command.Outcome.WARNING, ":warning: Not a valid guild id.");
        }
        return listSettings("All Channel Overrides for Guild " + guildArg, Long.parseLong(guildArg));
    }

    private Command.Result listSettings(String title, long gid) {
        EmbedBuilder eb = new EmbedBuilder().setTitle(title);
        String guildField = buildField(s -> s.getDisplay(txt.getGuild(gid)));
        eb.addField("Guild", guildField, false);

        List<DbChannel> channels = txt.getChannelsInGuild(gid);
        if (!channels.isEmpty()) {
            Guild g = txt.bot.getShardManager().getGuildById(gid);
            for (DbChannel channel : channels) {
                String fieldTitle = getChannelNameIfGuildExists(channel.getId(), g);
                String field = buildField(s -> s.getDisplay(channel));
                eb.addField(fieldTitle, field, false);
            }
        }
        return new Command.Result(Command.Outcome.SUCCESS, txt.brand(eb).build());
    }

    private String buildField(Function<? super Setting<?>, String> displayFunction) {
        return txt.bot.getSettings().stream()
                .map(s -> inlineSetting(s, displayFunction))
                .collect(Collectors.joining("\n"));
    }
    private static String inlineSetting(Setting<?> s, Function<? super Setting<?>, String> displayFunction) {
        String display = displayFunction.apply(s);
        return s.getDisplayName() + ": " + MarkdownUtil.monospace(display);
    }

    private static String getChannelNameIfGuildExists(long cid, @Nullable Guild g) {
        if (g == null) {
            return String.valueOf(cid);
        }
        TextChannel c = g.getTextChannelById(cid);
        if (c == null) {
            return String.valueOf(cid);
        }
        return "#" + c.getName();
    }
}
