package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.command.meta.CommandContext;
import com.tisawesomeness.minecord.command.meta.Result;

import lombok.NonNull;
import net.dv8tion.jda.api.entities.Message;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class ReloadCommand extends AbstractAdminCommand {

    public @NonNull String getId() {
        return "reload";
    }

    public void run(String[] args, CommandContext ctx) {

        Message m = ctx.getE().getChannel().sendMessage(":arrows_counterclockwise: Reloading...").complete();
        try {
            ctx.getBot().reload();
        } catch (IOException | ExecutionException | InterruptedException ex) {
            ex.printStackTrace();
            ctx.sendResult(Result.EXCEPTION, "Could not reload!"); // A failed reload is REALLY severe
            return;
        }
        m.editMessage(":white_check_mark: Reloaded!").queue();

        ctx.commandResult(Result.SUCCESS);

    }

}
