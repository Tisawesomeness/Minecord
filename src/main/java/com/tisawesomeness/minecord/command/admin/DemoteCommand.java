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

    public void run(String[] args, CommandContext ctx) {

        if (args.length == 0) {
            ctx.showHelp();
            return;
        }

        //Extract user
        User user = DiscordUtils.findUser(args[0], ctx.getBot().getShardManager());
        if (user == null) {
            ctx.warn("Not a valid user!");
            return;
        }
        long id = user.getIdLong();

        //Don't demote a normal user
        DbUser dbUser = ctx.getUser(id);
        if (!dbUser.isElevated()) {
            ctx.warn("User is not elevated!");
            return;
        }

        //Can't demote the owner
        if (ctx.getConfig().isOwner(id)) {
            ctx.warn("You can't demote the owner!");
            return;
        }

        //Demote user
        try {
            dbUser.withElevated(false).update();
        } catch (SQLException ex) {
            ex.printStackTrace();
            ctx.warn("There was an internal error.");
            return;
        }
        ctx.reply(":arrow_down: Demoted " + user.getAsTag());

    }

}
