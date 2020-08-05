package com.tisawesomeness.minecord.setting.parse;

import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.command.Result;
import com.tisawesomeness.minecord.database.dao.DbChannel;
import com.tisawesomeness.minecord.database.dao.DbUser;
import com.tisawesomeness.minecord.setting.Setting;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Changes a setting for the guild if executed in a server, or for the user if executed in DMs.
 * <br>Will fail if there is a channel override (since that should be changed instead).
 */
@RequiredArgsConstructor
public class SmartSetParser {
    @Getter private final @NonNull CommandContext ctx;
    @Getter private final @NonNull Setting<?> setting;

    /**
     * Tries to change the provided setting.
     * @return The result of the command
     */
    public Result parse() {
        if (!ctx.e.isFromGuild()) {
            DbUser user = ctx.getUser(ctx.e.getAuthor());
            return new SettingChanger(this, user).parse();
        } else if (!SettingCommandHandler.userHasManageServerPermissions(ctx.e)) {
            return ctx.warn("You must have Manage Server permissions.");
        }
        return changeIfNoChannelOverrides();
    }
    private Result changeIfNoChannelOverrides() {
        DbChannel channel = ctx.getChannel(ctx.e.getTextChannel());
        if (setting.get(channel).isPresent()) {
            String name = setting.getDisplayName().toLowerCase();
            String msg = String.format("The %s setting has a channel override in this channel.\n" +
                    "Use `%sset channel #%s %s <value>` to change it.",
                    name, ctx.prefix, ctx.e.getChannel().getName(), name);
            return ctx.reply(msg);
        }
        return new SettingChanger(this, ctx.getGuild(ctx.e.getGuild())).parse();
    }
}
