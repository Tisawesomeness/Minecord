package com.tisawesomeness.minecord.command.utility;

import com.tisawesomeness.minecord.command.Command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ShadowCommand extends Command {

    private static final long SUM = -7379792620528906219L;

    public CommandInfo getInfo() {
        return new CommandInfo(
            "shadow",
            "Gets the shadow of a seed.",
            "<seed>",
            null,
            0,
            false,
            false,
            false
        );
    }

    public String getHelp() {
        return "`{&}shadow <text>` - Generates a seed's \"shadow\", where the biome maps are the same but everything else is different.\n" +
            "Spaces at the start and end are removed, and numbers are treated as raw numbers (same as MC 1.18.2).\n" +
            "\n" +
            "Examples:\n" +
            "- `{&}shadow Glacier`\n" +
            "- `{&}shadow zsjpxah` - converted to numeric 0\n" +
            "- `{&}shadow 0` - numeric seed 0\n";
    }

    public Result run(String[] args, MessageReceivedEvent e) {
        if (args.length == 0) {
            return new Result(Outcome.WARNING, ":warning: You must specify a seed.");
        }
        String input = String.join(" ", args);
        long shadow = shadow(stringToSeed(input));
        return new Result(Outcome.SUCCESS, String.format("Shadow Seed: `%s`", shadow));
    }

    private static long shadow(long seed) {
        return SUM - seed;
    }
    private static long stringToSeed(String input) {
        String seed = input.trim();
        try {
            return Long.parseLong(seed);
        } catch (NumberFormatException ignored) {
            return seed.hashCode();
        }
    }

}