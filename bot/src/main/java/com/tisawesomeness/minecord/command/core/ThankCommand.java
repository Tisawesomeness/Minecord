package com.tisawesomeness.minecord.command.core;

import com.tisawesomeness.minecord.command.meta.CommandContext;
import com.tisawesomeness.minecord.command.meta.IHiddenCommand;

import lombok.NonNull;

public class ThankCommand extends AbstractCoreCommand implements IHiddenCommand {
    public @NonNull String getId() {
        return "thank";
    }
    public void run(String[] args, CommandContext ctx) {
        ctx.reply(ctx.i18n("yw"));
    }
}
