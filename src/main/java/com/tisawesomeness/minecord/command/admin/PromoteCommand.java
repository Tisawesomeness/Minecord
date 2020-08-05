package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.command.Result;
import com.tisawesomeness.minecord.database.dao.DbUser;
import com.tisawesomeness.minecord.util.DiscordUtils;

import lombok.NonNull;
import net.dv8tion.jda.api.entities.User;

import java.sql.SQLException;

public class PromoteCommand extends AbstractAdminCommand {

    public @NonNull String getId() {
        return "promote";
    }

    public Result run(String[] args, CommandContext ctx) {

        if (args.length == 0) {
            return ctx.showHelp();
        }

        //Extract user
        User user = DiscordUtils.findUser(args[0], ctx.bot.getShardManager());
        if (user == null) {
            return ctx.warn("Not a valid user!");
        }

        //Don't elevate a normal user
        DbUser dbUser = ctx.getUser(user);
        if (dbUser.isElevated()) {
            return ctx.warn("User is already elevated!");
        }

        //Elevate user
        try {
            dbUser.withElevated(true).update();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return ctx.err("There was an internal error.");
        }
        return ctx.reply(":arrow_up: Elevated " + user.getAsTag());

    }

}
