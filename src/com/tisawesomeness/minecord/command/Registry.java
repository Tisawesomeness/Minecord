package com.tisawesomeness.minecord.command;

import com.tisawesomeness.minecord.command.admin.*;
import com.tisawesomeness.minecord.command.core.*;
import com.tisawesomeness.minecord.command.discord.*;
import com.tisawesomeness.minecord.command.player.*;
import com.tisawesomeness.minecord.command.utility.*;
import com.tisawesomeness.minecord.mc.player.RenderType;
import com.tisawesomeness.minecord.util.type.Either;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.time.Duration;
import java.util.*;
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
                    new InfoCommand(),
                    new InfoCommandAdmin(),
                    new PingCommand(),
                    new PrefixCommand(),
                    new SettingsCommand(),
                    new InviteCommand(),
                    new VoteCommand(),
                    new CreditsCommand()
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
    private static final Map<String, Either<String, Command<?>>> commandMap = new HashMap<>();
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
        insert(ci.name, Either.right(c));
        if (c instanceof LegacyCommand) {
            for (String alias : ((LegacyCommand) c).getAliases()) {
                insert(alias, Either.right(c));
            }
        } else if (c instanceof SlashCommand) {
            for (String alias : ((SlashCommand) c).getLegacyAliases()) {
                insert(alias, Either.left(ci.name));
            }
        }
        stateMap.put(c, new CommandState());
    }
    private static void insert(String input, Either<String, Command<?>> entry) {
        Either<String, Command<?>> old = commandMap.put(input, entry);
        if (old != null) {
            String oldName = old.foldLeft(r -> r.getInfo().name);
            String newName = entry.foldLeft(r -> r.getInfo().name);
            System.err.println("Command " + oldName + " is being overwritten by " + newName + " for " + input);
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
     * @param name The part of the command after "&" and before a space. For example, "&server hypixel.net" becomes "server".
     * @return The command which should be executed, or empty if there is no command associated with the input.
     */
    public static Optional<Command<?>> getCommand(String name) {
        return getCommandMapping(name).map(mapping -> mapping.fold(l -> null, r -> r));
    }
    /**
     * Gets a command, given its name or alias.
     * @param name The part of the command after "&" and before a space. For example, "&server hypixel.net" becomes "server".
     * @return The command which should be executed, the name of the slash command the user should use instead,
     * or empty if there is no command associated with the input.
     */
    public static Optional<Either<String, Command<?>>> getCommandMapping(String name) {
        return Optional.ofNullable(commandMap.get(name));
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
        return Arrays.stream(modules)
                .map(Module::getCommands)
                .flatMap(Arrays::stream)
                .filter(c -> c instanceof SlashCommand)
                .map(c -> ((SlashCommand) c))
                .map(SlashCommand::getCommandSyntax)
                .collect(Collectors.toList());
    }

    private static class CommandState {
        private int uses = 0;
        private final Cache<Long, Long> lastUseTimesByUser = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(1))
                .build();
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

}
