package com.tisawesomeness.minecord.command.core;

import com.tisawesomeness.minecord.command.meta.CommandContext;
import com.tisawesomeness.minecord.command.meta.IHiddenCommand;

import lombok.NonNull;

public class PingCommand extends AbstractCoreCommand implements IHiddenCommand {

    public @NonNull String getId() {
        return "ping";
    }

    public void run(String[] args, CommandContext ctx) {
        ctx.triggerCooldown();
        ctx.reply(String.format(
                ":ping_pong: **Pong!** `%s ms`\nUse `%sserver` to ping a server.",
                ctx.getBot().getShardManager().getAverageGatewayPing(), ctx.getPrefix()
        ));
    }

}