package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.command.Module;
import com.tisawesomeness.minecord.command.CommandRegistry;
import com.tisawesomeness.minecord.util.DateUtils;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.Arrays;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class UsageCommand extends Command {

	private @NonNull CommandRegistry registry;
	
	public CommandInfo getInfo() {
		return new CommandInfo(
			"usage",
			"Shows how often commands are used.",
			null,
			null,
				true,
			true,
			false
		);
	}

	public Result run(CommandContext ctx) {
		String prefix = ctx.prefix;

		// Build usage message
		EmbedBuilder eb = new EmbedBuilder()
			.setTitle("Command usage for " + DateUtils.getDurationString(ctx.bot.getBirth()));
		for (Module m : registry.modules) {
			String field = Arrays.asList(m.getCommands()).stream()
				.filter(c -> !c.getInfo().name.equals("") && !c.getInfo().description.equals("Look up a color code."))
				.map(c -> String.format("`%s%s` **-** %d", prefix, c.getInfo().name, c.uses))
				.collect(Collectors.joining("\n"));
			eb.addField(String.format("**%s**", m.getName()), field, true);
		}

		return new Result(Outcome.SUCCESS, ctx.brand(eb).build());
	}
	
}
