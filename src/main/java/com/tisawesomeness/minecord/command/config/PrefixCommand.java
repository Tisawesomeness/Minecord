package com.tisawesomeness.minecord.command.config;

import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.command.IHiddenCommand;
import com.tisawesomeness.minecord.command.Result;
import com.tisawesomeness.minecord.setting.parse.SmartSetParser;

import lombok.NonNull;

public class PrefixCommand extends AbstractConfigCommand implements IHiddenCommand {

    public @NonNull String getId() {
        return "prefix";
    }

    public Result run(String[] args, CommandContext ctx) {
        if (args.length == 0) {
            ctx.triggerCooldown();
            return ctx.reply(String.format("The current prefix is `%s`", ctx.getPrefix()));
        }
        return new SmartSetParser(ctx, ctx.getBot().getSettings().prefix).parse();
    }

}
