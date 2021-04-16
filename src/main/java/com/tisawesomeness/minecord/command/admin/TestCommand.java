package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.command.meta.CommandContext;

import lombok.NonNull;

public class TestCommand extends AbstractAdminCommand {

    public @NonNull String getId() {
        return "test";
    }

    public void run(String[] args, CommandContext ctx) {
        ctx.getE().getGuild().retrieveOwner().queue(mem -> {
            ctx.reply(mem.getEffectiveName());
            throw new RuntimeException("test");
        });
        ctx.reply("Test");
    }

}
