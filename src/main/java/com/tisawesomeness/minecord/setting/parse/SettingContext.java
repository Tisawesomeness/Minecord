package com.tisawesomeness.minecord.setting.parse;

import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.command.Result;
import com.tisawesomeness.minecord.database.dao.SettingContainer;

import lombok.NonNull;

/**
 * Represents the context argument for all setting commands.
 */
public abstract class SettingContext extends SettingCommandHandler {
    public abstract @NonNull SettingCommandType getType();
    public abstract int getCurrentArg();

    /**
     * If this command is a query, then all settings for this context are displayed.
     * <br>If this command sets or resets, then the {@link SettingChooser} takes over.
     * @param title The title of the embed with all displayed settings
     * @param obj The context object, which is either a guild, channel, or user
     * @return The result of the command
     */
    protected Result displayOrParse(String title, SettingContainer obj) {
        CommandContext ctx = getCtx();
        SettingCommandType type = getType();
        int currentArg = getCurrentArg();
        if (type == SettingCommandType.QUERY) {
            return displaySettings(title, s -> s.getDisplay(obj));
        }
        if (currentArg < ctx.args.length) {
            return new SettingChooser(this, obj).parse();
        }
        return ctx.warn("You must specify a setting.");
    }
}
