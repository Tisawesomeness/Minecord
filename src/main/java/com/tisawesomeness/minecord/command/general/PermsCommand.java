package com.tisawesomeness.minecord.command.general;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.util.DiscordUtils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

public class PermsCommand extends Command {

    public CommandInfo getInfo() {
		return new CommandInfo(
			"perms",
			"Test the bot's permissions in a channel.",
			"[channel]",
			new String[]{"permissions"},
			0,
			false,
			false,
			false
		);
    }

    public String getHelp() {
        return "`{&}perms` - Test the bot's permissions for the current channel.\n" +
            "`{&}perms <channel>` - Test the bot's permissions for a channel in the same guild.\n" +
            "You must have permission to send messages in the channel being tested.\n" +
            "`<channel>` can be a `#channel` mention or a valid channel ID.\n" +
            "\n" +
            "Examples:\n" +
            "- `{&}perms #bot-commands`\n" +
            "- `{&}perms 347909541264097281`\n";
    }

    public String getAdminHelp() {
        return "`{&}perms` - Test the bot's permissions for the current channel.\n" +
            "`{&}perms <channel>` - Test the bot's permissions for a channel in the same guild.\n" +
            "You must have permission to send messages in the channel being tested.\n" +
            "`{&}perms <id> admin` - Test the bot's permissions for any channel.\n" +
            "`<channel>` can be a `#channel` mention or a valid channel ID.\n" +
            "\n" +
            "Examples:\n" +
            "- `{&}perms #bot-commands`\n" +
            "- `{&}perms 347909541264097281`\n" +
            "- `{&}perms 399734453712191498 admin`\n";
    }

    // Error message cannot be "you cannot see that channel"
    // since it reveals the channel exists when the user couldn't have known that otherwise
    private static final Result invalidChannel = new Result(Outcome.WARNING,
            ":warning: That channel does not exist in the current guild or is not visible to you.");

    public Result run(CommandContext txt) {
        String[] args = txt.args;
        MessageReceivedEvent e = txt.e;

        TextChannel c;
        // Check any channel id if admin
		if (args.length > 1 && args[1].equals("admin") && txt.isElevated) {
            if (!DiscordUtils.isDiscordId(args[0])) {
                return new Result(Outcome.WARNING, ":warning: Not a valid ID!");
            }
            c = txt.bot.getShardManager().getTextChannelById(args[0]);
            if (c == null) {
                return new Result(Outcome.WARNING, ":warning: That channel does not exist.");
            }
        
        // No admin = guild only
        } else if (!e.isFromGuild()) {
            return new Result(Outcome.WARNING, ":warning: This command is not available in DMs.");
        
        } else if (args.length > 0) {
            // Find by id
            if (DiscordUtils.isDiscordId(args[0])) {
                TextChannel tc = e.getGuild().getTextChannelById(args[0]);
                if (tc == null || tc.getGuild().getIdLong() != e.getGuild().getIdLong()) {
                    return invalidChannel;
                }
                c = tc;
            // Find by mention
            } else {
                List<TextChannel> mentioned = e.getMessage().getMentionedChannels();
                if (mentioned.size() == 0) {
                    return new Result(Outcome.WARNING,
                            ":warning: Not a valid channel format. Use a `#channel` mention or a valid ID.");
                }
                TextChannel tc = mentioned.get(0);
                if (tc.getGuild().getIdLong() != e.getGuild().getIdLong()) {
                    return invalidChannel;
                }
                c = tc;
            }

            // Check for user permissions (prevent using this command to get unseen channel info)
            if (!e.getMember().hasPermission(c, Permission.VIEW_CHANNEL, Permission.MESSAGE_READ)) {
                return invalidChannel;
            } else if (!e.getMember().hasPermission(c, Permission.MESSAGE_WRITE)) {
                return new Result(Outcome.WARNING, ":warning: You do not have permission to write in that channel.");
            }
        
        // Get current channel if no args, user clearly has permission to send messages
        } else {
            c = e.getTextChannel();
        }

        EnumSet<Permission> perms = c.getGuild().getSelfMember().getPermissions(c);
        boolean menuPerms = perms.containsAll(Arrays.asList(
            Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_MANAGE
        ));
        String name = c.getName() == null ? "channel" : c.getName();
        String m = String.format("**Bot Permissions for %s:**", name) +
            "\nView channels: " + getBoolEmote(perms.contains(Permission.VIEW_CHANNEL)) +
            "\nRead messages: " + getBoolEmote(perms.contains(Permission.MESSAGE_READ)) +
            "\nRead message history: " + getBoolEmote(perms.contains(Permission.MESSAGE_HISTORY)) +
            "\nWrite messages: " + getBoolEmote(perms.contains(Permission.MESSAGE_WRITE)) +
            "\nEmbed links: " + getBoolEmote(perms.contains(Permission.MESSAGE_EMBED_LINKS)) +
            "\nAdd reactions: " + getBoolEmote(perms.contains(Permission.MESSAGE_ADD_REACTION)) +
            "\nManage messages: " + getBoolEmote(perms.contains(Permission.MESSAGE_MANAGE)) +
            "\nCan use reaction menus: " + getBoolEmote(menuPerms);
        
        return new Result(Outcome.SUCCESS, m);
    }

    /**
     * Gets the emote text associated with true or false.
     */
    private static String getBoolEmote(boolean bool) {
        return bool ? ":white_check_mark:" : ":x:";
    }
    
}