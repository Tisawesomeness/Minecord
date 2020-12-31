package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.command.CommandContext;

import lombok.NonNull;

public class TestCommand extends AbstractAdminCommand {

    public @NonNull String getId() {
        return "test";
    }

    public void run(String[] args, CommandContext ctx) {
        ctx.reply("Test");
    }

}
