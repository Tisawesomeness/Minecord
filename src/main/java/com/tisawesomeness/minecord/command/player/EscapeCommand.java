package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.command.IHiddenCommand;
import com.tisawesomeness.minecord.mc.player.Username;

import lombok.NonNull;
import net.dv8tion.jda.api.utils.MarkdownUtil;

public class EscapeCommand extends AbstractPlayerCommand implements IHiddenCommand {
    public @NonNull String getId() {
        return "escape";
    }
    public void run(String[] args, CommandContext ctx) {
        String escaped = String.format("\"%s\"", Username.escape(ctx.joinArgs()));
        ctx.reply(MarkdownUtil.monospace(escaped));
    }
}
