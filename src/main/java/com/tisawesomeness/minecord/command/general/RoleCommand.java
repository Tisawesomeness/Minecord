package com.tisawesomeness.minecord.command.general;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.util.ColorUtils;
import com.tisawesomeness.minecord.util.DateUtils;
import com.tisawesomeness.minecord.util.DiscordUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class RoleCommand extends Command {

    public CommandInfo getInfo() {
		return new CommandInfo(
			"role",
			"Shows role info.",
			"<role>",
			new String[]{"roleinfo"},
			0,
			false,
			false,
			false
		);
    }

    public String getHelp() {
        return "Shows the info of a role in the current guild.\n" +
            "`<role>` can be a role name, a mention, or a valid ID.`\n" +
            "\n" +
            "Examples:\n" +
            "- `{&}role Moderator`\n" +
            "- `{&}role @Bot`\n" +
            "- `{&}role 347797250266628108`\n";
    }

    public String getAdminHelp() {
        return "`{&}role <role>` - Shows the info of a role in the current guild.\n" +
            "`<role>` can be a role name, a mention, or a valid ID.`\n" +
            "`{&}role <role id> admin` - Shows the info of any role.\n" +
            "\n" +
            "Examples:\n" +
            "- `{&}role Moderator`\n" +
            "- `{&}role @Bot`\n" +
            "- `{&}role 347797250266628108`\n" +
            "- `{&}role 347797250266628108 admin`\n";
    }

    public Result run(CommandContext txt) {
        String[] args = txt.args;
        MessageReceivedEvent e = txt.e;

        // Find role
        Role role = null;
        List<Role> roles = e.getGuild().getRoles();
        List<Role> mentioned = e.getMessage().getMentionedRoles();
        // Search for any role if admin
		if (args.length > 1 && args[1].equals("admin") && txt.isElevated) {
            role = txt.bot.getShardManager().getRoleById(args[0]);
        // Mentioned roles
        } else if (mentioned.size() > 0) {
            role = mentioned.get(0);
        // Search by id
        } else if (DiscordUtils.isDiscordId(args[0])) {
            role = e.getGuild().getRoleById(args[0]);
        // Search by name
        } else {
            String query = String.join(" ", args);
            for (Role r : roles) {
                if (r.getName().equalsIgnoreCase(query)) {
                    role = r;
                    break;
                }
            }
        }
        if (role == null) {
            return new Result(Outcome.WARNING, ":warning: That role does not exist.");
        }

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

        return new Result(Outcome.SUCCESS, txt.addFooter(eb).build());
    }

}