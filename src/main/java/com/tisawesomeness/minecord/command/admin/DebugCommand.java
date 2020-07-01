package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.database.Database;
import com.tisawesomeness.minecord.debug.ChannelCacheDebugOption;
import com.tisawesomeness.minecord.debug.DebugOption;
import com.tisawesomeness.minecord.debug.GuildCacheDebugOption;
import com.tisawesomeness.minecord.debug.JDADebugOption;
import com.tisawesomeness.minecord.debug.ThreadDebugOption;
import com.tisawesomeness.minecord.debug.UserCacheDebugOption;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DebugCommand extends Command {
    public CommandInfo getInfo() {
        return new CommandInfo(
            "debug",
            "Prints out debug info.",
            "[option]",
            new String[]{"dump"},
            0,
            true,
            true,
            false
        );
    }

    private final List<DebugOption> debugOptions;
    public DebugCommand(ShardManager sm, Database db) {
        debugOptions = Arrays.asList(
                new JDADebugOption(sm),
                new ThreadDebugOption(),
                new GuildCacheDebugOption(db),
                new ChannelCacheDebugOption(db),
                new UserCacheDebugOption(db)
        );
    }

    public Result run(CommandContext txt) {
        if (!txt.config.debugMode) {
            return new Result(Outcome.WARNING, "The bot is not in debug mode.");
        }

        if (txt.args.length == 0) {
            String possibleOptions = debugOptions.stream()
                    .map(d -> MarkdownUtil.monospace(d.getName()))
                    .collect(Collectors.joining(", "));
            return new Result(Outcome.SUCCESS, "Possible options: " + possibleOptions);
        }

        if (txt.args[0].equalsIgnoreCase("all")) {
            for (DebugOption d : debugOptions) {
                String debugInfo = d.debug();
                printToConsole(debugInfo, txt.e.getAuthor());
                txt.e.getChannel().sendMessage(debugInfo).queue();
            }
            return new Result(Outcome.SUCCESS);
        }

        for (DebugOption d : debugOptions) {
            if (d.getName().equalsIgnoreCase(txt.args[0])) {
                String debugInfo = d.debug();
                printToConsole(debugInfo, txt.e.getAuthor());
                return new Result(Outcome.SUCCESS, debugInfo);
            }
        }
        return new Result(Outcome.WARNING, ":warning: Not a valid debug option.");
    }

    private static void printToConsole(String debugInfo, User author) {
        String requestedBy = String.format("Requested By %s (%s)\n", author.getAsTag(), author.getId());
        System.out.println("\n" + requestedBy + debugInfo + "\n");
    }
}
