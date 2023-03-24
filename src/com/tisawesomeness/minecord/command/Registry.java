package com.tisawesomeness.minecord.command;

import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.command.admin.*;
import com.tisawesomeness.minecord.command.core.*;
import com.tisawesomeness.minecord.command.discord.*;
import com.tisawesomeness.minecord.command.player.*;
import com.tisawesomeness.minecord.command.utility.*;
import com.tisawesomeness.minecord.mc.player.RenderType;
import com.tisawesomeness.minecord.util.type.Either;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The list of all commands the bot knows.
 */
public class Registry {

    private static final String adminHelp = "**These commands require elevation to use.**\n\n" +
            "`{&}infoadmin` - Displays bot info, including used memory and boot time.\n" +
            "`{&}settings <guild id> admin [setting] [value]` - Change the bot's settings for another guild.\n" +
            "`{&}permsadmin <channel id>` - Test the bot's permissions in any channel.\n" +
            "`{&}useradmin <user id> [mutual]` - Show info, ban status, and elevation for a user outside of the current guild. Include \"mutual\" to show mutual guilds.\n" +
            "`{&}guildadmin <guild id>` - Show info and ban status for another guild.\n" +
            "`{&}roleadmin <role id>` - Shows the info of any role.\n";

    public static final Module[] modules = {
            new Module("Core",
                    new HelpCommand(),
                    new HelpCommandLegacy(),
                    new InfoCommand(),
                    new InfoCommandLegacy(),
                    new InfoCommandAdmin(),
                    new PingCommand(),
                    new PingCommandLegacy(),
                    new PrefixCommand(),
                    new SettingsCommand(),
                    new InviteCommand(),
                    new VoteCommand(),
                    new CreditsCommand(),
                    new CreditsCommandLegacy()
            ),
            new Module("Player",
                    new ProfileCommand(),
                    new HistoryCommand(),
                    new UuidCommand(),
                    new SkinCommand(),
                    new CapeCommand(),
                    new RenderCommand(RenderType.AVATAR),
                    new RenderCommand(RenderType.HEAD),
                    new RenderCommand(RenderType.BODY),
                    new AnsiCommand()
            ),
            new Module("Utility",
                    new StatusCommand(),
                    new ServerCommand(),
                    new ItemCommand(),
                    new RecipeCommand(),
                    new IngredientCommand(),
                    new CodesCommand(),
                    new ColorCommand(),
                    new SeedCommand(),
                    new ShadowCommand(),
                    new Sha1Command()
            ),
            new Module("Discord",
                    new UserCommand(),
                    new GuildCommand(),
                    new GuildCommandAdmin(),
                    new RoleCommand(),
                    new RoleCommandAdmin(),
                    new RolesCommand(),
                    new IdCommand(),
                    new PurgeCommand(),
                    new PermsCommand(),
                    new PermsCommandAdmin()
            ),
            new Module("Admin", true, adminHelp,
                    new SayCommand(),
                    new MsgCommand(),
                    new NameCommand(),
                    new UsageCommand(),
                    new DebugCommand(),
                    new PromoteCommand(),
                    new DemoteCommand(),
                    new BanCommand(),
                    new ReloadCommand(),
                    new ShutdownCommand(),
                    new DeployCommand(),
                    new EvalCommand(),
                    new TestCommand()
            )
    };
    // Map from name or alias user enters to either command or name of slash command to migrate to
    private static final Map<String, SlashCommand> slashCommandMap = new HashMap<>();
    private static final Map<String, Either<String, LegacyCommand>> legacyCommandMap = new HashMap<>();
    private static final Map<Command<?>, CommandState> stateMap = new HashMap<>();

    /**
     * Adds every module to the registry and maps the possible aliases to the command to execute.
     * Must be executed before getCommand() can be called.
     */
    public static void init() {
        for (Module m : modules) {
            for (Command<?> c : m.getCommands()) {
                initCommand(c);
            }
        }
    }
    private static void initCommand(Command<?> c) {
        Command.CommandInfo ci = c.getInfo();
        if (c instanceof LegacyCommand) {
            LegacyCommand lc = (LegacyCommand) c;
            insert(ci.name, lc);
            for (String alias : lc.getAliases()) {
                insert(alias, lc);
            }
        } else if (c instanceof SlashCommand) {
            SlashCommand sc = (SlashCommand) c;
            insert(ci.name, sc);
            insert(ci.name, ci.name);
            for (String alias : sc.getLegacyAliases()) {
                insert(alias, ci.name);
            }
        } else {
            throw new AssertionError("Command " + ci.name + " is not a legacy or slash command.");
        }
        stateMap.put(c, new CommandState());
    }
    private static void insert(String input, SlashCommand cmd) {
        SlashCommand old = slashCommandMap.put(input, cmd);
        if (old != null) {
            String oldName = old.getInfo().name;
            String newName = cmd.getInfo().name;
            System.err.println("Slash command " + oldName + " is being overwritten by " + newName + " for " + input);
        }
    }
    private static void insert(String input, LegacyCommand cmd) {
        insert(input, Either.right(cmd));
    }
    private static void insert(String input, String migrateTo) {
        insert(input, Either.left(migrateTo));
    }
    private static void insert(String input, Either<String, LegacyCommand> entry) {
        Either<String, LegacyCommand> old = legacyCommandMap.get(input);

        // legacy command registered for the first time
        if (old == null) {
            legacyCommandMap.put(input, entry);

        // legacy command is being replaced with another legacy command
        } else if (old.isRight() && entry.isRight()) {
            legacyCommandMap.put(input, entry);
            String oldName = old.getRight().getInfo().name;
            String newName = entry.getRight().getInfo().name;
            System.err.println("Command " + oldName + " is being overwritten by " + newName + " for " + input);

        // slash command redirection is being replaced with another redirection
        } else if (old.isLeft() && entry.isLeft()) {
            legacyCommandMap.put(input, entry);
            String oldName = old.getLeft();
            String newName = entry.getLeft();
            System.err.println("Slash command redirection " + oldName + " is being overwritten by " + newName + " for " + input);

        // slash command redirection is being replaced with a legacy command, legacy command takes priority
        } else if (old.isLeft() && entry.isRight()) {
            legacyCommandMap.put(input, entry);
        }

    }

    /**
     * Gets a module, given its name
     * @param name Case-insensitive name of the desired module
     * @return The module, or null if not found.
     */
    public static Module getModule(String name) {
        for (Module m : modules) {
            if (m.getName().equalsIgnoreCase(name)) {
                return m;
            }
        }
        return null;
    }

    /**
     * Gets a command, given its name or alias.
     * @param name The part of the command after "/" and before a space. For example, "/server hypixel.net" becomes "server".
     * @return The command which should be executed, or empty if there is no command associated with the input.
     */
    public static Optional<Command<?>> getCommand(String name) {
        Optional<SlashCommand> sc = getSlashCommand(name);
        // thank you java type system
        if (sc.isPresent()) {
            return Optional.of(sc.get());
        }
        return getLegacyCommand(name).map(mapping -> mapping.fold(l -> null, r -> r));
    }
    /**
     * Gets a slash command, given its name or alias.
     * @param name The part of the command after "/" and before a space. For example, "/server hypixel.net" becomes "server".
     * @return The command which should be executed, or empty if there is no command associated with the input.
     */
    public static Optional<SlashCommand> getSlashCommand(String name) {
        return Optional.ofNullable(slashCommandMap.get(name));
    }
    /**
     * Gets a legacy command, given its name or alias.
     * @param name The part of the command after "/" and before a space. For example, "/server hypixel.net" becomes "server".
     * @return The command which should be executed (Right), the name of the slash command the user should use instead (Left),
     * or empty if there is no command associated with the input (Empty).
     */
    public static Optional<Either<String, LegacyCommand>> getLegacyCommand(String name) {
        return Optional.ofNullable(legacyCommandMap.get(name));
    }

    /**
     * Gets the module a command belongs to
     * @param cmdName Case-sensitive name of the command
     * @return The module, or null if not found. This should never return null unless the command name is incorrect or a command was registered without a module.
     */
    public static String findModuleName(String cmdName) {
        for (Module m : modules) {
            for (Command<?> c : m.getCommands()) {
                if (c.getInfo().name.equals(cmdName)) {
                    return m.getName();
                }
            }
        }
        return null;
    }

    public static List<CommandData> getSlashCommands() {
        return slashCommandMap.values().stream()
                .map(SlashCommand::getCommandSyntax)
                .collect(Collectors.toList());
    }

    private static class CommandState {
        private int uses = 0;
        private final Cache<Long, Long> lastUseTimesByUser;

        public CommandState() {
            Caffeine<Object, Object> builder = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(1));
            if (Config.getRecordCacheStats()) {
                builder.recordStats();
            }
            lastUseTimesByUser = builder.build();
        }
    }

    public static void useCommand(Command<?> cmd, User user) {
        CommandState state = stateMap.get(cmd);
        state.uses++;
        state.lastUseTimesByUser.put(user.getIdLong(), System.currentTimeMillis());
    }
    public static long getCooldownLeft(Command<?> cmd, User user) {
        long lastUse = stateMap.get(cmd).lastUseTimesByUser.get(user.getIdLong(), k -> 0L);
        if (lastUse == 0L) {
            return 0L;
        }
        return cmd.getInfo().cooldown + lastUse - System.currentTimeMillis();
    }
    public static int getUses(Command<?> cmd) {
        return stateMap.get(cmd).uses;
    }

    public static CacheStats cooldownStats() {
        return stateMap.values().stream()
                .map(state -> state.lastUseTimesByUser)
                .map(Cache::stats)
                .reduce(CacheStats::plus)
                .orElse(CacheStats.empty());
    }
    public static Optional<CacheStats> cooldownStats(String command) {
        return getCommand(command)
                .map(stateMap::get)
                .map(state -> state.lastUseTimesByUser)
                .map(Cache::stats);
    }

}
