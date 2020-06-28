package com.tisawesomeness.minecord.command.general;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.database.Database;
import com.tisawesomeness.minecord.util.DateUtils;
import com.tisawesomeness.minecord.util.DiscordUtils;

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

public class UserCommand extends Command {

    public CommandInfo getInfo() {
		return new CommandInfo(
			"user",
			"Shows user info.",
			"<user>",
			new String[]{"whois", "userinfo"},
			0,
			false,
			false,
			false
		);
    }

    public String getHelp() {
        return "Shows the info of a user in the current guild.\n" +
            "`<user>` can be `name#1234`, a mention, or a valid ID.`\n" +
            "\n" +
            "Examples:\n" +
            "- `{&}user @Tis_awesomeness`\n" +
            "- `{&}user Tis_awesomeness#8617`\n" +
            "- `{&}user 211261249386708992`\n";
    }

    public String getAdminHelp() {
        return "`{&}user <user>` - Shows the info of a user in the current guild.\n" +
            "`{&}user <user id> admin` - Shows the info, ban status, and elevation of a user.\n" +
            "`{&}user <user id> admin mutual` - Includes mutual guilds.\n" +
            "__**Requesting mutual guilds with a large bot may freeze the shard!**__\n" +
            "`<user>` can be `name#1234`, a mention, or a valid ID.`\n" +
            "\n" +
            "Examples:\n" +
            "- `{&}user @Tis_awesomeness`\n" +
            "- `{&}user Tis_awesomeness#8617`\n" +
            "- `{&}user 211261249386708992`\n" +
            "- `{&}user 211261249386708992 admin`\n";
    }
    
    public Result run(CommandContext txt) {
        String[] args = txt.args;
        MessageReceivedEvent e = txt.e;
        ShardManager sm = txt.bot.getShardManager();
        Database db = txt.bot.getDatabase();

        //If the author used the admin keyword and is an elevated user
		if (args.length > 1 && args[1].equals("admin") && txt.isElevated) {
            if (!args[0].matches(DiscordUtils.idRegex)) {
                return new Result(Outcome.WARNING, ":warning: Not a valid ID!");
            }
            User u = sm.retrieveUserById(args[0]).onErrorMap(ErrorResponse.UNKNOWN_USER::test, x -> null).complete();
            if (u == null) {
                long gid = Long.valueOf(args[0]);
                String elevatedStr = String.format("Elevated: `%s`", db.isElevated(gid));
                if (db.isBanned(gid)) {
                    return new Result(Outcome.SUCCESS, "__**USER BANNED FROM MINECORD**__\n" + elevatedStr);
                }
                return new Result(Outcome.SUCCESS, elevatedStr);
            }

            EmbedBuilder eb = new EmbedBuilder()
                .setTitle(MarkdownSanitizer.escape(u.getAsTag()))
                .addField("ID", u.getId(), true)
                .addField("Bot?", u.isBot() ? "Yes" : "No", true)
                .addField("Elevated?", db.isElevated(u.getIdLong()) ? "Yes" : "No", true);
            if (db.isBanned(u.getIdLong())) {
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
            return new Result(Outcome.SUCCESS, txt.brand(eb).build());
        }
        
        // Guild-only command
        if (!e.isFromGuild()) {
            return new Result(Outcome.WARNING, ":warning: This command is not available in DMs.");
        }

        // Check for argument length
        if (args.length == 0) {
            return new Result(Outcome.WARNING, ":warning: You must specify a user!");
        }
        
        // Find user
        Member mem = null;
        List<Member> mentioned = e.getMessage().getMentionedMembers();
        if (mentioned.size() > 0) {
            mem = mentioned.get(0);
        } else {
            if (args[0].matches(DiscordUtils.idRegex)) {
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
        return new Result(Outcome.SUCCESS, txt.addFooter(eb).build());
    }
    
}