package com.tisawesomeness.minecord.command.discord;

import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.database.dao.DbUser;
import com.tisawesomeness.minecord.util.DateUtils;
import com.tisawesomeness.minecord.util.DiscordUtils;

import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;

import java.util.List;
import java.util.stream.Collectors;

public class UserCommand extends AbstractDiscordCommand {

    public @NonNull String getId() {
        return "user";
    }

    public Result run(CommandContext ctx) {
        String[] args = ctx.args;
        MessageReceivedEvent e = ctx.e;
        ShardManager sm = ctx.bot.getShardManager();

        //If the author used the admin keyword and is an elevated user
		if (args.length > 1 && args[1].equals("admin") && ctx.isElevated) {
            if (!DiscordUtils.isDiscordId(args[0])) {
                return new Result(Outcome.WARNING, ":warning: Not a valid ID!");
            }
            User u = sm.retrieveUserById(args[0]).onErrorMap(ErrorResponse.UNKNOWN_USER::test, x -> null).complete();
            if (u == null) {
                long uid = Long.valueOf(args[0]);
                DbUser dbUser = ctx.getUser(uid);
                String elevatedStr = String.format("Elevated: `%s`", dbUser.isElevated());
                if (dbUser.isBanned()) {
                    return new Result(Outcome.SUCCESS, "__**USER BANNED FROM MINECORD**__\n" + elevatedStr);
                }
                return new Result(Outcome.SUCCESS, elevatedStr);
            }

            DbUser dbUser = ctx.getUser(u);
            EmbedBuilder eb = new EmbedBuilder()
                .setTitle(MarkdownSanitizer.escape(u.getAsTag()))
                .addField("ID", u.getId(), true)
                .addField("Bot?", u.isBot() ? "Yes" : "No", true)
                .addField("Elevated?", dbUser.isElevated() ? "Yes" : "No", true);
            if (dbUser.isBanned()) {
                eb.setDescription("__**USER BANNED FROM MINECORD**__");
            }
            // Since user caching is disabled, retrieveMember() is required
            // This may cause a lot of requests and lag, so it must be explicitly requested
            if (args.length > 2 && args[2].equals("mutual")) {
                String mutualGuilds = sm.getGuilds().stream()
                    .filter(g -> {
                        try {
                            return g.retrieveMember(u).complete() != null;
                        } catch (ErrorResponseException ex) {
                            return false;
                        }
                    })
                    .map(g -> String.format("%s `%s`", g.getName(), g.getId()))
                    .collect(Collectors.joining("\n"));
                eb.addField("Mutual Guilds", mutualGuilds, false);
            }
            return new Result(Outcome.SUCCESS, ctx.brand(eb).build());
        }
        
        // Guild-only command
        if (!e.isFromGuild()) {
            return new Result(Outcome.WARNING, ":warning: This command is not available in DMs.");
        }

        // Check for argument length
        if (args.length == 0) {
            return ctx.showHelp();
        }
        
        // Find user
        Member mem = null;
        List<Member> mentioned = e.getMessage().getMentionedMembers();
        if (mentioned.size() > 0) {
            mem = mentioned.get(0);
        } else {
            if (DiscordUtils.isDiscordId(args[0])) {
                mem = e.getGuild().retrieveMemberById(args[0]).onErrorMap(ErrorResponse.UNKNOWN_USER::test, x -> null).complete();
                if (mem == null) {
                    return new Result(Outcome.WARNING, ":warning: That user does not exist.");
                }
            } else {
                if (!User.USER_TAG.matcher(args[0]).matches()) {
                    return new Result(Outcome.WARNING,
                            ":warning: Not a valid user format. Use `name#1234`, a mention, or a valid ID.");
                }
                mem = e.getGuild().getMemberByTag(args[0]);
                if (mem == null) {
                    return new Result(Outcome.WARNING, ":warning: That user does not exist.");
                }
            }
        }
        User u = mem.getUser();

        // Build role string
        String roles = "";
        int c = 0;
        for (Role r : mem.getRoles()) {
            roles += r.getAsMention() + "\n";
            c += 1;
            if (c == 5) {
                roles += "...";
                break;
            }
        }

        // Generate user info
        EmbedBuilder eb = new EmbedBuilder()
            .setTitle(MarkdownSanitizer.escape(u.getAsTag()))
            .setColor(mem.getColor())
            .setImage(u.getAvatarUrl())
            .addField("ID", u.getId(), true)
            .addField("Nickname", mem.getNickname() == null ? "None" : MarkdownSanitizer.escape(mem.getNickname()), true)
            .addField("Bot?", u.isBot() ? "Yes" : "No", true)
            .addField("Joined Server", DateUtils.getDateAgo(mem.getTimeJoined()), false)
            .addField("Created Account", DateUtils.getDateAgo(u.getTimeCreated()), false);
        if (mem.getTimeBoosted() != null) {
            eb.addField("Boosted", DateUtils.getDateAgo(mem.getTimeBoosted()), false);
        }
        eb.addField("Roles", roles, false);
        return new Result(Outcome.SUCCESS, ctx.addFooter(eb).build());
    }
    
}