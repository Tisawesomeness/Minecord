package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.LegacyCommand;
import com.tisawesomeness.minecord.command.Module;
import com.tisawesomeness.minecord.command.Registry;
import com.tisawesomeness.minecord.util.DateUtils;
import com.tisawesomeness.minecord.util.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.stream.Collectors;

public class UsageCommand extends LegacyCommand {

    public CommandInfo getInfo() {
        return new CommandInfo(
                "usage",
                "Shows how often commands are used.",
                null,
                0,
                true,
                true
        );
    }

    public Result run(String[] args, MessageReceivedEvent e) {
        // Build usage message
        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("Command usage for " + DateUtils.getUptime())
                .setColor(Bot.color);
        for (Module m : Registry.modules) {
            String field = Arrays.stream(m.getCommands())
                    .filter(c -> !c.getInfo().name.isEmpty())
                    .filter(c -> !isLegacyCommandWithSlashVariant(c))
                    .map(c -> String.format("`%s%s` **-** %d", getPrefix(c, e), c.getInfo().name, Registry.getUses(c)))
                    .collect(Collectors.joining("\n"));
            eb.addField(String.format("**%s**", m.getName()), field, true);
        }

        return new Result(Outcome.SUCCESS, MessageUtils.addFooter(eb).build());
    }

    private static boolean isLegacyCommandWithSlashVariant(Command<?> c) {
        return c instanceof LegacyCommand && Registry.getSlashCommand(c.getInfo().name).isPresent();
    }

    private static String getPrefix(Command<?> c, MessageReceivedEvent e) {
        if (c instanceof LegacyCommand) {
            return MessageUtils.getPrefix(e);
        }
        return "/";
    }

}
