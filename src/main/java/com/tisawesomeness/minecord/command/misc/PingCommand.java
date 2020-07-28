package com.tisawesomeness.minecord.command.misc;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.CommandContext;

public class PingCommand extends Command {

    public CommandInfo getInfo() {
		return new CommandInfo(
			"ping",
			"Pings the bot.",
			null,
			null,
                true,
			false,
			false
		);
	}
	
	public String getHelp() {
		return "Pings the bot.\nUse {&}server to ping a server.\n";
	}
    
    public Result run(CommandContext ctx) {
        return new Result(Outcome.SUCCESS, String.format(
            ":ping_pong: **Pong!** `%s ms`\nUse `%sserver` to ping a server.",
				ctx.bot.getShardManager().getAverageGatewayPing(), ctx.prefix
        ));
    }

}