package com.tisawesomeness.minecord.command.discord;

import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.command.Result;
import com.tisawesomeness.minecord.util.BooleanUtils;
import com.tisawesomeness.minecord.util.DiscordUtils;

import lombok.NonNull;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

public class PermsCommand extends AbstractDiscordCommand {

    public @NonNull String getId() {
        return "perms";
    }

    // Error message cannot be "you cannot see that channel"
    // since it reveals the channel exists when the user couldn't have known that otherwise
    private static final String invalidChannel =
            "That channel does not exist in the current guild or is not visible to you.";

    public Result run(String[] args, CommandContext ctx) {
        MessageReceivedEvent e = ctx.e;

        TextChannel c;
        // Check any channel id if admin
        if (args.length > 1 && args[1].equals("admin") && ctx.isElevated) {
            if (!DiscordUtils.isDiscordId(args[0])) {
                return ctx.warn("Not a valid ID!");
            }
            c = ctx.bot.getShardManager().getTextChannelById(args[0]);
            if (c == null) {
                return ctx.warn("That channel does not exist.");
            }
        
        // No admin = guild only
        } else if (!e.isFromGuild()) {
            return ctx.warn("This command is not available in DMs.");
        
        } else if (args.length > 0) {
            // Find by id
            if (DiscordUtils.isDiscordId(args[0])) {
                TextChannel tc = e.getGuild().getTextChannelById(args[0]);
                if (tc == null || tc.getGuild().getIdLong() != e.getGuild().getIdLong()) {
                    return ctx.warn(invalidChannel);
                }
                c = tc;
            // Find by mention
            } else {
                List<TextChannel> mentioned = e.getMessage().getMentionedChannels();
                if (mentioned.size() == 0) {
                    return ctx.warn("Not a valid channel format. Use a `#channel` mention or a valid ID.");
                }
                TextChannel tc = mentioned.get(0);
                if (tc.getGuild().getIdLong() != e.getGuild().getIdLong()) {
                    return ctx.warn(invalidChannel);
                }
                c = tc;
            }

            // Check for user permissions (prevent using this command to get unseen channel info)
            if (!e.getMember().hasPermission(c, Permission.VIEW_CHANNEL, Permission.MESSAGE_READ)) {
                return ctx.warn(invalidChannel);
            } else if (!e.getMember().hasPermission(c, Permission.MESSAGE_WRITE)) {
                return ctx.warn("You do not have permission to write in that channel.");
            }
        
        // Get current channel if no args, user clearly has permission to send messages
        } else {
            c = e.getTextChannel();
        }

        ctx.triggerCooldown();
        EnumSet<Permission> perms = c.getGuild().getSelfMember().getPermissions(c);
        boolean menuPerms = perms.containsAll(Arrays.asList(
            Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_MANAGE
        ));
        String name = c.getName() == null ? "channel" : c.getName();
        String m = String.format("**Bot Permissions for %s:**", name) +
            "\nView channels: " + BooleanUtils.getEmote(perms.contains(Permission.VIEW_CHANNEL)) +
            "\nRead messages: " + BooleanUtils.getEmote(perms.contains(Permission.MESSAGE_READ)) +
            "\nRead message history: " + BooleanUtils.getEmote(perms.contains(Permission.MESSAGE_HISTORY)) +
            "\nWrite messages: " + BooleanUtils.getEmote(perms.contains(Permission.MESSAGE_WRITE)) +
            "\nEmbed links: " + BooleanUtils.getEmote(perms.contains(Permission.MESSAGE_EMBED_LINKS)) +
            "\nAdd reactions: " + BooleanUtils.getEmote(perms.contains(Permission.MESSAGE_ADD_REACTION)) +
            "\nManage messages: " + BooleanUtils.getEmote(perms.contains(Permission.MESSAGE_MANAGE)) +
            "\nCan use reaction menus: " + BooleanUtils.getEmote(menuPerms);
        
        return ctx.reply(m);
    }

}