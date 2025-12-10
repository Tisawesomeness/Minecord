package com.tisawesomeness.minecord.command.discord;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.OptionTypes;
import com.tisawesomeness.minecord.command.SlashCommand;
import com.tisawesomeness.minecord.util.MessageUtils;
import com.tisawesomeness.minecord.util.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.IntegrationType;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class RolesCommand extends SlashCommand {

    public CommandInfo getInfo() {
        return new CommandInfo(
                "roles",
                "List a user's roles.",
                "<user>",
                0,
                false,
                false
        );
    }

    @Override
    public SlashCommandData addCommandSyntax(SlashCommandData builder) {
        return builder.setIntegrationTypes(IntegrationType.GUILD_INSTALL)
                .setContexts(InteractionContextType.GUILD)
                .addOption(OptionType.USER, "user", "The user to list roles for", true);
    }

    @Override
    public String getHelp() {
        return "List the roles of a user in the current guild.\n" +
                "\n" +
                "Examples:\n" +
                "- `{&}roles @Tis_awesomeness`\n";
    }

    public Result run(SlashCommandInteractionEvent e) {
        // Find user
        Member mem = getOption(e, "user", OptionTypes.MEMBER);
        if (mem == null) {
            User user = getOption(e, "user", OptionTypes.USER);
            if (user == null) {
                return Result.SLASH_COMMAND_FAIL;
            } else {
                return new Result(Outcome.WARNING, ":warning: That user is not in this guild.");
            }
        }

        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("Roles for " + mem.getUser().getEffectiveName())
                .setColor(Bot.color);

        // Truncate role list until 6000 chars reached
        ArrayList<String> lines = mem.getRoles().stream()
                .map(IMentionable::getAsMention)
                .collect(Collectors.toCollection(ArrayList::new));
        int chars = StringUtils.getTotalChars(lines);
        boolean truncated = false;
        while (chars > 6000 - 4) {
            truncated = true;
            lines.remove(lines.size() - 1);
            chars = StringUtils.getTotalChars(lines);
        }
        if (truncated) {
            lines.add("...");
        }

        // If over 2048, use fields, otherwise use description
        if (chars > 2048) {
            // Split into fields, avoiding 1024 field char limit
            for (String field : StringUtils.splitLinesByLength(lines, 1024)) {
                eb.addField("Roles", field, true);
            }
        } else {
            eb.setDescription(String.join("\n", lines));
        }

        return new Result(Outcome.SUCCESS, MessageUtils.addFooter(eb).build());
    }

}
