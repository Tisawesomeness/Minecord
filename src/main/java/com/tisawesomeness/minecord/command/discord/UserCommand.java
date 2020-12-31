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

    public void run(String[] args, CommandContext ctx) {
        MessageReceivedEvent e = ctx.getE();
        ShardManager sm = ctx.getBot().getShardManager();

        //If the author used the admin keyword and is an elevated user
        if (args.length > 1 && args[1].equals("admin") && ctx.isElevated()) {
            if (!DiscordUtils.isDiscordId(args[0])) {
                ctx.invalidArgs("Not a valid ID!");
                return;
            }
            User u = sm.retrieveUserById(args[0]).onErrorMap(ErrorResponse.UNKNOWN_USER::test, x -> null).complete();
            if (u == null) {
                long uid = Long.valueOf(args[0]);
                DbUser dbUser = ctx.getUser(uid);
                String elevatedStr = String.format("Elevated: `%s`", dbUser.isElevated());
                if (dbUser.isBanned()) {
                    ctx.reply("__**USER BANNED FROM MINECORD**__\n" + elevatedStr);
                    return;
                }
                ctx.reply(elevatedStr);
                return;
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
            ctx.reply(eb);
            return;
        }
        
        // Guild-only command
        if (!e.isFromGuild()) {
            ctx.warn("This command is not available in DMs.");
            return;
        }

        // Check for argument length
        if (args.length == 0) {
            ctx.showHelp();
            return;
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
                    ctx.warn("That user does not exist.");
                    return;
                }
            } else {
                if (!User.USER_TAG.matcher(args[0]).matches()) {
                    ctx.invalidArgs("Not a valid user format. Use `name#1234`, a mention, or a valid ID.");
                    return;
                }
                mem = e.getGuild().getMemberByTag(args[0]);
                if (mem == null) {
                    ctx.warn("That user does not exist.");
                    return;
                }
            }
        }
        User u = mem.getUser();
        ctx.triggerCooldown();

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
        ctx.replyRaw(ctx.addFooter(eb));
    }
    
}