package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.LegacyCommand;
import com.tisawesomeness.minecord.debug.*;
import com.tisawesomeness.minecord.debug.cache.CooldownCacheDebugOption;
import com.tisawesomeness.minecord.debug.cache.PlayerCacheDebugOption;
import com.tisawesomeness.minecord.debug.cache.StatusCacheDebugOption;
import com.tisawesomeness.minecord.debug.cache.UuidCacheDebugOption;
import com.tisawesomeness.minecord.mc.external.PlayerProvider;
import com.tisawesomeness.minecord.util.StringUtils;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DebugCommand extends LegacyCommand {

    @Override
    public CommandInfo getInfo() {
        return new CommandInfo(
                "debug",
                "Prints out debug info.",
                "[<option>|all]",
                0,
                true,
                true
        );
    }

    @Override
    public Result run(String[] args, MessageReceivedEvent e) throws Exception {
        List<DebugOption> debugOptions = buildDebugOptionList();
        if (args.length == 0) {
            String possibleOptions = debugOptions.stream()
                    .map(d -> MarkdownUtil.monospace(d.getName()))
                    .collect(Collectors.joining(", "));
            return new Result(Outcome.SUCCESS, "Possible options: " + possibleOptions);
        }

        if ("all".equalsIgnoreCase(args[0])) {
            for (DebugOption d : debugOptions) {
                sendDebugInfo(args, e, d);
            }
            return new Result(Outcome.SUCCESS);
        }

        for (DebugOption d : debugOptions) {
            if (d.getName().equalsIgnoreCase(args[0])) {
                sendDebugInfo(args, e, d);
                return new Result(Outcome.SUCCESS);
            }
        }
        return new Result(Outcome.WARNING, "Not a valid debug option.");
    }

    private static List<DebugOption> buildDebugOptionList() {
        PlayerProvider playerProvider = Bot.mcLibrary.getPlayerProvider();
        return Arrays.asList(
                new JDADebugOption(Bot.shardManager),
                new ThreadDebugOption(),
                new ItemDebugOption(),
                new ClientDebugOption(Bot.apiClient),
                new CooldownCacheDebugOption(),
                new UuidCacheDebugOption(playerProvider),
                new PlayerCacheDebugOption(playerProvider),
                new StatusCacheDebugOption(playerProvider)
        );
    }

    private void sendDebugInfo(String[] args, MessageReceivedEvent e, DebugOption d) {
        String extra = args.length > 1 ? args[1] : "";
        String debugInfo = d.debug(extra);
        printToConsole(debugInfo, e.getAuthor());
        List<String> messages = StringUtils.splitLinesByLength(debugInfo, Message.MAX_CONTENT_LENGTH);
        for (String message : messages) {
            e.getChannel().sendMessage(message).queue();
        }
    }

    private static void printToConsole(String debugInfo, User author) {
        String requestedBy = String.format("Requested By %s (%s)%n", author.getEffectiveName(), author.getId());
        System.out.println("\n" + requestedBy + MarkdownSanitizer.sanitize(debugInfo) + "\n");
    }

}
