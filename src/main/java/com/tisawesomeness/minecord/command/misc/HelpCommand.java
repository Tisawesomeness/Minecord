package com.tisawesomeness.minecord.command.misc;

import com.tisawesomeness.minecord.Lang;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.command.CommandRegistry;
import com.tisawesomeness.minecord.command.IElevatedCommand;
import com.tisawesomeness.minecord.command.IHiddenCommand;
import com.tisawesomeness.minecord.command.Module;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class HelpCommand extends AbstractMiscCommand {

	private @NonNull CommandRegistry registry;

	public @NonNull String getId() {
		return "help";
	}

	public Result run(CommandContext ctx) {
		String[] args = ctx.args;
		String prefix = ctx.prefix;
		Lang lang = ctx.lang;

		EmbedBuilder eb = ctx.brand(new EmbedBuilder());
		String url = ctx.e.getJDA().getSelfUser().getEffectiveAvatarUrl();

		// General help
		if (args.length == 0) {
			// Help menu only contains names of commands, tell user how to get more help
			String help = String.format(
				"Use `%shelp <command>` to get more information about a command.\n" +
				"Use `%shelp <module>` to get help for that module.",
				prefix, prefix);
			eb.setAuthor("Minecord Help", null, url).setDescription(help);

			// Hidden modules must be viewed directly
			for (Module m : Module.values()) {
				if (m.isHidden()) {
					continue;
				}
				// Build that module's list of user-facing commands
				Collection<Command> cmds = registry.getCommandsInModule(m);
				if (cmds.isEmpty()) {
					continue;
				}
				String mHelp = cmds.stream()
					.filter(c -> !(c instanceof IHiddenCommand))
					.map(c -> String.format("`%s%s`", prefix, c.getDisplayName(lang)))
					.collect(Collectors.joining(", "));
				eb.addField(m.getDisplayName(lang), mHelp, false);
			}
			return new Result(Outcome.SUCCESS, eb.build());
		}

		// Module help
		Optional<Module> moduleOpt = Module.from(args[0], lang);
		if (moduleOpt.isPresent()) {
			Module m = moduleOpt.get();
			if (m.isHidden() && !ctx.isElevated) {
				return new Result(Outcome.WARNING, ":warning: You do not have permission to view that module.");
			}
			String mUsage = registry.getCommandsInModule(m).stream()
				.filter(c -> !(c instanceof IHiddenCommand) || m.isHidden()) // All admin commands are hidden
				.map(c -> {
					// Formatting changes based on whether the command has arguments
					Optional<String> usageOpt = c.getUsage(lang);
					if (!usageOpt.isPresent()) {
						return String.format("`%s%s` - %s", prefix, c.getDisplayName(lang), c.getDescription(lang));
					}
					return String.format("`%s%s %s` - %s", prefix, c.getDisplayName(lang), usageOpt.get(), c.getDescription(lang));
				})
				.collect(Collectors.joining("\n"));
			// Add module-specific help if it exists
			Optional<String> mHelp = m.getHelp(lang, prefix);
			if (mHelp.isPresent()) {
				mUsage = mHelp.get() + "\n\n" + mUsage;
			}
			eb.setAuthor(m.getDisplayName(lang) + " Module Help", null, url).setDescription(mUsage);
			return new Result(Outcome.SUCCESS, eb.build());
		}

		// Command help
		Optional<Command> cmdOpt = registry.getCommand(args[0], lang);
		if (cmdOpt.isPresent()) {
			Command c = cmdOpt.get();
			// Elevation check
			if (c instanceof IElevatedCommand && !ctx.isElevated) {
				return new Result(Outcome.WARNING, ":warning: You do not have permission to view that command.");
			}
			// Admin check
			String tag = ctx.e.getJDA().getSelfUser().getAsMention();
			if (args.length > 1 && "admin".equals(args[1])) {
				return showHelpInternal(ctx, c, c.getAdminHelp(lang, prefix, tag));
			}
			return showHelp(ctx, c);
		}
		
		return new Result(Outcome.WARNING, ":warning: That command or module does not exist.");
	}

	public static Result showHelp(CommandContext ctx, Command c) {
		String tag = ctx.e.getJDA().getSelfUser().getAsMention();
		return showHelpInternal(ctx, c, c.getHelp(ctx.lang, ctx.prefix, tag));
	}

	private static Result showHelpInternal(CommandContext ctx, Command c, String help) {
		EmbedBuilder eb = ctx.brand(new EmbedBuilder());
		String prefix = ctx.prefix;
		Lang lang = ctx.lang;

		help += "\n";
		// Alias list formatted with prefix in code blocks
		if (!c.getAliases(lang).isEmpty()) {
			String aliases = c.getAliases(lang).stream()
					.map(s -> String.format("`%s%s`", prefix, s))
					.collect(Collectors.joining(", "));
			help += "\nAliases: " + aliases;
		}
		// If the cooldown is exactly N seconds, treat as int
		int cooldown = c.getCooldown(ctx.config.getCommandConfig());
		if (cooldown > 0) {
			help += getCooldownString(cooldown);
		}
		String desc = String.format("%s\nModule: `%s`", help, c.getModule().getDisplayName(lang));
		eb.setAuthor(prefix + c.getDisplayName(lang) + " Help").setDescription(desc);
		return new Result(Outcome.SUCCESS, eb.build());
	}
	private static String getCooldownString(int cooldown) {
		if (cooldown % 1000 == 0) {
			return String.format("\nCooldown: `%ss`", cooldown / 1000);
		}
		return String.format("\nCooldown: `%ss`", cooldown / 1000.0);
	}

}
