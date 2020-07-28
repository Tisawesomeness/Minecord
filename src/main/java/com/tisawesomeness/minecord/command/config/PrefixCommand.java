package com.tisawesomeness.minecord.command.config;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.setting.parse.SmartSetParser;

public class PrefixCommand extends Command {
	
	public CommandInfo getInfo() {
		return new CommandInfo(
			"prefix",
			"Change the prefix.",
			"[prefix]",
			new String[]{
				"resetprefix",
				"changeprefix"},
                true,
			false,
			true
		);
	}

	public String getHelp() {
		return "`{&}prefix` - Show the current prefix.\n" +
			"`{&}prefix <prefix>` - Change the prefix. The user must have **Manage Server** permissions.\n" +
			"\n" +
			"Examples:\n" +
			"- `{&}prefix mc!`\n" +
			"- {@}` prefix &`\n";
	}

	public String getAdminHelp() {
		return "Use `{&}set admin <context> prefix <value>` instead.";
	}
	
	public Result run(CommandContext ctx) {
		if (ctx.args.length == 0) {
			return new Result(Outcome.SUCCESS, String.format("The current prefix is `%s`", ctx.prefix));
		}
		return new SmartSetParser(ctx, ctx.bot.getSettings().prefix).parse();
	}

}
