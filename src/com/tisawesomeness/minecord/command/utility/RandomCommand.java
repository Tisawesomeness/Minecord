package com.tisawesomeness.minecord.command.utility;

import com.tisawesomeness.minecord.command.SlashCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.concurrent.ThreadLocalRandom;

public class RandomCommand extends SlashCommand {

    @Override
    public CommandInfo getInfo() {
        return new CommandInfo(
                "random",
                "Looks up the recipes containing an ingredient.",
                "<type> <arguments...>",
                0,
                false,
                false
        );
    }

    @Override
    public SlashCommandData addCommandSyntax(SlashCommandData builder) {
        return builder.addSubcommands(
                new SubcommandData("value", "Generates a random value between a minimum and maximum")
                        .addOption(OptionType.INTEGER, "min", "Minimum value")
                        .addOption(OptionType.INTEGER, "max", "Maximum value"),
                new SubcommandData("uniform", "Generates a random decimal between 0 and 1")
        );
    }

    @Override
    public Result run(SlashCommandInteractionEvent e) {
        switch (e.getSubcommandName()) {
            case "value":
                long min = e.getOption("min", (long) Integer.MIN_VALUE, OptionMapping::getAsLong);
                long max = e.getOption("max", (long) Integer.MAX_VALUE, OptionMapping::getAsLong);
                return valueSubcommand(min, max);
            case "uniform":
                return uniformSubcommand();
            default:
                throw new RuntimeException("Invalid subcommand " + e.getSubcommandName());
        }
    }

    private static Result valueSubcommand(long min, long max) {
        if (min > max) {
            return new Result(Outcome.WARNING, "Minimum cannot be greater than maximum");
        }
        // nextLong upper bound is exclusive, so need to assert that max+1 bound won't overflow
        assert OptionData.MAX_POSITIVE_NUMBER < Long.MAX_VALUE;
        long roll = ThreadLocalRandom.current().nextLong(min, max + 1);
        return new Result(Outcome.SUCCESS, String.format("Rolled %d (from %d to %d)", roll, min, max));
    }

    private static Result uniformSubcommand() {
        double value = ThreadLocalRandom.current().nextDouble();
        return new Result(Outcome.SUCCESS, String.format("Rolled `%f` (uniform random, between 0 and 1)", value));
    }

}
