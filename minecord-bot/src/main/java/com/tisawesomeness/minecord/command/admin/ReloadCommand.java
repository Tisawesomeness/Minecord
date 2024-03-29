package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.ExitCodes;
import com.tisawesomeness.minecord.command.meta.CommandContext;
import com.tisawesomeness.minecord.command.meta.Result;

import lombok.NonNull;
import net.dv8tion.jda.api.entities.Message;

public class ReloadCommand extends AbstractAdminCommand {

    public @NonNull String getId() {
        return "reload";
    }

    public void run(String[] args, CommandContext ctx) {
        Message m = ctx.getE().getChannel().sendMessage(":arrows_counterclockwise: Reloading...").complete();
        ctx.log(":arrows_counterclockwise: **Bot reloaded by " + ctx.getE().getAuthor().getName() + "**");
        int exitCode = ctx.getBot().reload();
        if (exitCode == ExitCodes.SUCCESS) {
            m.editMessage(":white_check_mark: Reloaded!").queue();
            ctx.commandResult(Result.SUCCESS);
        } else {
            String msg = Result.EXCEPTION.addEmote("Bot reload failed with exit code " + exitCode);
            m.editMessage(msg).queue();
            ctx.commandResult(Result.EXCEPTION);
        }
    }

}
