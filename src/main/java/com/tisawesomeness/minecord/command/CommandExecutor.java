package com.tisawesomeness.minecord.command;

import com.tisawesomeness.minecord.Lang;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.MarkdownUtil;

/**
 * Runs commands and keep track of the output.
 */
public class CommandExecutor {
    /**
     * Runs the given command with this executor.
     * @param c The command
     * @param ctx The context of the command
     */
    public void run(Command c, CommandContext ctx) {
        runCommand(c, ctx);
    }

    private static Result runCommand(Command c, CommandContext ctx) {
        try {
            return c.run(ctx.args, ctx);
        } catch (Exception ex) {
            handle(ex, ctx);
        }
        return Result.EXCEPTION;
    }

    private static void handle(Exception ex, CommandContext ctx) {
        ex.printStackTrace();
        String unexpected = "There was an unexpected exception: " + MarkdownUtil.monospace(ex.toString());
        String errorMessage = Result.EXCEPTION.addEmote(unexpected, Lang.getDefault());
        if (ctx.config.getFlagConfig().isDebugMode()) {
            errorMessage += buildStackTrace(ex);
        }
        // Not guarenteed to escape properly, but since users should never see exceptions, it's not necessary
        if (errorMessage.length() >= Message.MAX_CONTENT_LENGTH) {
            errorMessage = errorMessage.substring(0, Message.MAX_CONTENT_LENGTH - 3) + "```";
        }
        ctx.log(errorMessage);
    }

    private static String buildStackTrace(Exception ex) {
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
