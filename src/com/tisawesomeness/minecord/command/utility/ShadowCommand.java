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
        return "Generates a seed's \"shadow\", where the biome maps are the same but everything else is different.\n";
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
    private static long stringToSeed(String seed) {
        if (seed.matches("-?\\d{1,20}")) {
            return Long.parseLong(seed);
        }
        return seed.hashCode();
    }

}