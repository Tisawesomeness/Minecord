package com.tisawesomeness.minecord.command.discord;

import com.tisawesomeness.minecord.command.SlashCommand;
import com.tisawesomeness.minecord.util.DiscordUtils;
import com.tisawesomeness.minecord.util.MessageUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;
import net.dv8tion.jda.api.utils.TimeFormat;

import java.util.concurrent.CompletableFuture;

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
        if (!e.isFromGuild()) {
            return new Result(Outcome.WARNING, ":warning: This command is not available in DMs.");
        }

        Member mem = e.getOption("user").getAsMember();
        if (mem == null) {
            return new Result(Outcome.WARNING, ":warning: That user is not in this guild.");
        }

        User u = mem.getUser();
        e.deferReply().queue();
        CompletableFuture<byte[]> avatar = DiscordUtils.retrieveImage(u.getAvatar());

        // Build role string
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

        // Generate user info
        EmbedBuilder eb = MessageUtils.addFooter(new EmbedBuilder())
                .setTitle(MarkdownSanitizer.escape(u.getEffectiveName()))
                .setColor(mem.getColor())
                .addField("ID", u.getId(), true)
                .addField("Nickname", mem.getNickname() == null ? "None" : MarkdownSanitizer.escape(mem.getNickname()), true)
                .addField("Bot?", u.isBot() ? "Yes" : "No", true)
                .addField("Joined Server", TimeFormat.RELATIVE.format(mem.getTimeJoined()), false)
                .addField("Created Account", TimeFormat.RELATIVE.format(u.getTimeCreated()), false);
        if (mem.getTimeBoosted() != null) {
            eb.addField("Boosted", TimeFormat.RELATIVE.format(mem.getTimeBoosted()), false);
        }
        eb.addField("Roles", roles.toString(), false);

        avatar.whenComplete((data, ex) -> {
            if (ex == null) {
                e.getHook().sendMessageEmbeds(eb.setImage("attachment://avatar.png").build())
                        .addFiles(FileUpload.fromData(data, "avatar.png")).queue();
            } else {
                ex.printStackTrace();
                e.getHook().sendMessageEmbeds(eb.setImage(u.getAvatarUrl()).build()).queue();
            }
        });
        return new Result(Outcome.SUCCESS);
    }

}
