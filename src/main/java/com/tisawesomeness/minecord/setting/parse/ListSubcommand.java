package com.tisawesomeness.minecord.setting.parse;

import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.command.Result;
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
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The class that handles {@code &settings list}, listing all settings in a guild and the channel overrides.
 */
public class ListSubcommand {
    private final @NonNull CommandContext ctx;
    private final boolean isAdmin;
    private final int currentArg;

    public ListSubcommand(SettingContextParser prev) {
        ctx = prev.getCtx();
        isAdmin = prev.isAdmin();
        currentArg = prev.getCurrentArg();
    }

    /**
     * Lists settings for the specified guild if admin, otherwise the current guild if not in DMs.
     * @return The result of the command
     */
    public Result parse() {
        if (isAdmin) {
            return parseAdminList();
        } else if (!ctx.e.isFromGuild()) {
            return ctx.warn(String.format("`%ssettings list` cannot be used in DMs.", ctx.prefix));
        } else if (!ctx.userHasPermission(Permission.MANAGE_SERVER)) {
            return ctx.noUserPermissions("You do not have Manage Server permissions.");
        }
        ctx.triggerCooldown();
        return listSettings("All Channel Overrides", ctx.e.getGuild().getIdLong());
    }
    private Result parseAdminList() {
        if (currentArg < ctx.args.length) {
            return parseGuildAndList();
        } else if (!ctx.e.isFromGuild()) {
            return ctx.warn(String.format(
                    "`%ssettings admin list` with no guild id cannot be used in DMs.", ctx.prefix));
        }
        return listSettings("All Channel Overrides", ctx.e.getGuild().getIdLong());
    }

    private Result parseGuildAndList() {
        String guildArg = ctx.args[currentArg];
        if (!DiscordUtils.isDiscordId(guildArg)) {
            return ctx.warn("Not a valid guild id.");
        }
        return listSettings("All Channel Overrides for Guild " + guildArg, Long.parseLong(guildArg));
    }

    private Result listSettings(String title, long gid) {
        EmbedBuilder eb = new EmbedBuilder().setTitle(title);
        String guildField = buildField(s -> s.getDisplay(ctx.getGuild(gid)));
        eb.addField("Guild", guildField, false);

        List<DbChannel> channels = ctx.getChannelsInGuild(gid);
        if (!channels.isEmpty()) {
            Guild g = ctx.bot.getShardManager().getGuildById(gid);
            for (DbChannel channel : channels) {
                String fieldTitle = getChannelNameIfGuildExists(channel.getId(), g);
                String field = buildField(s -> s.getDisplay(channel));
                eb.addField(fieldTitle, field, false);
            }
        }
        return ctx.reply(eb);
    }

    private String buildField(Function<? super Setting<?>, String> displayFunction) {
        return ctx.bot.getSettings().stream()
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
