package com.tisawesomeness.minecord.command;

import com.tisawesomeness.minecord.Lang;
import com.tisawesomeness.minecord.config.serial.CommandConfig;
import com.tisawesomeness.minecord.config.serial.Config;
import com.tisawesomeness.minecord.config.serial.FlagConfig;
import com.tisawesomeness.minecord.database.dao.CommandStats;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import com.google.common.base.Functions;
import com.google.common.collect.ConcurrentHashMultiset;
import com.google.common.collect.EnumMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import org.jetbrains.annotations.TestOnly;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Runs commands and keep track of the output.
 */
public class CommandExecutor {

    private final CommandConfig cc;
    private final FlagConfig fc;
    @Getter private final CommandStats commandStats;
    private final Map<String, Cache<Long, Long>> cooldownMap;
    private final Map<Command, Multiset<Result>> results;
    private final Multiset<Command> commandUses = ConcurrentHashMultiset.create();
    private final Multiset<String> unpushedUses = ConcurrentHashMultiset.create();

    /**
     * Creates a new command executor, initializing a cache for each command.
     * @param cr The registry containing all commands
     * @param config The configuration options
     */
    public CommandExecutor(@NonNull CommandRegistry cr, @NonNull CommandStats commandStats, @NonNull Config config) {
        cc = config.getCommandConfig();
        fc = config.getFlagConfig();
        this.commandStats = commandStats;
        Caffeine<Object, Object> builder = Caffeine.newBuilder()
                .expireAfterWrite(30L, TimeUnit.SECONDS)
                .maximumSize(100L);
        if (fc.isDebugMode()) {
            builder.recordStats();
        }
        cooldownMap = cr.stream()
                .filter(c -> !(c instanceof IElevatedCommand))
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
        runCommand(c, ctx);
        commandUses.add(c);
        unpushedUses.add(c.getId());
    }
    /**
     * Directly runs a command without keeping track of it.
     * @param c The command
     * @param ctx The context of the command
     */
    @TestOnly
    public void runCommand(Command c, CommandContext ctx) {
        processGuildOnly(c, ctx);
    }

    private void processGuildOnly(Command c, CommandContext ctx) {
        if (c instanceof IGuildOnlyCommand && !ctx.isFromGuild()) {
            ctx.guildOnly("This command is not available in DMs.");
            return;
        }
        processElevation(c, ctx);
    }
    private void processElevation(Command c, CommandContext ctx) {
        if (c instanceof IElevatedCommand && !ctx.isElevated()) {
            ctx.notElevated("You must be elevated to use that command!");
            return;
        }
        processPermissions(c, ctx);
    }

    private void processPermissions(Command c, CommandContext ctx) {
        if (ctx.isFromGuild()) {
            processBotPermissions(c, ctx);
            return;
        }
        processCooldown(c, ctx);
    }
    private void processBotPermissions(Command c, CommandContext ctx) {
        if (!ctx.botHasPermission(Permission.MESSAGE_EMBED_LINKS)) {
            ctx.noBotPermissions("I need Embed Links permissions to use commands!");
            return;
        }
        EnumSet<Permission> rbp = c.getBotPermissions();
        if (!ctx.botHasPermission(rbp)) {
            Member sm = ctx.getE().getGuild().getSelfMember();
            TextChannel tc = ctx.getE().getTextChannel();
            String missingPermissions = getMissingPermissionString(sm, tc, rbp);
            String errMsg = String.format("I am missing the %s permissions.", missingPermissions);
            ctx.noBotPermissions(errMsg);
            return;
        }
        processUserPermissions(c, ctx);
    }
    private void processUserPermissions(Command c, CommandContext ctx) {
        if (!ctx.isElevated()) {
            EnumSet<Permission> rup = c.getUserPermissions();
            if (!ctx.userHasPermission(rup)) {
                Member mem = Objects.requireNonNull(ctx.getE().getMember());
                TextChannel tc = ctx.getE().getTextChannel();
                String missingPermissions = getMissingPermissionString(mem, tc, rup);
                String errMsg = String.format("You are missing the %s permissions.", missingPermissions);
                ctx.noUserPermissions(errMsg);
                return;
            }
        }
        processCooldown(c, ctx);
    }
    // Mutates permissions collection! Only use when done
    private static String getMissingPermissionString(Member m, TextChannel tc, Collection<Permission> permissions) {
        permissions.removeAll(m.getPermissions(tc));
        return permissions.stream()
                .map(Permission::getName)
                .collect(Collectors.joining(", "));
    }

    private void processCooldown(Command c, CommandContext ctx) {
        if (!shouldSkipCooldown(ctx)) {
            long cooldown = c.getCooldown(cc);
            if (cooldown > 0) {
                long uid = ctx.getUserId();
                long lastExecutedTime = getLastExecutedTime(c, uid);
                long msLeft = cooldown + lastExecutedTime - System.currentTimeMillis();
                if (msLeft > 0) {
                    String cooldownMsg = String.format("Wait `%.3f` more seconds.", (double) msLeft/1000);
                    ctx.sendResult(Result.COOLDOWN, cooldownMsg);
                    return;
                }
            }
        }
        tryToRun(c, ctx);
    }
    private long getLastExecutedTime(Command c, long uid) {
        Long let = cooldownMap.get(c.getCooldownId(cc)).get(uid, ignore -> 0L);
        return Objects.requireNonNull(let); // Null value never put into map
    }

    private void tryToRun(Command c, CommandContext ctx) {
        try {
            c.run(ctx.getArgs(), ctx);
        } catch (Exception ex) {
            handle(ex, ctx);
            pushResult(c, Result.EXCEPTION);
        }
    }
    private static void handle(Exception ex, CommandContext ctx) {
        ex.printStackTrace();
        String unexpected = "There was an unexpected exception: " + MarkdownUtil.monospace(ex.toString());
        String errorMessage = Result.EXCEPTION.addEmote(unexpected, Lang.getDefault());
        if (ctx.getConfig().getFlagConfig().isDebugMode()) {
            errorMessage += buildStackTrace(ex);
            // Not guaranteed to escape properly, but since users should never see exceptions, it's not necessary
            if (errorMessage.length() >= Message.MAX_CONTENT_LENGTH) {
                errorMessage = errorMessage.substring(0, Message.MAX_CONTENT_LENGTH - 3) + "```";
            }
        }
        ctx.sendMessage(errorMessage);
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
     * Starts the cooldown for the given command and user.
     * @param c The command
     * @param uid The Discord user ID of the author of the command
     */
    public void startCooldown(Command c, long uid) {
        cooldownMap.get(c.getCooldownId(cc)).put(uid, System.currentTimeMillis());
    }
    /**
     * Determines if the author of a command has permission to skip cooldowns.
     * @param ctx The context of the command
     * @return True if cooldowns should not be processed
     */
    public boolean shouldSkipCooldown(CommandContext ctx) {
        return fc.isElevatedSkipCooldown() && ctx.isElevated();
    }

    /**
     * Gets the number of times a command has been used.
     * @param c The command
     * @return A non-negative integer
     */
    public int getUses(@NonNull Command c) {
        return commandUses.count(c);
    }
    /**
     * Gets the number of times any command in a module has been used.
     * @param m The module
     * @return A non-negative integer
     */
    public int getUses(@NonNull Module m) {
        return commandUses.stream()
                .filter(cm -> cm.getModule() == Module.DISCORD)
                .mapToInt(this::getUses)
                .sum();
    }
    /**
     * Gets the number of times any command has been used.
     * @return A nongative integer
     */
    public int getTotalUses() {
        return commandUses.size();
    }
    /**
     * Gets the count of all results for a command. This may be slightly inaccurate as a command that is still running
     * but hasn't reported a result yet will be counted as a success.
     * @param c The command
     * @return A Multiset where the size is equal to the number of command results (NOT executions)
     */
    public Multiset<Result> getResults(@NonNull Command c) {
        return Multisets.unmodifiableMultiset(results.get(c));
    }

    /**
     * Records the result of a command. A command may have multiple result if it replies multiple times.
     * @param c The command
     * @param result The result to record
     */
    public void pushResult(@NonNull Command c, Result result) {
        Multiset<Result> resultsMultiset = results.get(c);
        if (resultsMultiset != null) {
            resultsMultiset.add(result);
        }
    }

    /**
     * Records all command uses since the last push to the database.
     */
    public void pushUses() {
        try {
            commandStats.pushCommandUses(unpushedUses);
            unpushedUses.clear();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Gets the combined stats of all cooldown caches.
     * @return A possibly-empty CacheStats
     */
    public CacheStats stats() {
        return cooldownMap.values().stream()
                .map(Cache::stats)
                .reduce(CacheStats::plus)
                .orElse(CacheStats.empty());
    }
    /**
     * Gets the stats of a specific cooldown cache.
     * @param pool The cache pool
     * @return An empty optional if the pool does not exist, and the stats itself may be empty
     */
    public Optional<CacheStats> stats(String pool) {
        return Optional.ofNullable(cooldownMap.get(pool)).map(Cache::stats);
    }

    /**
     * Builds a string showing each pool and it's estimated cache size
     * @return A debug string
     */
    public String debugEstimatedSizes() {
        return cooldownMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(en -> String.format("**%s**: `%d`", en.getKey(), en.getValue().estimatedSize()))
                .collect(Collectors.joining("\n"));
    }

}
