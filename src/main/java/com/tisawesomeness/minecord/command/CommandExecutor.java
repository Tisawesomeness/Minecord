package com.tisawesomeness.minecord.command;

import com.tisawesomeness.minecord.Lang;
import com.tisawesomeness.minecord.config.serial.CommandConfig;
import com.tisawesomeness.minecord.config.serial.Config;
import com.tisawesomeness.minecord.config.serial.FlagConfig;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import com.google.common.base.Functions;
import com.google.common.collect.EnumMultiset;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multiset;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Runs commands and keep track of the output.
 */
public class CommandExecutor {

    private final CommandConfig cc;
    private final FlagConfig fc;
    private final Map<String, Cache<Long, Long>> cooldownMap;
    private final Map<Command, Multiset<Result>> results;

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
        if (fc.isDebugMode()) {
            builder.recordStats();
        }
        cooldownMap = cr.stream()
                .map(c -> c.getCooldownId(cc))
                .distinct()
                .collect(Collectors.toMap(
                        Functions.identity(),
                        s -> builder.build()
                ));
        results = cr.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        c -> EnumMultiset.create(Result.class)
                ));
    }

    /**
     * Runs the given command with this executor.
     * @param c The command
     * @param ctx The context of the command
     */
    public void run(Command c, CommandContext ctx) {
        if (!shouldSkipCooldown(ctx)) {
            long cooldown = c.getCooldown(cc);
            if (cooldown > 0) {
                long uid = ctx.e.getAuthor().getIdLong();
                long lastExecutedTime = getLastExecutedTime(c, uid);
                long msLeft = cooldown + lastExecutedTime - System.currentTimeMillis();
                if (msLeft > 0) {
                    ctx.warn(String.format("Wait `%.3f` more seconds.", (double) msLeft/1000));
                    return;
                }
            }
        }
        Result result = runCommand(c, ctx);
        results.get(c).add(result);
    }
    private long getLastExecutedTime(Command c, long uid) {
        Long let = cooldownMap.get(c.getCooldownId(cc)).get(uid, ignore -> 0L);
        return Objects.requireNonNull(let); // Null value never put into map
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
            // Not guarenteed to escape properly, but since users should never see exceptions, it's not necessary
            if (errorMessage.length() >= Message.MAX_CONTENT_LENGTH) {
                errorMessage = errorMessage.substring(0, Message.MAX_CONTENT_LENGTH - 3) + "```";
            }
        }
        ctx.reply(errorMessage);
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

    /**
     * Starts the cooldown for the given command and user
     * @param c The command
     * @param uid The Discord user ID of the author of the command
     */
    public void startCooldown(Command c, long uid) {
        cooldownMap.get(c.getCooldownId(cc)).put(uid, System.currentTimeMillis());
    }
    /**
     * Determines if the author of a command has permission to skip cooldowns
     * @param ctx The context of the command
     * @return True if cooldowns should not be processed
     */
    public boolean shouldSkipCooldown(CommandContext ctx) {
        return fc.isElevatedSkipCooldown() && ctx.isElevated;
    }

    public Multiset<Result> getResults(Command c) {
        return ImmutableMultiset.copyOf(results.get(c));
    }

    public CacheStats stats() {
        return cooldownMap.values().stream()
                .map(Cache::stats)
                .reduce(CacheStats::plus)
                .orElse(CacheStats.empty());
    }

}
