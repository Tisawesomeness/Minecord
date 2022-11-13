package com.tisawesomeness.minecord.command.discord;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.LegacyCommand;
import com.tisawesomeness.minecord.database.Database;
import com.tisawesomeness.minecord.util.DiscordUtils;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

public class GuildCommandAdmin extends LegacyCommand {

    public CommandInfo getInfo() {
        return new CommandInfo(
                "guildadmin",
                "Shows guild info.",
                "<guild id>",
                0,
                true,
                true
        );
    }

    @Override
    public String getHelp() {
        return "`{&}guildadmin <guild id>` - Shows the info of another guild.\n" +
                "\n" +
                "Examples:\n" +
                "- `{&}guild 347765748577468416`\n";
    }

    public Result run(String[] args, MessageReceivedEvent e) throws Exception {
        if (args.length == 0) {
            return new Result(Outcome.WARNING, ":warning: You need to specify a guild id.");
        }
        if (!DiscordUtils.isDiscordId(args[0])) {
            return new Result(Outcome.WARNING, ":warning: Not a valid ID!");
        }
        Guild g = Bot.shardManager.getGuildById(args[0]);
        if (g == null) {
            long gid = Long.parseLong(args[0]);
            if (Database.isBanned(gid)) {
                return new Result(Outcome.SUCCESS, "__**GUILD BANNED FROM MINECORD**__\n" + GuildCommand.getSettingsStr(gid));
            }
            return new Result(Outcome.SUCCESS, GuildCommand.getSettingsStr(gid));
        }
        GuildCommand.buildReply(g, true)
                .thenAccept(emb -> sendSuccess(e, MessageCreateData.fromEmbeds(emb)));
        return new Result(Outcome.SUCCESS);
    }

}
