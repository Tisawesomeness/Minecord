package com.tisawesomeness.minecord.command;

import com.tisawesomeness.minecord.Lang;
import com.tisawesomeness.minecord.command.admin.BanCommand;
import com.tisawesomeness.minecord.command.admin.DebugCommand;
import com.tisawesomeness.minecord.command.admin.DemoteCommand;
import com.tisawesomeness.minecord.command.admin.EvalCommand;
import com.tisawesomeness.minecord.command.admin.MsgCommand;
import com.tisawesomeness.minecord.command.admin.NameCommand;
import com.tisawesomeness.minecord.command.admin.PromoteCommand;
import com.tisawesomeness.minecord.command.admin.ReloadCommand;
import com.tisawesomeness.minecord.command.admin.SayCommand;
import com.tisawesomeness.minecord.command.admin.ShutdownCommand;
import com.tisawesomeness.minecord.command.admin.TestCommand;
import com.tisawesomeness.minecord.command.admin.UsageCommand;
import com.tisawesomeness.minecord.command.config.LangCommand;
import com.tisawesomeness.minecord.command.config.PrefixCommand;
import com.tisawesomeness.minecord.command.config.ResetCommand;
import com.tisawesomeness.minecord.command.config.SetCommand;
import com.tisawesomeness.minecord.command.config.SettingsCommand;
import com.tisawesomeness.minecord.command.discord.GuildCommand;
import com.tisawesomeness.minecord.command.discord.PermsCommand;
import com.tisawesomeness.minecord.command.discord.PurgeCommand;
import com.tisawesomeness.minecord.command.discord.RoleCommand;
import com.tisawesomeness.minecord.command.discord.RolesCommand;
import com.tisawesomeness.minecord.command.discord.UserCommand;
import com.tisawesomeness.minecord.command.misc.CreditsCommand;
import com.tisawesomeness.minecord.command.misc.HelpCommand;
import com.tisawesomeness.minecord.command.misc.InfoCommand;
import com.tisawesomeness.minecord.command.misc.InviteCommand;
import com.tisawesomeness.minecord.command.misc.PingCommand;
import com.tisawesomeness.minecord.command.misc.VoteCommand;
import com.tisawesomeness.minecord.command.player.AvatarCommand;
import com.tisawesomeness.minecord.command.player.BodyCommand;
import com.tisawesomeness.minecord.command.player.CapeCommand;
import com.tisawesomeness.minecord.command.player.HeadCommand;
import com.tisawesomeness.minecord.command.player.HistoryCommand;
import com.tisawesomeness.minecord.command.player.ProfileCommand;
import com.tisawesomeness.minecord.command.player.SkinCommand;
import com.tisawesomeness.minecord.command.player.UuidCommand;
import com.tisawesomeness.minecord.command.utility.CodesCommand;
import com.tisawesomeness.minecord.command.utility.ColorCommand;
import com.tisawesomeness.minecord.command.utility.ColorShortcut;
import com.tisawesomeness.minecord.command.utility.IngredientCommand;
import com.tisawesomeness.minecord.command.utility.ItemCommand;
import com.tisawesomeness.minecord.command.utility.RecipeCommand;
import com.tisawesomeness.minecord.command.utility.SalesCommand;
import com.tisawesomeness.minecord.command.utility.ServerCommand;
import com.tisawesomeness.minecord.command.utility.Sha1Command;
import com.tisawesomeness.minecord.command.utility.StatusCommand;
import com.tisawesomeness.minecord.config.serial.CommandConfig;
import com.tisawesomeness.minecord.database.DatabaseCache;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Table;
import lombok.NonNull;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * The list of all commands the bot knows.
 */
public class CommandRegistry implements Iterable<Command> {

    private final Multimap<Module, Command> moduleToCommandsMap;
    private final Table<Lang, String, Command> commandTable;

    /**
     * Adds every module to the registry and maps the possible aliases to the command to execute.
     */
    public CommandRegistry(ShardManager sm, DatabaseCache dbCache, CommandConfig cc) {

        Command colorCmd = new ColorCommand();
        Command[] commands = {

                new ProfileCommand(),
                new HistoryCommand(),
                new UuidCommand(),
                new AvatarCommand(),
                new HeadCommand(),
                new BodyCommand(),
                new SkinCommand(),
                new CapeCommand(),

                new StatusCommand(),
                new SalesCommand(),
                new ServerCommand(),
                new CodesCommand(),
                colorCmd,
                new ColorShortcut(colorCmd, 0),
                new ColorShortcut(colorCmd, 1),
                new ColorShortcut(colorCmd, 2),
                new ColorShortcut(colorCmd, 3),
                new ColorShortcut(colorCmd, 4),
                new ColorShortcut(colorCmd, 5),
                new ColorShortcut(colorCmd, 6),
                new ColorShortcut(colorCmd, 7),
                new ColorShortcut(colorCmd, 8),
                new ColorShortcut(colorCmd, 9),
                new ColorShortcut(colorCmd, 10),
                new ColorShortcut(colorCmd, 11),
                new ColorShortcut(colorCmd, 12),
                new ColorShortcut(colorCmd, 13),
                new ColorShortcut(colorCmd, 14),
                new ColorShortcut(colorCmd, 15),
                new Sha1Command(),
                new ItemCommand(),
                new RecipeCommand(),
                new IngredientCommand(),

                new GuildCommand(),
                new RoleCommand(),
                new RolesCommand(),
                new UserCommand(),
                new PurgeCommand(),

                new SettingsCommand(),
                new SetCommand(),
                new ResetCommand(),
                new PermsCommand(),
                new PrefixCommand(),
                new LangCommand(),

                new HelpCommand(this),
                new InfoCommand(),
                new PingCommand(),
                new InviteCommand(),
                new VoteCommand(),
                new CreditsCommand(),

                new SayCommand(),
                new MsgCommand(),
                new NameCommand(),
                new UsageCommand(this),
                new PromoteCommand(),
                new DemoteCommand(),
                new BanCommand(),
                new ReloadCommand(),
                new ShutdownCommand(),
                new EvalCommand(),
                new DebugCommand(sm, dbCache),
                new TestCommand()

        };

        moduleToCommandsMap = buildModuleToCommandsMap(commands, cc);
        commandTable = buildCommandTable(cc);
    }

    private static Multimap<Module, Command> buildModuleToCommandsMap(Command[] commands, CommandConfig cc) {
        Multimap<Module, Command> mm = MultimapBuilder.enumKeys(Module.class).arrayListValues().build();
        for (Command c : commands) {
            if (c.isEnabled(cc)) {
                mm.put(c.getModule(), c);
            }
        }
        return ImmutableMultimap.copyOf(mm);
    }
    private Table<Lang, String, Command> buildCommandTable(CommandConfig cc) {
        Table<Lang, String, Command> table = HashBasedTable.create();
        for (Command c : this) {
            if (c.isEnabled(cc)) {
                registerNameAndAliases(table, c);
            }
        }
        return ImmutableTable.copyOf(table);
    }
    private static void registerNameAndAliases(Table<? super Lang, ? super String, ? super Command> table, Command c) {
        for (Lang lang : Lang.values()) {
            Collection<String> possibleInputs = new HashSet<>();
            possibleInputs.add(c.getId());
            possibleInputs.add(c.getDisplayName(lang));
            possibleInputs.addAll(c.getAliases(lang));
            for (String input : possibleInputs) {
                table.put(lang, input, c);
            }
        }
    }

    /**
     * Gets a command, given its id, name, or alias.
     * @param name The part of the command after "&" and before a space. For example, "&server hypixel.net" becomes "server".
     * @return The command which should be executed, or empty if there is no command associated with the input.
     */
    public Optional<Command> getCommand(String name, Lang lang) {
        return Optional.ofNullable(commandTable.get(lang, name));
    }
    /**
     * Gets all registered commands that are in the given module.
     * @param module The module
     * @return A possibly-empty list of commands
     */
    public Collection<Command> getCommandsInModule(Module module) {
        return moduleToCommandsMap.get(module);
    }

    /**
     * Used to loop over all commands in this registry.
     * @return An iterator over all commands in all modules
     */
    public @NonNull Iterator<Command> iterator() {
        return moduleToCommandsMap.values().iterator();
    }
    /**
     * Creates a stream for all commands in this registry.
     * @return A stream over all commands in all modules
     */
    public Stream<Command> stream() {
        return moduleToCommandsMap.values().stream();
    }
}
