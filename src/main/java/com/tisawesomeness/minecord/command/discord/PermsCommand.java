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

    public void run(String[] args, CommandContext ctx) {
        MessageReceivedEvent e = ctx.getE();

        TextChannel c;
        // Check any channel id if admin
        if (args.length > 1 && args[1].equals("admin") && ctx.isElevated()) {
            if (!DiscordUtils.isDiscordId(args[0])) {
                ctx.invalidArgs("Not a valid ID!");
                return;
            }
            c = ctx.getBot().getShardManager().getTextChannelById(args[0]);
            if (c == null) {
                ctx.warn("That channel does not exist.");
                return;
            }
        
        // No admin = guild only
        } else if (!e.isFromGuild()) {
            ctx.guildOnly("This command is not available in DMs.");
            return;
        
        } else if (args.length > 0) {
            // Find by id
            if (DiscordUtils.isDiscordId(args[0])) {
                TextChannel tc = e.getGuild().getTextChannelById(args[0]);
                if (tc == null || tc.getGuild().getIdLong() != e.getGuild().getIdLong()) {
                    ctx.warn(invalidChannel);
                    return;
                }
                c = tc;
            // Find by mention
            } else {
                List<TextChannel> mentioned = e.getMessage().getMentionedChannels();
                if (mentioned.size() == 0) {
                    ctx.invalidArgs("Not a valid channel format. Use a `#channel` mention or a valid ID.");
                    return;
                }
                TextChannel tc = mentioned.get(0);
                if (tc.getGuild().getIdLong() != e.getGuild().getIdLong()) {
                    ctx.warn(invalidChannel);
                    return;
                }
                c = tc;
            }

            // Check for user permissions (prevent using this command to get unseen channel info)
            if (!e.getMember().hasPermission(c, Permission.VIEW_CHANNEL, Permission.MESSAGE_READ)) {
                ctx.warn(invalidChannel);// Lying about the result lol
                ctx.commandResult(Result.NO_USER_PERMISSIONS);
                return;
            } else if (!e.getMember().hasPermission(c, Permission.MESSAGE_WRITE)) {
                ctx.noUserPermissions("You do not have permission to write in that channel.");
                return;
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
        
        ctx.reply(m);
    }

}