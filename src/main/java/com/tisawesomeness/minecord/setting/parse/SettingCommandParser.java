package com.tisawesomeness.minecord.setting.parse;

import com.tisawesomeness.minecord.command.meta.CommandContext;

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
     */
    public void parse() {
        if (ctx.getArgs().length == 0) {
            if (type == SettingCommandType.QUERY) {
                ctx.triggerCooldown();
                displaySettings("Currently Active Settings", s -> s.getDisplay(ctx));
                return;
            }
            ctx.showHelp();
            return;
        }
        parseAdminArg();
    }

    private void parseAdminArg() {
        String[] args = ctx.getArgs();
        if ("admin".equalsIgnoreCase(args[0])) {
            parseAdminContextIfAble(args);
            return;
        }
        new SettingContextParser(this, false).parse();
    }

    private void parseAdminContextIfAble(String[] args) {
        if (!ctx.isElevated()) {
            ctx.notElevated("You do not have permission to use elevated commands.");
            return;
        }
        if (args.length == 1) {
            ctx.invalidArgs(String.format("Incorrect arguments. See `%shelp settings admin`.", ctx.getPrefix()));
            return;
        }
        currentArg++;
        new SettingContextParser(this, true).parse();
    }
}
