package com.tisawesomeness.minecord.command.general;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.Module;
import com.tisawesomeness.minecord.command.Registry;
import com.tisawesomeness.minecord.database.Database;
import com.tisawesomeness.minecord.util.MessageUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

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
	
	public Result run(String[] args, MessageReceivedEvent e) {
		String prefix = MessageUtils.getPrefix(e);
		EmbedBuilder eb = new EmbedBuilder().setColor(Bot.color);
		String url = e.getJDA().getSelfUser().getEffectiveAvatarUrl();

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
			return new Result(Outcome.SUCCESS, MessageUtils.addFooter(eb).build());
		}

		// Module help
		Module m = Registry.getModule(args[0]);
		if (m != null) {
			if (m.isHidden() && !Database.isElevated(e.getAuthor().getIdLong())) {
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
			return new Result(Outcome.SUCCESS, MessageUtils.addFooter(eb).build());
		}

		// Command help
		Command c = Registry.getCommand(args[0]);
		if (c != null) {
			CommandInfo ci = c.getInfo();
			if (ci.elevated && !Database.isElevated(e.getAuthor().getIdLong())) {
				return new Result(Outcome.WARNING, ":warning: You do not have permission to view that command.");
			}
			String help = c.getHelp().replace("{@}", e.getJDA().getSelfUser().getAsMention()).replace("{&}", prefix);
			if (ci.aliases.length > 0) {
				String aliases = Arrays.asList(ci.aliases).stream()
					.map(s -> String.format("`%s%s`", prefix, s))
					.collect(Collectors.joining(", "));
				help += "\nAliases: " + aliases;
			}
			if (ci.cooldown > 0) {
				help += String.format("\nCooldown: `%ss`", ci.cooldown / 1000);
			}
			String desc = String.format("%s\nModule: `%s`", help, Registry.findModuleName(ci.name));
			eb.setAuthor(prefix + ci.name + " Help").setDescription(desc);
			return new Result(Outcome.SUCCESS, MessageUtils.addFooter(eb).build());
		}
		
		return new Result(Outcome.WARNING, ":warning: That command or module does not exist.");
	}

}
