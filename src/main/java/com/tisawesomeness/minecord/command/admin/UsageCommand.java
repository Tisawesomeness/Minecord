package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.command.Module;
import com.tisawesomeness.minecord.command.Registry;
import com.tisawesomeness.minecord.util.DateUtils;

import net.dv8tion.jda.api.EmbedBuilder;

import java.util.Arrays;
import java.util.stream.Collectors;

public class UsageCommand extends Command {
	
	public CommandInfo getInfo() {
		return new CommandInfo(
			"usage",
			"Shows how often commands are used.",
			null,
			null,
			0,
			true,
			true,
			false
		);
	}

	public Result run(CommandContext txt) {
		String prefix = txt.prefix;

		// Build usage message
		EmbedBuilder eb = new EmbedBuilder()
			.setTitle("Command usage for " + DateUtils.getUptime(txt.bot.getBirth()));
		for (Module m : Registry.modules) {
			String field = Arrays.asList(m.getCommands()).stream()
				.filter(c -> !c.getInfo().name.equals("") && !c.getInfo().description.equals("Look up a color code."))
				.map(c -> String.format("`%s%s` **-** %d", prefix, c.getInfo().name, c.uses))
				.collect(Collectors.joining("\n"));
			eb.addField(String.format("**%s**", m.getName()), field, true);
		}

		return new Result(Outcome.SUCCESS, txt.brand(eb).build());
	}
	
}
