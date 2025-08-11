package com.tisawesomeness.minecord.command.discord;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.SlashCommand;
import com.tisawesomeness.minecord.util.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;
import net.dv8tion.jda.api.utils.TimeFormat;

import javax.annotation.Nullable;

public class UserCommand extends SlashCommand {

    public CommandInfo getInfo() {
        return new CommandInfo(
                "user",
                "Shows user info.",
                "<user>",
                0,
                false,
                false
        );
    }

    @Override
    public SlashCommandData addCommandSyntax(SlashCommandData builder) {
        return builder.addOption(OptionType.USER, "user", "The user to look up", true);
    }

    @Override
    public String[] getLegacyAliases() {
        return new String[]{"whois", "userinfo"};
    }

    @Override
    public String getHelp() {
        return "Shows the info of a user in the current guild.\n" +
                "\n" +
                "Examples:\n" +
                "- `{&}user @Tis_awesomeness`\n";
    }

    public Result run(SlashCommandInteractionEvent e) {
        Member mem = e.getOption("user").getAsMember();
        User u;
        if (mem != null) {
            u = mem.getUser();
        } else {
            u = e.getOption("user").getAsUser();
        }

        // Build role string
        String roles = getRoleString(mem);

        // Generate user info
        EmbedBuilder eb = MessageUtils.addFooter(new EmbedBuilder())
                .setTitle(MarkdownSanitizer.escape(u.getEffectiveName()))
                .setColor(mem == null || mem.isDetached() ? Bot.color : mem.getColor())
                .setImage(u.getAvatarUrl())
                .addField("ID", u.getId(), true);
        if (mem != null) {
            eb.addField("Nickname", mem.getNickname() == null ? "None" : MarkdownSanitizer.escape(mem.getNickname()), true);
        }
        eb.addField("Bot?", u.isBot() ? "Yes" : "No", true);
        if (mem != null && mem.hasTimeJoined()) {
            eb.addField("Joined Server", TimeFormat.RELATIVE.format(mem.getTimeJoined()), false);
        }
        eb.addField("Created Account", TimeFormat.RELATIVE.format(u.getTimeCreated()), false);
        if (mem != null && mem.getTimeBoosted() != null) {
            eb.addField("Boosted", TimeFormat.RELATIVE.format(mem.getTimeBoosted()), false);
        }
        if (roles != null) {
            eb.addField("Roles", roles, false);
        }

        return new Result(Outcome.SUCCESS, eb.build());
    }

    private static @Nullable String getRoleString(@Nullable Member mem) {
        if (mem == null || mem.isDetached()) {
            return null;
        }
        StringBuilder roles = new StringBuilder();
        int c = 0;
        for (Role r : mem.getRoles()) {
            roles.append(r.getAsMention()).append("\n");
            c += 1;
            if (c == 5) {
                roles.append("...");
                break;
            }
        }
        return roles.toString();
    }

}
