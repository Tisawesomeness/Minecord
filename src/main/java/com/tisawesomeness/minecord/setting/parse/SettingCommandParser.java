package com.tisawesomeness.minecord.setting.parse;

import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.command.Result;

import lombok.Getter;
import lombok.NonNull;

/**
 * The entry handler for all setting commands.
 * <br>This determines whether to parse the context argument next.
 */
public class SettingCommandParser extends SettingCommandHandler {
    @Getter private final @NonNull CommandContext ctx;
    @Getter private final @NonNull SettingCommandType type;
    @Getter private int currentArg;

    public SettingCommandParser(CommandContext ctx, SettingCommandType type) {
        this.ctx = ctx;
        this.type = type;
    }

    /**
     * Displays effective settings for the current channel if there are no additional args.
     * <br>Otherwise, check for admin arg
     * @return The result of this command
     */
    public Result parse() {
        if (ctx.getArgs().length == 0) {
            if (type == SettingCommandType.QUERY) {
                ctx.triggerCooldown();
                return displaySettings("Currently Active Settings", s -> s.getDisplay(ctx));
            }
            return ctx.showHelp();
        }
        return parseAdminArg();
    }

    private Result parseAdminArg() {
        String[] args = ctx.getArgs();
        if ("admin".equalsIgnoreCase(args[0])) {
            return parseAdminContextIfAble(args);
        }
        return new SettingContextParser(this, false).parse();
    }

    private Result parseAdminContextIfAble(String[] args) {
        if (!ctx.isElevated()) {
            return ctx.notElevated("You do not have permission to use elevated commands.");
        }
        if (args.length == 1) {
            return ctx.invalidArgs(String.format("Incorrect arguments. See `%shelp settings admin`.", ctx.getPrefix()));
        }
        currentArg++;
        return new SettingContextParser(this, true).parse();
    }
}
