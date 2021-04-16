package com.tisawesomeness.minecord.command;

import com.tisawesomeness.minecord.command.admin.*;
import com.tisawesomeness.minecord.command.config.*;
import com.tisawesomeness.minecord.command.core.*;
import com.tisawesomeness.minecord.command.discord.*;
import com.tisawesomeness.minecord.command.meta.Category;
import com.tisawesomeness.minecord.command.meta.Command;
import com.tisawesomeness.minecord.command.meta.IMultiNameCommand;
import com.tisawesomeness.minecord.command.player.*;
import com.tisawesomeness.minecord.command.utility.*;
import com.tisawesomeness.minecord.lang.Lang;
import com.tisawesomeness.minecord.lang.LangSpecificKey;
import com.tisawesomeness.minecord.mc.player.RenderType;
import com.tisawesomeness.minecord.util.type.ListValuedEnumMap;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MultiMapUtils;
import org.apache.commons.collections4.MultiValuedMap;

import java.util.*;
import java.util.stream.Stream;

/**
 * A registry of all active commands, their inputs, and categories.
 */
@Slf4j
public class CommandRegistry implements Iterable<Command> {

    private static final String CONFLICT_WARNING = "Input conflict detected for %s, %s will be overwritten with %s, " +
            "check the lang files!";

    private final MultiValuedMap<Category, Command> categoryToCommandsMap;
    private final Map<LangSpecificKey, Command> commandInputMap;
    @Getter private final Command helpCommand;

    /**
     * Adds every commands to the registry and maps the possible aliases to the command to execute.
     */
    public CommandRegistry() {

        helpCommand = new HelpCommand(this);
        Command colorCmd = new ColorCommand();
        Command[] commands = {

                // Core
                helpCommand,
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
        commandInputMap = buildCommandInputMap();
    }

    private static MultiValuedMap<Category, Command> buildCategoryToCommandsMap(Command[] commands) {
        MultiValuedMap<Category, Command> mm = new ListValuedEnumMap<>(Category.class);
        for (Command c : commands) {
            mm.put(c.getCategory(), c);
        }
        return MultiMapUtils.unmodifiableMultiValuedMap(mm);
    }
    private Map<LangSpecificKey, Command> buildCommandInputMap() {
        Map<LangSpecificKey, Command> map = new HashMap<>();
        for (Command c : this) {
            for (LangSpecificKey key : getInputs(c)) {
                if (map.containsKey(key)) {
                    Command overwritten = map.get(key);
                    log.warn(String.format(CONFLICT_WARNING, key, overwritten, c));
                }
                map.put(key, c);
            }
        }
        return Collections.unmodifiableMap(map);
    }
    private static Set<LangSpecificKey> getInputs(Command c) {
        Set<LangSpecificKey> possibleInputs = new HashSet<>();
        for (Lang lang : Lang.values()) {
            for (String input : getLangSpecificInputs(c, lang)) {
                if (input.isEmpty() || input.length() > Command.MAX_NAME_LENGTH) {
                    log.warn(String.format("Not adding input `%s` for %s, length must be from 1-%d but was %d",
                            input, c, Command.MAX_NAME_LENGTH, input.length()));
                } else {
                    possibleInputs.add(new LangSpecificKey(input, lang));
                }
            }
        }
        return possibleInputs;
    }
    private static Set<String> getLangSpecificInputs(Command c, Lang lang) {
        Set<String> langSpecificInputs = new HashSet<>();
        langSpecificInputs.add(c.getId());
        if (c instanceof IMultiNameCommand) {
            for (Lang otherLang : Lang.values()) {
                langSpecificInputs.add(c.getDisplayName(otherLang));
            }
        } else {
            langSpecificInputs.add(c.getDisplayName(lang));
        }
        langSpecificInputs.addAll(c.getAliases(lang));
        return langSpecificInputs;
    }

    /**
     * Gets a command, given its id, name, or alias.
     * @param name The part of the command after "&" and before a space.
     *             For example, "&server hypixel.net" becomes "server".
     * @return The command which should be executed, or empty if there is no command associated with the input.
     */
    public Optional<Command> getCommand(String name, Lang lang) {
        if (name.isEmpty() || name.length() > Command.MAX_NAME_LENGTH) {
            return Optional.empty();
        }
        return Optional.ofNullable(commandInputMap.get(new LangSpecificKey(name, lang)));
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
