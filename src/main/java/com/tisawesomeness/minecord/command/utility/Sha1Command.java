package com.tisawesomeness.minecord.command.utility;

import com.tisawesomeness.minecord.command.meta.CommandContext;
import com.tisawesomeness.minecord.command.meta.IMultiLineCommand;
import com.tisawesomeness.minecord.util.Mth;

import lombok.NonNull;

public class Sha1Command extends AbstractUtilityCommand implements IMultiLineCommand {

    public @NonNull String getId() {
        return "sha1";
    }

    public void run(String[] args, CommandContext ctx) {
        if (args.length == 0) {
            ctx.showHelp();
            return;
        }
        ctx.triggerCooldown();
        ctx.reply(Mth.sha1(ctx.joinArgs()));
    }

}