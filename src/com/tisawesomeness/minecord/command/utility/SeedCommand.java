package com.tisawesomeness.minecord.command.utility;

import com.tisawesomeness.minecord.command.Command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SeedCommand extends Command {

    public CommandInfo getInfo() {
        return new CommandInfo(
            "seed",
            "Converts some text to a seed number.",
            "<text>",
            null,
            0,
            false,
            false,
            true
        );
    }

    public String getHelp() {
        return "`{&}seed <text>` - Converts some text to a seed number.\n" +
            "Spaces at the start and end are removed, and numbers are treated as strings.\n" +
            "\n" +
            "Examples:\n" +
            "- `{&}seed Glacier`\n" +
            "- `{&}seed zsjpxah` - numeric seed 0\n" +
            "- `{&}seed 0` - treated as a string\n";
    }

    @Override
    public Result run(String[] args, MessageReceivedEvent e) throws Exception {
        if (args.length == 0) {
            return new Result(Outcome.WARNING, ":warning: You must specify a seed.");
        }
        long seed = String.join(" ", args).hashCode();
        return new Result(Outcome.SUCCESS, String.format("Seed: `%d`", seed));
    }

}
