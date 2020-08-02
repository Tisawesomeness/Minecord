package com.tisawesomeness.minecord.command.config;

import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.setting.parse.SmartSetParser;

import lombok.NonNull;

public class PrefixCommand extends AbstractConfigCommand {

	public @NonNull String getId() {
		return "prefix";
	}
	public CommandInfo getInfo() {
		return new CommandInfo(
                true,
				false,
				true
		);
	}
	
	public Result run(CommandContext ctx) {
		if (ctx.args.length == 0) {
			return new Result(Outcome.SUCCESS, String.format("The current prefix is `%s`", ctx.prefix));
		}
		return new SmartSetParser(ctx, ctx.bot.getSettings().prefix).parse();
	}

}
