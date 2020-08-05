package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.command.Result;

import lombok.NonNull;

public class TestCommand extends AbstractAdminCommand {

    public @NonNull String getId() {
        return "test";
    }

    public Result run(String[] args, CommandContext ctx) {
        return ctx.reply("Test");
    }

}
