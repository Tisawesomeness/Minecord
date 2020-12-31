package com.tisawesomeness.minecord.setting.parse;

import com.tisawesomeness.minecord.command.CommandContext;
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
     */
    protected void displayOrParse(String title, SettingContainer obj) {
        CommandContext ctx = getCtx();
        SettingCommandType type = getType();
        int currentArg = getCurrentArg();
        if (type == SettingCommandType.QUERY) {
            ctx.triggerCooldown();
            displaySettings(title, s -> s.getDisplay(obj));
            return;
        }
        if (currentArg < ctx.getArgs().length) {
            new SettingChooser(this, obj).parse();
            return;
        }
        ctx.invalidArgs("You must specify a setting.");
    }
}
