package com.tisawesomeness.minecord.command.admin;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.database.Database;
import com.tisawesomeness.minecord.debug.DebugOption;
import com.tisawesomeness.minecord.debug.GuildCacheDebugOption;
import com.tisawesomeness.minecord.debug.JDADebugOption;
import com.tisawesomeness.minecord.debug.ThreadDebugOption;
import com.tisawesomeness.minecord.debug.UserCacheDebugOption;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.sharding.ShardManager;

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
                new UserCacheDebugOption(db)
        );
    }

    public Result run(CommandContext txt) {
        if (!txt.config.debugMode) {
            return new Result(Outcome.WARNING, "The bot is not in debug mode.");
        }

        if (txt.args.length == 0) {
            String possibleOptions = debugOptions.stream()
                    .map(d -> String.format("`%s`", d.getName()))
                    .collect(Collectors.joining(", "));
            return new Result(Outcome.SUCCESS, "Possible options: " + possibleOptions);
        }

        for (DebugOption d : debugOptions) {
            if (d.getName().equalsIgnoreCase(txt.args[0])) {
                String debugInfo = d.debug();
                User author = txt.e.getAuthor();
                String requestedBy = String.format("Requested By %s (%s)\n", author.getAsTag(), author.getId());
                System.out.println("\n" + requestedBy + debugInfo + "\n"); // Useful to have in console
                return new Result(Outcome.SUCCESS, debugInfo);
            }
        }
        return new Result(Outcome.WARNING, ":warning: Not a valid debug option.");
    }
}
