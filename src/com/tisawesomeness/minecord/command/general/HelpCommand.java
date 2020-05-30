package com.tisawesomeness.minecord.command.general;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.Module;
import com.tisawesomeness.minecord.command.Registry;
import com.tisawesomeness.minecord.util.MessageUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class HelpCommand extends Command {
	
	public CommandInfo getInfo() {
		return new CommandInfo(
			"help",
			"Displays this help menu.",
			null,
			new String[]{
				"commands",
				"cmds"},
			0,
			false,
			false,
			true
		);
	}
	
	public Result run(String[] args, MessageReceivedEvent e) {
		String prefix = MessageUtils.getPrefix(e);

		// Help menu only contains names of commands, tell user how to get more help
		String help = String.format(
			"Use `%shelp <command>` to get more information about a command.\n" +
			"Use `%shelp <module>` to get help for that module.",
			prefix, prefix);
		EmbedBuilder eb = new EmbedBuilder()
			.setAuthor("Minecord Help", null, e.getJDA().getSelfUser().getEffectiveAvatarUrl())
			.setColor(Bot.color)
			.setDescription(help);

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

}
