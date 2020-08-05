package com.tisawesomeness.minecord.command.utility;

import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.command.Result;
import com.tisawesomeness.minecord.util.RequestUtils;

import lombok.NonNull;

public class Sha1Command extends AbstractUtilityCommand {

    public @NonNull String getId() {
        return "sha1";
    }

    public Result run(String[] args, CommandContext ctx) {
        if (args.length == 0) {
            return ctx.showHelp();
        }
        return ctx.reply(RequestUtils.sha1(ctx.joinArgs()));
    }

}