package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.database.dao.DbUser;
import com.tisawesomeness.minecord.util.Discord;

import lombok.NonNull;
import net.dv8tion.jda.api.entities.User;

import java.sql.SQLException;

public class PromoteCommand extends AbstractAdminCommand {

    public @NonNull String getId() {
        return "promote";
    }

    public void run(String[] args, CommandContext ctx) {

        if (args.length == 0) {
            ctx.showHelp();
            return;
        }

        //Extract user
        User user = Discord.findUser(args[0], ctx.getBot().getShardManager());
        if (user == null) {
            ctx.warn("Not a valid user!");
            return;
        }

        //Don't elevate a normal user
        DbUser dbUser = ctx.getUser(user);
        if (dbUser.isElevated()) {
            ctx.warn("User is already elevated!");
            return;
        }

        //Elevate user
        try {
            dbUser.withElevated(true).update();
        } catch (SQLException ex) {
            ex.printStackTrace();
            ctx.err("There was an internal error.");
            return;
        }
        ctx.reply(":arrow_up: Elevated " + user.getAsTag());

    }

}
