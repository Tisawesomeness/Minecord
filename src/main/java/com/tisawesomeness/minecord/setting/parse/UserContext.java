package com.tisawesomeness.minecord.setting.parse;

import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.command.Result;
import com.tisawesomeness.minecord.database.dao.DbUser;
import com.tisawesomeness.minecord.util.DiscordUtils;

import lombok.Getter;
import lombok.NonNull;

/**
 * Parses the user context, setting values that are used in DMs.
 */
public class UserContext extends SettingContext {
    @Getter private final @NonNull CommandContext ctx;
    @Getter private final @NonNull SettingCommandType type;
    private final boolean isAdmin;
    @Getter private int currentArg;

    public UserContext(SettingContextParser prev) {
        ctx = prev.getCtx();
        type = prev.getType();
        isAdmin = prev.isAdmin();
        currentArg = prev.getCurrentArg();
    }

    /**
     * Reads the user id if this is an admin command, otherwise use the current user.
     * <br>The user id is then saved as the context,
     * and is either displayed ({@code &settings}) or changed ({@code &set}/{@code &reset}).
     * @return The result of the command
     */
    public Result parse() {
        if (isAdmin) {
            if (currentArg >= ctx.getArgs().length) {
                return ctx.invalidArgs("You must specify a user id.");
            }
            return parseUserId();
        }
        return displayOrParseUser(ctx.getE().getAuthor().getIdLong());
    }

    private Result parseUserId() {
        String userArg = ctx.getArgs()[currentArg];
        if (!DiscordUtils.isDiscordId(userArg)) {
            return ctx.invalidArgs("Not a valid user id.");
        }
        currentArg++;
        return displayOrParseUser(Long.parseLong(userArg));
    }

    private Result displayOrParseUser(long uid) {
        DbUser user = ctx.getUser(uid);
        String title = isAdmin ? "DM Settings for " + uid : "DM Settings";
        return displayOrParse(title, user);
    }
}
