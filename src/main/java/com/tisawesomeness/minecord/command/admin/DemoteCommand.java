package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.database.dao.DbUser;
import com.tisawesomeness.minecord.util.DiscordUtils;

import lombok.NonNull;
import net.dv8tion.jda.api.entities.User;

import java.sql.SQLException;

public class DemoteCommand extends AbstractAdminCommand {

    public @NonNull String getId() {
        return "demote";
    }

    public Result run(CommandContext ctx) {

        if (ctx.args.length == 0) {
            return ctx.showHelp();
        }

        //Extract user
        User user = DiscordUtils.findUser(ctx.args[0], ctx.bot.getShardManager());
        if (user == null) return new Result(Outcome.ERROR, ":x: Not a valid user!");
        long id = user.getIdLong();

        //Don't demote a normal user
        DbUser dbUser = ctx.getUser(id);
        if (!dbUser.isElevated()) {
            return new Result(Outcome.WARNING, ":warning: User is not elevated!");
        }

        //Can't demote the owner
        if (ctx.config.isOwner(id)) {
            return new Result(Outcome.WARNING, ":warning: You can't demote the owner!");
        }

        //Demote user
        try {
            dbUser.withElevated(false).update();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return new Result(Outcome.ERROR, ":x: There was an internal error.");
        }
        return new Result(Outcome.SUCCESS, ":arrow_down: Demoted " + user.getAsTag());

    }

}
