package com.tisawesomeness.minecord.setting.parse;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.database.dao.DbUser;
import com.tisawesomeness.minecord.util.DiscordUtils;

import lombok.Getter;
import lombok.NonNull;

/**
 * Parses the user context, setting values that are used in DMs.
 */
public class UserContext extends SettingContext {
    @Getter private final @NonNull CommandContext txt;
    @Getter private final @NonNull SettingCommandType type;
    private final boolean isAdmin;
    @Getter private int currentArg;

    public UserContext(SettingContextParser prev) {
        txt = prev.getTxt();
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
    public Command.Result parse() {
        if (isAdmin) {
            if (currentArg >= txt.args.length) {
                return new Command.Result(Command.Outcome.WARNING, ":warning: You must specify a user id.");
            }
            return parseUserId();
        }
        return displayOrParseUser(txt.e.getAuthor().getIdLong());
    }

    private Command.Result parseUserId() {
        String userArg = txt.args[currentArg];
        if (!DiscordUtils.isDiscordId(userArg)) {
            return new Command.Result(Command.Outcome.WARNING, ":warning: Not a valid user id.");
        }
        currentArg++;
        return displayOrParseUser(Long.parseLong(userArg));
    }

    private Command.Result displayOrParseUser(long uid) {
        DbUser user = txt.getUser(uid);
        String title = isAdmin ? "DM Settings for " + uid : "DM Settings";
        return displayOrParse(title, user);
    }
}
