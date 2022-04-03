package com.tisawesomeness.minecord.command.discord;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.util.DiscordUtils;
import com.tisawesomeness.minecord.util.MessageUtils;
import com.tisawesomeness.minecord.util.StringUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.ErrorResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RolesCommand extends Command {

    public CommandInfo getInfo() {
        return new CommandInfo(
                "roles",
                "List a user's roles.",
                "<user|id>",
                null,
                0,
                false,
                false,
                false
        );
    }

    public String getHelp() {
        return "List the roles of a user in the current guild.\n" +
                "\n" +
                "Examples:\n" +
                "- `{&}roles @Tis_awesomeness`\n" +
                "- `{&}roles 211261249386708992`\n";
    }

    public Result run(String[] args, MessageReceivedEvent e) {

        // Guild-only command
        if (!e.isFromGuild()) {
            return new Result(Outcome.WARNING, ":warning: This command is not available in DMs.");
        }

        // Check for argument length
        if (args.length == 0) {
            return new Result(Outcome.WARNING, ":warning: You must specify a user!");
        }

        // Find user
        Member mem;
        List<Member> mentioned = e.getMessage().getMentionedMembers();
        if (mentioned.size() > 0) {
            mem = mentioned.get(0);
        } else {
            if (DiscordUtils.isDiscordId(args[0])) {
                mem = e.getGuild().retrieveMemberById(args[0]).onErrorMap(ErrorResponse.UNKNOWN_MEMBER::test, x -> null).complete();
            } else {
                if (!User.USER_TAG.matcher(args[0]).matches()) {
                    return new Result(Outcome.WARNING, ":warning: Not a valid user format. Use `name#1234`, a mention, or a user ID.");
                }
                mem = e.getGuild().getMemberByTag(args[0]);
            }
            if (mem == null) {
                return new Result(Outcome.WARNING, ":warning: That user does not exist.");
            }
        }

        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("Roles for " + mem.getUser().getAsTag())
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
