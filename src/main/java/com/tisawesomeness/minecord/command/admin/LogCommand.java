package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.command.meta.CommandContext;

import lombok.NonNull;
import net.dv8tion.jda.api.entities.User;

public class LogCommand extends AbstractAdminCommand {

    public @NonNull String getId() {
        return "log";
    }

    public void run(String[] args, CommandContext ctx) {
        if (args.length == 0) {
            ctx.showHelp();
            return;
        }
        User author = ctx.getE().getAuthor();
        String msg = String.format("&log sent by %#s, ID %d: %s", author, author.getIdLong(), ctx.joinArgs());
        ctx.log(msg);
        ctx.reply(":notepad_spiral: Log sent!");
    }

}
