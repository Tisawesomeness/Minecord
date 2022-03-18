package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.util.type.FutureCallback;

import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractPlayerCommand extends Command {

    @SneakyThrows
    protected static void handleIOE(Throwable ex, MessageReceivedEvent e, String errorMessage) {
        Throwable cause = ex.getCause();
        if (cause instanceof IOException) {
            System.err.println(errorMessage);
            e.getChannel().sendMessage(":x: There was an error contacting the Mojang API.").queue();
            return;
        }
        throw cause;
    }

    protected static <T> FutureCallback.Builder<T> newCallbackBuilder(CompletableFuture<T> future, MessageReceivedEvent e) {
        return FutureCallback.builder(future).onUncaught(ex -> handleUncaught(ex, e));
    }

    private static void handleUncaught(Throwable ex, MessageReceivedEvent e) {
        try {
            System.err.println("Uncaught exception in command " + e.getMessage().getContentRaw());
            String unexpected = "There was an unexpected exception: " + MarkdownUtil.monospace(ex.toString());
            String errorMessage = ":boom: " + unexpected;
            if (Config.getDebugMode()) {
                errorMessage += "\n" + buildStackTrace(ex);
                // Not guaranteed to escape properly, but since users should never see exceptions, it's not necessary
                if (errorMessage.length() >= Message.MAX_CONTENT_LENGTH) {
                    errorMessage = errorMessage.substring(0, Message.MAX_CONTENT_LENGTH - 3) + "```";
                }
            }
            e.getChannel().sendMessage(errorMessage).queue();
            System.err.println(errorMessage);
        } catch (Exception ex2) {
            System.err.println("Somehow, there was an exception processing an uncaught exception");
            ex2.printStackTrace();
        }
    }
    private static String buildStackTrace(Throwable ex) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement ste : ex.getStackTrace()) {
            sb.append(ste);
            String className = ste.getClassName();
            if (className.contains("net.dv8tion") || className.contains("com.neovisionaries")) {
                sb.append("...");
                break;
            }
            sb.append("\n");
        }
        if (sb.charAt(sb.length() - 1) == '\n') {
            sb.setLength(sb.length() - 1);
        }
        return MarkdownUtil.codeblock(sb.toString());
    }

}
