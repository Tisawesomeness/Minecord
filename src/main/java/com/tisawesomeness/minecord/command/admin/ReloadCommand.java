package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.command.CommandContext;

import lombok.NonNull;
import net.dv8tion.jda.api.entities.Message;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class ReloadCommand extends AbstractAdminCommand {

    public @NonNull String getId() {
        return "reload";
    }

    public Result run(String[] args, CommandContext ctx) {

        Message m = ctx.e.getChannel().sendMessage(":arrows_counterclockwise: Reloading...").complete();
        try {
            ctx.bot.reload();
        } catch (IOException | ExecutionException | InterruptedException ex) {
            ex.printStackTrace();
            return new Result(Outcome.ERROR, ":x: Could not reload!");
        }
        m.editMessage(":white_check_mark: Reloaded!").queue();

        return new Result(Outcome.SUCCESS);

    }

}
