package com.tisawesomeness.minecord.command;

import com.tisawesomeness.minecord.Lang;
import com.tisawesomeness.minecord.config.serial.CommandConfig;
import com.tisawesomeness.minecord.config.serial.Config;
import com.tisawesomeness.minecord.config.serial.FlagConfig;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Runs commands and keep track of the output.
 */
public class CommandExecutor {

    private final Map<Command, Cache<Long, Long>> cooldownMap;
    private final CommandConfig cc;
    private final FlagConfig fc;

    /**
     * Creates a new command executor, initializing a cache for each command.
     * @param cr The registry containing all commands
     * @param config The configuration options
     */
    public CommandExecutor(@NonNull CommandRegistry cr, @NonNull Config config) {
        cc = config.getCommandConfig();
        fc = config.getFlagConfig();
        Caffeine<Object, Object> builder = Caffeine.newBuilder()
                .expireAfterWrite(30L, TimeUnit.SECONDS)
                .maximumSize(100L);
        Map<Command, Cache<Long, Long>> temp = new HashMap<>();
        for (Command c : cr) {
            temp.put(c, builder.build());
        }
        cooldownMap = Collections.unmodifiableMap(temp);
    }

    /**
     * Runs the given command with this executor.
     * @param c The command
     * @param ctx The context of the command
     */
    public void run(Command c, CommandContext ctx) {
        long uid = ctx.e.getAuthor().getIdLong();
        if (!skipCooldown(ctx)) {
            long cooldown = c.getCooldown(cc);
            if (cooldown > 0) {
                long lastExecutedTime = cooldownMap.get(c).get(uid, ignore -> 0L); // Guarenteed non-null
                long msLeft = cooldown + lastExecutedTime - System.currentTimeMillis();
                if (msLeft > 0) {
                    ctx.warn(String.format("Wait %.3f more seconds.", (double) msLeft/1000));
                    return;
                }
            }
        }
        addCooldown(c, uid);
        runCommand(c, ctx);
    }
    private boolean skipCooldown(CommandContext ctx) {
        return fc.isElevatedSkipCooldown() && ctx.isElevated;
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

    private void addCooldown(Command c, long uid) {
        cooldownMap.get(c).put(uid, System.currentTimeMillis());
    }
}
