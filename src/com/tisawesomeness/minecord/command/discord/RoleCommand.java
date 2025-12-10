package com.tisawesomeness.minecord.command.discord;

import com.tisawesomeness.minecord.command.OptionTypes;
import com.tisawesomeness.minecord.command.SlashCommand;
import com.tisawesomeness.minecord.util.ColorUtils;
import com.tisawesomeness.minecord.util.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.RoleIcon;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.utils.TimeFormat;

import javax.annotation.Nullable;
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
        return builder.setContexts(InteractionContextType.GUILD)
                .addOption(OptionType.ROLE, "role", "The role to look up", true);
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
        Role role = getOption(e, "role", OptionTypes.ROLE);
        if (role == null) {
            return Result.SLASH_COMMAND_FAIL;
        }
        return run(role, e.getGuild());
    }

    public static Result run(Role role, @Nullable Guild g) {
        boolean isNotDetached = g != null && !g.isDetached();
        EmbedBuilder eb = new EmbedBuilder()
                .setTitle(role.getName().substring(0, Math.min(MessageEmbed.TITLE_MAX_LENGTH, role.getName().length())))
                .setColor(role.getColorRaw())
                .addField("ID", role.getId(), true)
                .addField("Color", ColorUtils.getHexCode(role.getColorRaw()), true); // Mask gets RGB of color
        if (isNotDetached) {
            List<Role> roles = g.getRoles();
            // Position corrected so @everyone is pos 1
            eb.addField("Position", (role.getPosition() + 2) + "/" + roles.size(), true);
        } else {
            eb.addField("Role Created", TimeFormat.RELATIVE.format(role.getTimeCreated()), true);
        }
        eb.addField("Mentionable?", role.isMentionable() ? "Yes" : "No", true)
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

        if (isNotDetached) {
            eb.addField("Role Created", TimeFormat.RELATIVE.format(role.getTimeCreated()), true);
        }

        return new Result(Outcome.SUCCESS, MessageUtils.addFooter(eb).build());
    }

}
