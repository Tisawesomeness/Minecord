package com.tisawesomeness.minecord.setting.parse;

import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.command.Result;
import com.tisawesomeness.minecord.database.dao.DbGuild;
import com.tisawesomeness.minecord.util.DiscordUtils;

import lombok.Getter;
import lombok.NonNull;

/**
 * Parses the guild context, the setting values that are used when there is no channel override.
 */
public class GuildContext extends SettingContext {
    @Getter private final @NonNull CommandContext ctx;
    @Getter private final @NonNull SettingCommandType type;
    private final boolean isAdmin;
    @Getter private int currentArg;

    public GuildContext(SettingContextParser prev) {
        ctx = prev.getCtx();
        type = prev.getType();
        isAdmin = prev.isAdmin();
        currentArg = prev.getCurrentArg();
    }

    /**
     * Reads the guild id if this is an admin command, or the current guild id if not in DMs.
     * <br>The guild id is then saved as the context,
     * and is either displayed ({@code &settings}) or changed ({@code &set}/{@code &reset}).
     * @return The result of the command
     */
    public Result parse() {
        if (isAdmin) {
            return parseGuildId();
        }
        if (ctx.e.isFromGuild()) {
            return displayOrParseGuildId(ctx.e.getGuild().getIdLong());
        }
        return ctx.warn(String.format("`%ssettings guild` cannot be used in DMs.", ctx.prefix));
    }

    private Result parseGuildId() {
        if (currentArg < ctx.args.length) {
            String guildArg = ctx.args[currentArg];
            if (!DiscordUtils.isDiscordId(guildArg)) {
                return ctx.warn("Not a valid guild id.");
            }
            currentArg++;
            return displayOrParseGuildId(Long.parseLong(guildArg));
        }
        return ctx.warn("You must specify a guild id.");
    }

    private Result displayOrParseGuildId(long gid) {
        DbGuild guild = ctx.getGuild(gid);
        String title = isAdmin ? "Guild Settings for " + gid : "Guild Settings";
        return displayOrParse(title, guild);
    }
}
