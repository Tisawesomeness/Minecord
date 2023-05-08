package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.command.SlashCommand;
import com.tisawesomeness.minecord.util.type.FutureCallback;

import lombok.SneakyThrows;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractPlayerCommand extends SlashCommand {

    protected static void handleMojangIOE(Throwable ex, SlashCommandInteractionEvent e, String errorMessage) {
        handleIOE(ex, e, errorMessage, true);
    }
    protected static void handleIOE(Throwable ex, SlashCommandInteractionEvent e, String errorMessage) {
        handleIOE(ex, e, errorMessage, false);
    }
    @SneakyThrows
    private static void handleIOE(Throwable ex, SlashCommandInteractionEvent e, String errorMessage, boolean sendError) {
        Throwable cause = ex.getCause();
        if (cause instanceof IOException) {
            System.err.println(errorMessage);
            if (sendError) {
                e.getHook().sendMessage(":x: There was an error contacting the Mojang API.").setEphemeral(true).queue();
            }
            return;
        }
        throw cause;
    }

    protected <T> FutureCallback.Builder<T> newCallbackBuilder(CompletableFuture<T> future, SlashCommandInteractionEvent e) {
        return FutureCallback.builder(future).onUncaught(ex -> handleUncaught(ex, e));
    }

    private void handleUncaught(Throwable ex, SlashCommandInteractionEvent e) {
        try {
            System.err.println("Uncaught exception in command " + debugRunSlashCommand(e));
            handleException(ex, e);
        } catch (Exception ex2) {
            System.err.println("Somehow, there was an exception processing an uncaught exception");
            ex2.printStackTrace();
        }
    }

}
