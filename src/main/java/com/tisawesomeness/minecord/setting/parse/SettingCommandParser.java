package com.tisawesomeness.minecord.setting.parse;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.CommandContext;

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
    public Command.Result parse() {
        if (ctx.args.length == 0) {
            if (type == SettingCommandType.QUERY) {
                return displaySettings("Currently Active Settings", s -> s.getDisplay(ctx));
            }
            return ctx.showHelp();
        }
        return parseAdminArg();
    }

    private Command.Result parseAdminArg() {
        String[] args = ctx.args;
        if ("admin".equalsIgnoreCase(args[0])) {
            return parseAdminContextIfAble(args);
        }
        return new SettingContextParser(this, false).parse();
    }

    private Command.Result parseAdminContextIfAble(String[] args) {
        if (!ctx.isElevated) {
            return new Command.Result(Command.Outcome.WARNING,
                    ":warning: You do not have permission to use elevated commands.");
        }
        if (args.length == 1) {
            String msg = String.format(":warning: Incorrect arguments. See `%shelp settings admin`.", ctx.prefix);
            return new Command.Result(Command.Outcome.WARNING, msg);
        }
        currentArg++;
        return new SettingContextParser(this, true).parse();
    }
}
