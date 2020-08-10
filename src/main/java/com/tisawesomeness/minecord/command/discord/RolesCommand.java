package com.tisawesomeness.minecord.command.discord;

import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.command.Result;
import com.tisawesomeness.minecord.util.DiscordUtils;
import com.tisawesomeness.minecord.util.MessageUtils;

import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.ErrorResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RolesCommand extends AbstractDiscordCommand {

    public @NonNull String getId() {
        return "roles";
    }

    public Result run(String[] args, CommandContext ctx) {
        MessageReceivedEvent e = ctx.e;
        
        // Guild-only command
        if (!e.isFromGuild()) {
            return ctx.warn("This command is not available in DMs.");
        }

        // Check for argument length
        if (args.length == 0) {
            return ctx.showHelp();
        }
        
        // Find user
        Member mem = null;
        List<Member> mentioned = e.getMessage().getMentionedMembers();
        if (mentioned.size() > 0) {
            mem = mentioned.get(0);
        } else {
            if (DiscordUtils.isDiscordId(args[0])) {
                mem = e.getGuild().retrieveMemberById(args[0]).onErrorMap(ErrorResponse.UNKNOWN_USER::test, x -> null).complete();
                if (mem == null) {
                    return ctx.warn("That user does not exist.");
                }
            } else {
                if (!User.USER_TAG.matcher(args[0]).matches()) {
                    return ctx.warn("Not a valid user format. Use `name#1234`, a mention, or a valid ID.");
                }
                mem = e.getGuild().getMemberByTag(args[0]);
                if (mem == null) {
                    return ctx.warn("That user does not exist.");
                }
            }
        }

        ctx.triggerCooldown();
        EmbedBuilder eb = new EmbedBuilder()
            .setTitle("Roles for " + mem.getUser().getAsTag())
            .setColor(mem.getColor());

        // Truncate role list until 6000 chars reached
        ArrayList<String> lines = new ArrayList<String>(mem.getRoles().stream()
            .map(r -> r.getAsMention())
            .collect(Collectors.toList()));
        int chars = MessageUtils.getTotalChars(lines);
        boolean truncated = false;
        while (chars > 6000 - 4) {
            truncated = true;
            lines.remove(lines.size() - 1);
            chars = MessageUtils.getTotalChars(lines);
        }
        if (truncated) {
            lines.add("...");
        }

        // If over 2048, use fields, otherwise use description
        if (chars > 2048) {
            // Split into fields, avoiding 1024 field char limit
            for (String field : MessageUtils.splitLinesByLength(lines, 1024)) {
                eb.addField("Roles", field, true);
            }
        } else {
            eb.setDescription(String.join("\n", lines));
        }

        return ctx.replyRaw(ctx.addFooter(eb));
    }

}