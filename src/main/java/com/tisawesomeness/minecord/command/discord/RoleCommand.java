package com.tisawesomeness.minecord.command.discord;

import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.command.Result;
import com.tisawesomeness.minecord.util.ColorUtils;
import com.tisawesomeness.minecord.util.DateUtils;
import com.tisawesomeness.minecord.util.DiscordUtils;

import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class RoleCommand extends AbstractDiscordCommand {

    public @NonNull String getId() {
        return "role";
    }

    public Result run(String[] args, CommandContext ctx) {
        MessageReceivedEvent e = ctx.e;

        if (args.length == 0) {
            return ctx.showHelp();
        }

        // Find role
        Role role = null;
        List<Role> roles = e.getGuild().getRoles();
        List<Role> mentioned = e.getMessage().getMentionedRoles();
        // Search for any role if admin
        if (args.length > 1 && args[1].equals("admin") && ctx.isElevated) {
            role = ctx.bot.getShardManager().getRoleById(args[0]);
        } else if (!e.isFromGuild()) {
            return ctx.warn("This command is not available in DMs.");
        // Mentioned roles
        } else if (mentioned.size() > 0) {
            role = mentioned.get(0);
        // Search by id
        } else if (DiscordUtils.isDiscordId(args[0])) {
            role = e.getGuild().getRoleById(args[0]);
        // Search by name
        } else {
            String query = ctx.joinArgs();
            for (Role r : roles) {
                if (r.getName().equalsIgnoreCase(query)) {
                    role = r;
                    break;
                }
            }
        }
        if (role == null) {
            return ctx.invalidArgs("That role does not exist.");
        }

        ctx.triggerCooldown();
        EmbedBuilder eb = new EmbedBuilder()
            .setTitle(role.getName().substring(0, Math.min(MessageEmbed.TITLE_MAX_LENGTH, role.getName().length())))
            .setColor(role.getColorRaw())
            .addField("ID", role.getId(), true)
            .addField("Color", ColorUtils.getHexCode(role.getColorRaw()), true)
            .addField("Position", (role.getPosition() + 2) + "/" + roles.size(), true) // Position corrected so @everyone is pos 1
            .addField("Mentionable?", role.isMentionable() ? "Yes" : "No", true)
            .addField("Hoisted?", role.isHoisted() ? "Yes" : "No", true)
            .addField("Managed?", role.isManaged() ? "Yes" : "No", true)
            .addField("Role Created", DateUtils.getDateAgo(role.getTimeCreated()), false);

        return ctx.replyRaw(ctx.addFooter(eb));
    }

}