package com.tisawesomeness.minecord.command.admin;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.Module;
import com.tisawesomeness.minecord.command.Registry;
import com.tisawesomeness.minecord.util.DateUtils;
import com.tisawesomeness.minecord.util.MessageUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

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
	
	public Result run(String[] args, MessageReceivedEvent e) {
		String prefix = MessageUtils.getPrefix(e);

		// Build usage message
		EmbedBuilder eb = new EmbedBuilder()
			.setTitle("Command usage for " + DateUtils.getUptime())
			.setColor(Bot.color);
		for (Module m : Registry.modules) {
			String field = Arrays.asList(m.getCommands()).stream()
				.filter(c -> !c.getInfo().name.equals("") && !c.getInfo().description.equals("Look up a color code."))
				.map(c -> String.format("`%s%s` **-** %d", prefix, c.getInfo().name, c.uses))
				.collect(Collectors.joining("\n"));
			eb.addField(String.format("**%s**", m.getName()), field, true);
		}

		return new Result(Outcome.SUCCESS, MessageUtils.addFooter(eb).build());
	}
	
}
