package com.tisawesomeness.minecord.command;

import java.util.LinkedHashMap;

import com.tisawesomeness.minecord.command.Command.CommandInfo;
import com.tisawesomeness.minecord.command.admin.*;
import com.tisawesomeness.minecord.command.general.*;
import com.tisawesomeness.minecord.command.misc.*;
import com.tisawesomeness.minecord.command.player.*;
import com.tisawesomeness.minecord.command.utility.*;

/**
 * The list of all commands the bot knows.
 */
public class Registry {
	
	private static final String adminHelp = "**These commands require elevation to use.**\n\n" +
	"`{&}info admin` - Displays bot info, including used memory and boot time.\n" +
	"`{&}settings <guild id> admin [setting] [value]` - Change the bot's settings for another guild.\n" +
	"`{&}perms <channel id> admin` - Test the bot's permissions in any channel.\n" +
	"`{&}user <user id> admin [mutual]` - Show info, ban status, and elevation for a user outside of the current guild. Include \"mutual\" to show mutual guilds.\n" +
	"`{&}guild <guild id> admin` - Show info and ban status for another guild.\n";
	public static final Module[] modules = {
		new Module("General",
			new GuildCommand(),
			new RoleCommand(),
			new RolesCommand(),
			new UserCommand(),
			new PurgeCommand(),
			new PermsCommand(),
			new PrefixCommand(),
			new SettingsCommand()
		),
		new Module("Utility",
			new StatusCommand(),
			new SalesCommand(),
			new CodesCommand(),
			new ColorCommand(),
			new ServerCommand(),
			new Sha1Command(),
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
			new CapeCommand(),
			new ProfileCommand()
		),
		new Module("Misc",
			new HelpCommand(),
			new InfoCommand(),
			new PingCommand(),
			new InviteCommand(),
			new VoteCommand(),
			new CreditsCommand()
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
	private static LinkedHashMap<String, Command> commandMap = new LinkedHashMap<String, Command>();
	
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
	 * @return The command which should be executed, or null if there is no command associated with the input.
	 */
	public static Command getCommand(String name) {
		return commandMap.get(name);
	}
	/**
	 * Gets the module a command belongs to
	 * @param cmdName Case-sensitive name of the command
	 * @return The module, or null if not found. This should never return null unless the command name is incorrect or a command was registered without a module.
	 */
	public static String findModuleName(String cmdName) {
		for (Module m : modules) {
			for (Command c : m.getCommands()) {
				if (c.getInfo().name.equals(cmdName)) {
					return m.getName();
				}
			}
		}
		return null;
	}

}
