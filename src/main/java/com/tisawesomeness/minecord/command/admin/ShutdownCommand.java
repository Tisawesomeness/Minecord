package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.command.Result;

import lombok.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ShutdownCommand extends AbstractAdminCommand {

    public @NonNull String getId() {
        return "shutdown";
    }

    public void run(String[] args, CommandContext ctx) {

        if (args.length > 0 && "now".equals(args[0])) {
            System.exit(0);
            throw new AssertionError("System.exit() call failed.");
        }

        ctx.log(":x: **Bot shut down by " + ctx.getE().getAuthor().getName() + "**");
        ctx.getE().getChannel().sendMessage(":wave: Goodbye!").complete();
        ExecutorService exe = Executors.newSingleThreadExecutor();
        exe.submit(ctx.getBot()::shutdown);
        exe.shutdown();
        ctx.commandResult(Result.SUCCESS); // Graceful shutdown, just wait...

    }

}
