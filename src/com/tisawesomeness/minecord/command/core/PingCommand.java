package com.tisawesomeness.minecord.command.core;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.util.MessageUtils;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PingCommand extends Command {

    public CommandInfo getInfo() {
		return new CommandInfo(
			"ping",
			"Pings the bot.",
			null,
			null,
			0,
			true,
			false,
			false
		);
	}
	
	public String getHelp() {
		return "Pings the bot.\nUse {&}server to ping a server.\n";
	}
    
    public Result run(String[] args, MessageReceivedEvent e) {
        return new Result(Outcome.SUCCESS, String.format(
            ":ping_pong: **Pong!** `%s ms`\nUse `%sserver` to ping a server.",
            Bot.shardManager.getAverageGatewayPing(), MessageUtils.getPrefix(e)
        ));
    }

}