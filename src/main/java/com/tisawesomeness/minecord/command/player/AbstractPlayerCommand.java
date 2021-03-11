package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.command.Category;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.CommandContext;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public abstract class AbstractPlayerCommand extends Command {
    @Override
    public final Category getCategory() {
        return Category.PLAYER;
    }

    protected static void handleIOE(Throwable ex, CommandContext ctx, String errorMessage) {
        if (ex instanceof IOException) {
            log.error(errorMessage, ex);
            ctx.err(ctx.getLang().i18n("mc.external.mojang.error"));
            return;
        }
        throw new RuntimeException(ex);
    }

}
