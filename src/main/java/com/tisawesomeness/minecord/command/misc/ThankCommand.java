package com.tisawesomeness.minecord.command.misc;

import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.command.IHiddenCommand;

import lombok.NonNull;

public class ThankCommand extends AbstractMiscCommand implements IHiddenCommand {
    public @NonNull String getId() {
        return "thank";
    }
    public void run(String[] args, CommandContext ctx) {
        ctx.reply(ctx.i18n("yw"));
    }
}
