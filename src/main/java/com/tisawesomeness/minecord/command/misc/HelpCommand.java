package com.tisawesomeness.minecord.command.misc;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.command.Module;
import com.tisawesomeness.minecord.command.Registry;

import net.dv8tion.jda.api.EmbedBuilder;

import java.util.Arrays;
import java.util.stream.Collectors;

public class HelpCommand extends Command {
	
	public CommandInfo getInfo() {
		return new CommandInfo(
			"help",
			"Displays help for the bot, a command, or a module.",
			"[command|module]",
			new String[]{
				"cmds",
				"commands",
				"module",
				"modules",
				"categories"},
			0,
			false,
			false,
			true
		);
	}

	public String getHelp() {
		return "`{&}help` - Display help for the bot.\n" +
			"`{&}help <module>` - Display help for a module.\n" +
			"`{&}help <command>` - Display help for a command.\n" +
			"\n" +
			"Examples:\n" +
			"- `{&}help utility`\n" +
			"- `{&}help server`\n";
	}

	public String getAdminHelp() {
		return "`{&}help` - Display help for the bot.\n" +
			"`{&}help <module>` - Display help for a module.\n" +
			"`{&}help <command>` - Display help for a command.\n" +
			"`{&}help <command> admin` - Include admin-only command usage.\n" +
			"\n" +
			"Examples:\n" +
			"- `{&}help utility`\n" +
			"- `{&}help server`\n" +
			"- `{&}help settings admin`\n";
	}
	
	public Result run(CommandContext txt) {
		String[] args = txt.args;
		String prefix = txt.prefix;

		EmbedBuilder eb = txt.brand(new EmbedBuilder());
		String url = txt.e.getJDA().getSelfUser().getEffectiveAvatarUrl();

		// General help
		if (args.length == 0) {
			// Help menu only contains names of commands, tell user how to get more help
			String help = String.format(
				"Use `%shelp <command>` to get more information about a command.\n" +
				"Use `%shelp <module>` to get help for that module.",
				prefix, prefix);
			eb.setAuthor("Minecord Help", null, url).setDescription(help);

			// Hidden modules must be viewed directly
			for (Module m : Registry.modules) {
				if (m.isHidden()) {
					continue;
				}
				// Build that module's list of user-facing commands
				String mHelp = Arrays.asList(m.getCommands()).stream()
					.map(c -> c.getInfo())
					.filter(ci -> !ci.hidden)
					.map(ci -> String.format("`%s%s`", prefix, ci.name))
					.collect(Collectors.joining(", "));
				eb.addField(m.getName(), mHelp, false);
			}
			return new Result(Outcome.SUCCESS, eb.build());
		}

		// Module help
		Module m = Registry.getModule(args[0]);
		if (m != null) {
			if (m.isHidden() && !txt.isElevated) {
				return new Result(Outcome.WARNING, ":warning: You do not have permission to view that module.");
			}
			String mUsage = Arrays.asList(m.getCommands()).stream()
				.map(c -> c.getInfo())
				.filter(ci -> !ci.hidden || m.isHidden()) // All admin commands are hidden
				.map(ci -> {
					// Formatting changes based on whether the command has arguments
					if (ci.usage == null) {
						return String.format("`%s%s` - %s", prefix, ci.name, ci.description);
					}
					return String.format("`%s%s %s` - %s", prefix, ci.name, ci.usage, ci.description);
				})
				.collect(Collectors.joining("\n"));
			// Add module-specific help if it exists
			String mHelp = m.getHelp(prefix);
			if (mHelp != null) {
				mUsage = mHelp + "\n" + mUsage;
			}
			eb.setAuthor(m.getName() + " Module Help", null, url).setDescription(mUsage);
			return new Result(Outcome.SUCCESS, eb.build());
		}

		// Command help
		Command c = Registry.getCommand(args[0]);
		if (c != null) {
			// Elevation check
			CommandInfo ci = c.getInfo();
			if (ci.elevated && !txt.isElevated) {
				return new Result(Outcome.WARNING, ":warning: You do not have permission to view that command.");
			}
			// Admin check
			String help;
			if (args.length > 1 && args[1].equals("admin")) {
				help = c.getAdminHelp();
			} else {
				help = c.getHelp();
			}
			// {@} and {&} substitution
			help = help.replace("{@}", txt.e.getJDA().getSelfUser().getAsMention()).replace("{&}", prefix);
			// Alias list formatted with prefix in code blocks
			if (ci.aliases.length > 0) {
				String aliases = Arrays.asList(ci.aliases).stream()
					.map(s -> String.format("`%s%s`", prefix, s))
					.collect(Collectors.joining(", "));
				help += "\nAliases: " + aliases;
			}
			// If the cooldown is exactly N seconds, treat as int
			if (ci.cooldown > 0) {
				if (ci.cooldown % 1000 == 0) {
					help += String.format("\nCooldown: `%ss`", ci.cooldown / 1000);
				} else {
					help += String.format("\nCooldown: `%ss`", ci.cooldown / 1000.0);
				}
			}
			String desc = String.format("%s\nModule: `%s`", help, Registry.findModuleName(ci.name));
			eb.setAuthor(prefix + ci.name + " Help").setDescription(desc);
			return new Result(Outcome.SUCCESS, eb.build());
		}
		
		return new Result(Outcome.WARNING, ":warning: That command or module does not exist.");
	}

}
