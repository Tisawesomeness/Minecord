package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.command.meta.Category;
import com.tisawesomeness.minecord.command.meta.Command;
import com.tisawesomeness.minecord.command.meta.CommandContext;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.CompletionException;

@Slf4j
public abstract class AbstractPlayerCommand extends Command {

    @Override
    public final Category getCategory() {
        return Category.PLAYER;
    }

    @SneakyThrows
    protected static void handleIOE(Throwable ex, CommandContext ctx, String errorMessage) {
        Throwable cause = ex instanceof CompletionException ? ex.getCause() : ex;
        if (cause instanceof IOException) {
            log.error(errorMessage, cause);
            ctx.err(ctx.getLang().i18n("mc.external.mojang.error"));
            return;
        }
        throw cause;
    }

}
