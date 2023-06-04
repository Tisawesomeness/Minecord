package com.tisawesomeness.minecord.command.discord;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.LegacyCommand;
import com.tisawesomeness.minecord.database.Database;
import com.tisawesomeness.minecord.util.DiscordUtils;
import com.tisawesomeness.minecord.util.MessageUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;

import java.util.stream.Collectors;

public class UserCommandAdmin extends LegacyCommand {

    @Override
    public CommandInfo getInfo() {
        return new CommandInfo(
                "useradmin",
                "Shows user info.",
                "<user>",
                0,
                true,
                true
        );
    }

    @Override
    public String getHelp() {
        return "`{&}useradmin <user id>` - Shows the info, ban status, and elevation of a user.\n" +
                "`{&}useradmin <user id> mutual` - Includes mutual guilds.\n" +
                "__**Requesting mutual guilds with a large bot may freeze the shard!**__\n" +
                "\n" +
                "Examples:\n" +
                "- `{&}user 211261249386708992`\n";
    }

    @Override
    public Result run(String[] args, MessageReceivedEvent e) throws Exception {
        if (args.length == 0) {
            return new Result(Outcome.WARNING, ":warning: You must specify a user id.");
        }

        if (!DiscordUtils.isDiscordId(args[0])) {
            return new Result(Outcome.WARNING, ":warning: Not a valid ID!");
        }
        User u = Bot.shardManager.retrieveUserById(args[0]).onErrorMap(ErrorResponse.UNKNOWN_USER::test, x -> null).complete();
        if (u == null) {
            long gid = Long.parseLong(args[0]);
            String elevatedStr = String.format("Elevated: `%s`", Database.isElevated(gid));
            if (Database.isBanned(gid)) {
                return new Result(Outcome.SUCCESS, "__**USER BANNED FROM MINECORD**__\n" + elevatedStr);
            }
            return new Result(Outcome.SUCCESS, elevatedStr);
        }

        EmbedBuilder eb = new EmbedBuilder()
                .setTitle(MarkdownSanitizer.escape(u.getEffectiveName()))
                .setColor(Bot.color)
                .addField("ID", u.getId(), true)
                .addField("Bot?", u.isBot() ? "Yes" : "No", true)
                .addField("Elevated?", Database.isElevated(u.getIdLong()) ? "Yes" : "No", true);
        if (Database.isBanned(u.getIdLong())) {
            eb.setDescription("__**USER BANNED FROM MINECORD**__");
        }
        // Since user caching is disabled, retrieveMember() is required
        // This may cause a lot of requests and lag, so it must be explicitly requested
        if (args.length > 1 && args[1].equalsIgnoreCase("mutual")) {
            String mutualGuilds = Bot.shardManager.getGuilds().stream()
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
        return new Result(Outcome.SUCCESS, MessageUtils.addFooter(eb).build());
    }

}
