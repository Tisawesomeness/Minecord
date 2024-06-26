package com.tisawesomeness.minecord.command.discord;

import com.tisawesomeness.minecord.command.SlashCommand;
import com.tisawesomeness.minecord.util.ColorUtils;
import com.tisawesomeness.minecord.util.MessageUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.RoleIcon;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.utils.TimeFormat;

import java.util.List;

public class RoleCommand extends SlashCommand {

    public CommandInfo getInfo() {
        return new CommandInfo(
                "role",
                "Shows role info.",
                "<role>",
                0,
                false,
                false
        );
    }

    @Override
    public SlashCommandData addCommandSyntax(SlashCommandData builder) {
        return builder.addOption(OptionType.ROLE, "role", "The role to look up", true);
    }

    @Override
    public String[] getLegacyAliases() {
        return new String[]{"roleinfo"};
    }

    @Override
    public String getHelp() {
        return "Shows the info of a role in the current guild.\n" +
                "\n" +
                "Examples:\n" +
                "- `{&}role Moderator`\n";
    }

    public Result run(SlashCommandInteractionEvent e) {
        if (!e.isFromGuild()) {
            return new Result(Outcome.WARNING, ":warning: This command is not available in DMs.");
        }
        Role role = e.getOption("role").getAsRole();
        return run(role, e.getGuild());
    }

    public static Result run(Role role, Guild g) {
        List<Role> roles = g.getRoles();
        EmbedBuilder eb = new EmbedBuilder()
                .setTitle(role.getName().substring(0, Math.min(MessageEmbed.TITLE_MAX_LENGTH, role.getName().length())))
                .setColor(role.getColorRaw())
                .addField("ID", role.getId(), true)
                .addField("Color", ColorUtils.getHexCode(role.getColorRaw()), true) // Mask gets RGB of color
                .addField("Position", (role.getPosition() + 2) + "/" + roles.size(), true) // Position corrected so @everyone is pos 1
                .addField("Mentionable?", role.isMentionable() ? "Yes" : "No", true)
                .addField("Hoisted?", role.isHoisted() ? "Yes" : "No", true)
                .addField("Managed?", role.isManaged() ? "Yes" : "No", true);

        RoleIcon icon = role.getIcon();
        if (icon != null) {
            if (icon.isEmoji()) {
                eb.addField("Role Icon Emoji", icon.getEmoji(), true);
            } else {
                eb.setThumbnail(icon.getIconUrl());
            }
        }

        eb.addField("Role Created", TimeFormat.RELATIVE.format(role.getTimeCreated()), false);

        return new Result(Outcome.SUCCESS, MessageUtils.addFooter(eb).build());
    }

}
