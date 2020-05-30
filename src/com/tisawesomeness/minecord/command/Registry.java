package com.tisawesomeness.minecord.command;

import java.util.TreeMap;

import com.tisawesomeness.minecord.command.Command.CommandInfo;
import com.tisawesomeness.minecord.command.admin.*;
import com.tisawesomeness.minecord.command.general.*;
import com.tisawesomeness.minecord.command.player.*;
import com.tisawesomeness.minecord.command.utility.*;

/**
 * The list of all commands the bot knows.
 */
public class Registry {
	
	private static final String adminHelp = "< Admin Help >";
	public static final Module[] modules = {
		new Module("General",
			new HelpCommand(),
			new InfoCommand(),
			new PingCommand(),
			new InviteCommand(),
			new PurgeCommand(),
			new PrefixCommand(),
			new GuildCommand(),
			new UserCommand(),
			new SettingsCommand()
		),
		new Module("Utility",
			new StatusCommand(),
			new SalesCommand(),
			new CodesCommand(),
			new ServerCommand(),
			new ItemCommand(),
			new RecipeCommand(),
			new IngredientCommand()
		),
		new Module("Player",
			new UuidCommand(),
			new HistoryCommand(),
			new AvatarCommand(),
			new HeadCommand(),
			new BodyCommand(),
			new SkinCommand(),
			new CapeCommand()
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
			new EvalCommand(),
			new TestCommand()
		)
	};
	private static TreeMap<String, Command> commandMap = new TreeMap<String, Command>(String.CASE_INSENSITIVE_ORDER);
	
	/**
	 * Adds every module to the registry and maps the possible aliases to the command to execute.
	 * Must be executed before getCommand() can be called.
	 */
	public static void init() {
		for (Module m : modules) {
			for (Command c : m.getCommands()) {
				CommandInfo ci = c.getInfo();
				commandMap.put(ci.name, c);
				for (String alias : ci.aliases) commandMap.put(alias, c);
			}
		}
	}
	/**
	 * Gets a command, given its name or alias.
	 * @param name The part of the command after "&" and before a space. For example, "&server hypixel.net" becomes "server".
	 * @return The command which should be executed, or null if there is no command associated with the input.
	 */
	public static Command getCommand(String name) {
		return commandMap.get(name);
	}

}
