package com.tisawesomeness.minecord.command;

import com.tisawesomeness.minecord.command.admin.*;
import com.tisawesomeness.minecord.command.config.*;
import com.tisawesomeness.minecord.command.discord.*;
import com.tisawesomeness.minecord.command.core.*;
import com.tisawesomeness.minecord.command.player.*;
import com.tisawesomeness.minecord.command.utility.*;
import com.tisawesomeness.minecord.lang.Lang;
import com.tisawesomeness.minecord.mc.player.RenderType;

import com.google.common.collect.*;
import lombok.NonNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * The list of all commands the bot knows.
 */
public class CommandRegistry implements Iterable<Command> {

    private final Multimap<Category, Command> categoryToCommandsMap;
    private final Table<Lang, String, Command> commandTable;

    /**
     * Adds every category to the registry and maps the possible aliases to the command to execute.
     */
    public CommandRegistry() {

        Command colorCmd = new ColorCommand();
        Command[] commands = {

                // Core
                new HelpCommand(this),
                new InfoCommand(),
                new PingCommand(),
                new InviteCommand(),
                new VoteCommand(),
                new CreditsCommand(),
                new ThankCommand(),

                // Player
                new ProfileCommand(),
                new HistoryCommand(),
                new UuidCommand(),
                new RenderCommand(RenderType.AVATAR),
                new RenderCommand(RenderType.HEAD),
                new RenderCommand(RenderType.BODY),
                new GeneralRenderCommand(),
                new SkinCommand(),
                new CapeCommand(),
                new EscapeCommand(),

                // Utility
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

                // Discord
                new GuildCommand(),
                new RoleCommand(),
                new RolesCommand(),
                new UserCommand(),
                new PurgeCommand(),

                // Config
                new SettingsCommand(),
                new SetCommand(),
                new ResetCommand(),
                new PermsCommand(),
                new PrefixCommand(),
                new LangCommand(),

                // Admin
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
                new DebugCommand(),
                new TestCommand()

        };

        categoryToCommandsMap = buildCategoryToCommandsMap(commands);
        commandTable = buildCommandTable();
    }

    private static Multimap<Category, Command> buildCategoryToCommandsMap(Command[] commands) {
        Multimap<Category, Command> mm = MultimapBuilder.enumKeys(Category.class).arrayListValues().build();
        for (Command c : commands) {
            mm.put(c.getCategory(), c);
        }
        return ImmutableMultimap.copyOf(mm);
    }
    private Table<Lang, String, Command> buildCommandTable() {
        Table<Lang, String, Command> table = HashBasedTable.create();
        for (Command c : this) {
            registerNameAndAliases(table, c);
        }
        return ImmutableTable.copyOf(table);
    }
    private static void registerNameAndAliases(Table<Lang, String, Command> table, Command c) {
        for (Lang lang : Lang.values()) {
            Collection<String> possibleInputs = new HashSet<>();
            possibleInputs.add(c.getId());
            if (c instanceof IMultiNameCommand) {
                for (Lang lang2 : Lang.values()) {
                    possibleInputs.add(c.getDisplayName(lang2));
                }
            } else {
                possibleInputs.add(c.getDisplayName(lang));
            }
            possibleInputs.addAll(c.getAliases(lang));
            for (String input : possibleInputs) {
                table.put(lang, input, c);
            }
        }
    }

    /**
     * Gets a command, given its id, name, or alias.
     * @param name The part of the command after "&" and before a space.
     *             For example, "&server hypixel.net" becomes "server".
     * @return The command which should be executed, or empty if there is no command associated with the input.
     */
    public Optional<Command> getCommand(String name, Lang lang) {
        return Optional.ofNullable(commandTable.get(lang, name));
    }
    /**
     * Gets all registered commands that are in the given category.
     * @param category The category
     * @return A possibly-empty list of commands
     */
    public Collection<Command> getCommandsInCategory(Category category) {
        return categoryToCommandsMap.get(category);
    }

    /**
     * Used to loop over all commands in this registry.
     * @return An iterator over all commands in all categories
     */
    public @NonNull Iterator<Command> iterator() {
        return categoryToCommandsMap.values().iterator();
    }
    /**
     * Creates a stream for all commands in this registry.
     * @return A stream over all commands in all categories
     */
    public Stream<Command> stream() {
        return categoryToCommandsMap.values().stream();
    }

}
