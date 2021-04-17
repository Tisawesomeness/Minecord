package com.tisawesomeness.minecord.command;

import com.tisawesomeness.minecord.command.meta.*;
import com.tisawesomeness.minecord.config.serial.CommandConfig;
import com.tisawesomeness.minecord.config.serial.Config;
import com.tisawesomeness.minecord.database.dao.CommandStats;
import com.tisawesomeness.minecord.util.type.EnumMultiSet;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.MultiSetUtils;
import org.apache.commons.collections4.multiset.HashMultiSet;
import org.jetbrains.annotations.TestOnly;

import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Runs commands and keep track of the output.
 */
@Slf4j
public class CommandExecutor {

    private static final double COOLDOWN_CACHE_TOLERANCE = 1.05;

    private final Config config;
    @Getter private final CommandStats commandStats;
    private final CommandVerifier commandVerifier;
    private final Map<String, Cache<Long, Long>> cooldownMap;
    private final Map<Command, MultiSet<Result>> results;
    private final MultiSet<Command> commandUses = MultiSetUtils.synchronizedMultiSet(new HashMultiSet<>());
    private final MultiSet<String> unpushedUses = MultiSetUtils.synchronizedMultiSet(new HashMultiSet<>());

    /**
     * Creates a new command executor, initializing a cache for each command.
     * @param cr The registry containing all commands
     * @param config The configuration options
     */
    public CommandExecutor(@NonNull CommandRegistry cr, @NonNull CommandStats commandStats, @NonNull Config config) {
        this.config = config;
        this.commandStats = commandStats;
        commandVerifier = new CommandVerifier(this);
        cooldownMap = buildCooldownMap(cr);
        results = cr.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        c -> new EnumMultiSet<>(Result.class)
                ));
    }
    private Map<String, Cache<Long, Long>> buildCooldownMap(CommandRegistry cr) {
        CommandConfig cc = config.getCommandConfig();
        return cr.stream()
                .filter(c -> !(c instanceof IElevatedCommand))
                .map(c -> new CooldownHolder(c.getCooldownId(cc), c.getCooldown(cc)))
                .distinct()
                .collect(Collectors.toMap(
                        CooldownHolder::getId,
                        this::buildCooldownCache
                ));
    }
    private Cache<Long, Long> buildCooldownCache(CooldownHolder ch) {
        int cooldown = (int) (ch.getCooldown() * COOLDOWN_CACHE_TOLERANCE);
        Caffeine<Object, Object> builder = Caffeine.newBuilder()
                .expireAfterWrite(cooldown, TimeUnit.MILLISECONDS);
        if (config.getFlagConfig().isDebugMode()) {
            builder.recordStats();
        }
        return builder.build();
    }

    /**
     * Runs the given command with this executor.
     * @param ctx The context of the command
     */
    public void run(CommandContext ctx) {
        Command c = ctx.getCmd();
        runCommand(ctx);
        commandUses.add(c);
        unpushedUses.add(c.getId());
    }
    /**
     * Directly runs a command without keeping track of it.
     * @param ctx The context of the command
     */
    @TestOnly
    public void runCommand(CommandContext ctx) {
        if (commandVerifier.shouldRun(ctx)) {
            tryToRun(ctx);
        }
    }

    private void tryToRun(CommandContext ctx) {
        Command c = ctx.getCmd();
        try {
            c.run(ctx.getArgs(), ctx);
        } catch (Exception ex) {
            ctx.handleException(ex);
            pushResult(c, Result.EXCEPTION);
        }
    }

    /**
     * Gets the effective cooldown of a specific command for this executor.
     * @param c The command
     * @return The cooldown in milliseconds
     */
    public int getCooldown(Command c) {
        return c.getCooldown(config.getCommandConfig());
    }
    /**
     * Gets the Unix timestamp of when the user last executed the command.
     * @param c The command
     * @param uid The user ID
     * @return The last executed time, or 0 if the user is not in the cooldown cache
     */
    public long getLastExecutedTime(Command c, long uid) {
        Long let = cooldownMap.get(c.getCooldownId(config.getCommandConfig())).get(uid, ignore -> 0L);
        return Objects.requireNonNull(let); // Null value never put into map
    }
    /**
     * Starts the cooldown for the given command and user.
     * @param c The command
     * @param uid The Discord user ID of the author of the command
     */
    public void startCooldown(Command c, long uid) {
        cooldownMap.get(c.getCooldownId(config.getCommandConfig())).put(uid, System.currentTimeMillis());
    }
    /**
     * Determines if the author of a command has permission to skip cooldowns.
     * @param ctx The context of the command
     * @return True if cooldowns should not be processed
     */
    public boolean shouldSkipCooldown(CommandContext ctx) {
        return config.getFlagConfig().isElevatedSkipCooldown() && ctx.isElevated();
    }

    /**
     * Gets the number of times a command has been used.
     * @param c The command
     * @return A non-negative integer
     */
    public int getUses(@NonNull Command c) {
        return commandUses.getCount(c);
    }
    /**
     * Gets the number of times any command in a category has been used.
     * @param cat The category
     * @return A non-negative integer
     */
    public int getUses(@NonNull Category cat) {
        synchronized (commandUses) {
            return commandUses.entrySet().stream()
                    .filter(en -> en.getElement().getCategory() == cat)
                    .mapToInt(MultiSet.Entry::getCount)
                    .sum();
        }
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
    public MultiSet<Result> getResults(@NonNull Command c) {
        return MultiSetUtils.unmodifiableMultiSet(results.get(c));
    }

    /**
     * Records the result of a command. A command may have multiple result if it replies multiple times.
     * @param c The command
     * @param result The result to record
     */
    public void pushResult(@NonNull Command c, Result result) {
        MultiSet<Result> resultsMultiset = results.get(c);
        if (resultsMultiset != null) {
            resultsMultiset.add(result);
        }
    }

    /**
     * Records all command uses since the last push to the database.
     */
    public void pushUses() {
        MultiSet<String> copy;
        synchronized (unpushedUses) {
            copy = new HashMultiSet<>(unpushedUses);
            unpushedUses.clear();
        }
        try {
            commandStats.pushCommandUses(copy);
        } catch (SQLException ex) {
            log.error("Exception pushing command uses", ex);
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

    @Value
    private static class CooldownHolder {
        String id;
        @EqualsAndHashCode.Exclude
        int cooldown;
    }

}
