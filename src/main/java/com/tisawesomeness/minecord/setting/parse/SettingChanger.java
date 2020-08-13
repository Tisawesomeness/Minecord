package com.tisawesomeness.minecord.setting.parse;

import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.command.Result;
import com.tisawesomeness.minecord.database.dao.SettingContainer;
import com.tisawesomeness.minecord.setting.Setting;
import com.tisawesomeness.minecord.util.type.Validation;

import lombok.NonNull;

import java.sql.SQLException;
import java.util.StringJoiner;

/**
 * The final handler in the chain, actually tries to change the setting requested.
 * <br>If this is called from a set command, a value is required.
 */
public class SettingChanger {
    private final @NonNull CommandContext ctx;
    private final @NonNull SettingCommandType type;
    private final @NonNull SettingContainer obj;
    private final @NonNull Setting<?> setting;
    private int currentArg;

    public SettingChanger(SettingChooser prev, Setting<?> setting) {
        ctx = prev.getCtx();
        type = prev.getType();
        obj = prev.getObj();
        this.setting = setting;
        currentArg = prev.getCurrentArg();
    }
    public SettingChanger(SmartSetParser prev, SettingContainer obj) {
        ctx = prev.getCtx();
        type = SettingCommandType.SET;
        this.obj = obj;
        setting = prev.getSetting();
        currentArg = 0;
    }

    /**
     * Resets the setting if from a reset command.
     * <br>Parses the value and changes the setting if from a set command.
     * @return The result of the command
     */
    public Result parse() {
        ctx.triggerCooldown();
        if (type == SettingCommandType.RESET) {
            return resetSetting();
        }
        return changeSetting(getSettingValue());
    }

    private String getSettingValue() {
        String[] args = ctx.args;
        StringJoiner sj = new StringJoiner(" ");
        while (currentArg < args.length) {
            sj.add(args[currentArg]);
            currentArg++;
        }
        return sj.toString();
    }

    private Result changeSetting(String settingValue) {
        try {
            Validation<String> attempt = setting.tryToSet(obj, settingValue);
            return convertAttemptToResult(ctx, attempt);
        } catch (SQLException ex) {
            ex.printStackTrace(); // Not printing exception to the user just to be safe
        }
        return ctx.err("There was an internal error.");
    }
    private Result resetSetting() {
        try {
            Validation<String> attempt = setting.tryToReset(obj);
            return convertAttemptToResult(ctx, attempt);
        } catch (SQLException ex) {
            ex.printStackTrace(); // Not printing exception to the user just to be safe
        }
        return ctx.err("There was an internal error.");
    }

    private static Result convertAttemptToResult(CommandContext ctx, Validation<String> attempt) {
        if (attempt.isValid()) {
            return ctx.reply(attempt.getValue());
        }
        String errorMsg = String.join("\n", attempt.getErrors());
        // TODO invalid setting value should return invalid args but returns warning, low priority since invis to user
        return ctx.warn(errorMsg);
    }
}
