package com.tisawesomeness.minecord.setting.parse;

import com.tisawesomeness.minecord.command.meta.CommandContext;
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
public class SmartSetParser extends SettingCommandHandler {
    @Getter private final @NonNull CommandContext ctx;
    @Getter private final @NonNull Setting<?> setting;

    /**
     * Tries to change the provided setting.
     */
    public void parse() {
        if (!ctx.isFromGuild()) {
            DbUser user = ctx.getUser(ctx.getE().getAuthor());
            new SettingChanger(this, user).parse();
            return;
        } else if (!userHasManageServerPermissions()) {
            ctx.noUserPermissions("You must have Manage Server permissions.");
            return;
        }
        changeIfNoChannelOverrides();
    }
    private void changeIfNoChannelOverrides() {
        DbChannel channel = ctx.getChannel(ctx.getE().getTextChannel());
        if (setting.get(channel).isPresent()) {
            String name = setting.getDisplayName().toLowerCase();
            String msg = String.format("The %s setting has a channel override in this channel.\n" +
                    "Use `%sset channel #%s %s <value>` to change it.",
                    name, ctx.getPrefix(), ctx.getE().getChannel().getName(), name);
            ctx.reply(msg);
            return;
        }
        new SettingChanger(this, ctx.getGuild(ctx.getE().getGuild())).parse();
    }
}
