package com.tisawesomeness.minecord.command.utility;

import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.util.RequestUtils;

import lombok.NonNull;

public class Sha1Command extends AbstractUtilityCommand {

    public @NonNull String getId() {
        return "sha1";
    }
    public CommandInfo getInfo() {
		return new CommandInfo(
                false,
                false,
                true
		);
    }

    public Result run(CommandContext ctx) {
        if (ctx.args.length == 0) {
            return ctx.showHelp();
        }
        return new Result(Outcome.SUCCESS, RequestUtils.sha1(String.join(" ", ctx.args)));
    }

}