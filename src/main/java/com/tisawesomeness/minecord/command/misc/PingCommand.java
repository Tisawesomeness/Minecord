package com.tisawesomeness.minecord.command.misc;

import com.tisawesomeness.minecord.command.CommandContext;

import lombok.NonNull;

public class PingCommand extends AbstractMiscCommand {

	public @NonNull String getId() {
		return "ping";
	}
    public CommandInfo getInfo() {
		return new CommandInfo(
                true,
				false,
				false
		);
	}
    
    public Result run(CommandContext ctx) {
        return new Result(Outcome.SUCCESS, String.format(
            ":ping_pong: **Pong!** `%s ms`\nUse `%sserver` to ping a server.",
				ctx.bot.getShardManager().getAverageGatewayPing(), ctx.prefix
        ));
    }

}