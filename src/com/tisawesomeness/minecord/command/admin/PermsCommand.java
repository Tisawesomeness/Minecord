package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.LegacyCommand;
import com.tisawesomeness.minecord.util.DiscordUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.EnumSet;

public class PermsCommand extends LegacyCommand {

    public CommandInfo getInfo() {
        return new CommandInfo(
                "perms",
                "Test the bot's permissions in a channel.",
                "[<channel id>]",
                0,
                true,
                true
        );
    }

    @Override
    public String getHelp() {
        return "`{&}perms <id>` - Test the bot's permissions for any channel.\n" +
                "\n" +
                "Examples:\n" +
                "- `{&}perms 347909541264097281`\n";
    }

    public Result run(String[] args, MessageReceivedEvent e) {
        if (args.length == 0) {
            return run(e.getGuildChannel());
        }
        if (!DiscordUtils.isDiscordId(args[0])) {
            return new Result(Outcome.WARNING, ":warning: Not a valid ID!");
        }
        GuildChannel c = Bot.shardManager.getTextChannelById(args[0]);
        if (c == null) {
            return new Result(Outcome.WARNING, ":warning: That channel does not exist.");
        }
        return run(c);
    }

    private static Result run(GuildChannel c) {
        EnumSet<Permission> perms = c.getGuild().getSelfMember().getPermissions(c);
        String m = String.format("**Bot Permissions for %s:**", c.getAsMention()) +
                "\nView channels: " + DiscordUtils.getBoolEmote(perms.contains(Permission.VIEW_CHANNEL)) +
                "\nSend messages: " + DiscordUtils.getBoolEmote(perms.contains(Permission.MESSAGE_SEND)) +
                "\nSend messages in threads: " + DiscordUtils.getBoolEmote(perms.contains(Permission.MESSAGE_SEND_IN_THREADS)) +
                "\nEmbed links: " + DiscordUtils.getBoolEmote(perms.contains(Permission.MESSAGE_EMBED_LINKS)) +
                "\nAttach files: " + DiscordUtils.getBoolEmote(perms.contains(Permission.MESSAGE_ATTACH_FILES)) +
                "\nManage messages (optional): " + DiscordUtils.getBoolEmote(perms.contains(Permission.MESSAGE_MANAGE));
        return new Result(Outcome.SUCCESS, m);
    }

}
