package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.command.Result;
import com.tisawesomeness.minecord.database.dao.DbGuild;
import com.tisawesomeness.minecord.database.dao.DbUser;
import com.tisawesomeness.minecord.util.DiscordUtils;

import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.sql.SQLException;

public class BanCommand extends AbstractAdminCommand {

    public @NonNull String getId() {
        return "ban";
    }

    public Result run(String[] args, CommandContext ctx) {

        //Check for proper argument length
        if (args.length < 1) {
            return ctx.showHelp();
        }

        ShardManager sm = ctx.bot.getShardManager();

        //User part of command
        if ("user".equals(args[0])) {
            //Get user from message
            if (args.length == 1) {
                return ctx.warn("Please define a user.");
            }
            if (!DiscordUtils.isDiscordId(args[1])) {
                return ctx.warn("Not a valid ID!");
            }
            if (ctx.config.isOwner(args[1])) {
                return ctx.warn("You can't ban the owner!");
            }
            long uid = Long.valueOf(args[1]);
            DbUser dbUser = ctx.getUser(uid);
            //Ban or unban user
            boolean banned = dbUser.isBanned();
            try {
                dbUser.withBanned(!banned).update();
            } catch (SQLException ex) {
                ex.printStackTrace();
                return ctx.err("There was an internal error.");
            }
            //Format message
            User user = sm.retrieveUserById(args[1]).onErrorMap(ErrorResponse.UNKNOWN_USER::test, x -> null).complete();
            String msg = user == null ? args[1] : user.getAsTag();
            msg += banned ? " has been unbanned." : " was struck with the ban hammer!";
            return ctx.reply(msg);

        //Guild part of command
        } else if ("guild".equals(args[0])) {
            //Get guild from message
            if (args.length == 1) {
                return ctx.warn("Please define a guild.");
            }
            if (!DiscordUtils.isDiscordId(args[1])) {
                return ctx.warn("Not a valid ID!");
            }
            Guild guild = sm.getGuildById(args[1]);
            long logChannelID = ctx.config.getLogChannelId();
            if (guild != null && logChannelID != 0) {
                TextChannel logChannel = sm.getTextChannelById(logChannelID);
                if (logChannel != null && guild.getId().equals(logChannel.getGuild().getId())) {
                    return ctx.warn("You can't ban the guild with the log channel!");
                }
            }
            long gid = Long.valueOf(args[1]);
            DbGuild dbGuild = ctx.getGuild(gid);
            //Ban or unban guild
            boolean banned = dbGuild.isBanned();
            try {
                dbGuild.withBanned(!banned).update();
            } catch (SQLException ex) {
                ex.printStackTrace();
                return ctx.err("There was an internal error.");
            }
            //Format message
            String msg = guild.getName() + " (`" + guild.getId() + "`) ";
            msg += banned ? "has been unbanned." : "was struck with the ban hammer!";
            return ctx.reply(msg);

        //Query part of command
        } else {
            if (!DiscordUtils.isDiscordId(args[0])) {
                return ctx.warn("Not a valid ID!");
            }
            long id = Long.valueOf(args[0]);
            boolean banned = ctx.getGuild(id).isBanned() || ctx.getUser(id).isBanned();
            String msg = args[0] + (banned ? " is banned!" : " is not banned.");
            return ctx.reply(msg);
        }

    }
}
