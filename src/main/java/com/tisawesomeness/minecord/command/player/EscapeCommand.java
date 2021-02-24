package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.command.IHiddenCommand;
import com.tisawesomeness.minecord.mc.player.Username;

import lombok.NonNull;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;
import net.dv8tion.jda.api.utils.MarkdownUtil;

public class EscapeCommand extends AbstractPlayerCommand implements IHiddenCommand {
    public @NonNull String getId() {
        return "escape";
    }

    public void run(String[] args, CommandContext ctx) {

        if (args.length == 0) {
            ctx.showHelp();
            return;
        }
        String input = ctx.joinArgs();
        if (input.length() > Username.MAX_LENGTH) {
            ctx.warn(ctx.getLang().i18n("mc.player.username.tooLong"));
            return;
        }

        ctx.triggerCooldown();
        String escaped = String.format("\"%s\"", Username.escape(input));
        // Text that contains backticks doesn't work inside monospace
        if (escaped.contains("`")) {
            ctx.reply(MarkdownSanitizer.escape(escaped));
        } else {
            ctx.reply(MarkdownUtil.monospace(escaped));
        }

    }

}
