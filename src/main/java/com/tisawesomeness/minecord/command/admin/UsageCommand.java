package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.Lang;
import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.command.CommandRegistry;
import com.tisawesomeness.minecord.command.Module;
import com.tisawesomeness.minecord.util.DateUtils;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.stream.Collectors;

@RequiredArgsConstructor
public class UsageCommand extends AbstractAdminCommand {

	private @NonNull CommandRegistry registry;

	public @NonNull String getId() {
		return "usage";
	}
	public CommandInfo getInfo() {
		return new CommandInfo(
				true,
				true,
				false
		);
	}

	public Result run(CommandContext ctx) {
		String prefix = ctx.prefix;
		Lang lang = ctx.lang;

		EmbedBuilder eb = new EmbedBuilder()
				.setTitle("Command usage for " + DateUtils.getDurationString(ctx.bot.getBirth()));
		for (Module m : Module.values()) {
			String field = registry.getCommandsInModule(m).stream()
					.filter(c -> c.getDisplayName(lang).isEmpty())
					.map(c -> String.format("`%s%s` **-** %d", prefix, c.getDisplayName(lang), c.uses))
					.collect(Collectors.joining("\n"));
			eb.addField(String.format("**%s**", m.getDisplayName(lang)), field, true);
		}

		return new Result(Outcome.SUCCESS, ctx.brand(eb).build());
	}
	
}
