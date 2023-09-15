package com.tisawesomeness.minecord.command.utility;

import com.tisawesomeness.minecord.command.SlashCommand;
import com.tisawesomeness.minecord.util.MathUtils;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.concurrent.ThreadLocalRandom;

public class RandomCommand extends SlashCommand {

    private static final int MIN_FORMAT_EXPONENT = -5;
    private static final int MAX_FORMAT_EXPONENT = 10;
    private static final int ROLL_FRACTION_DIGITS = 20;
    private static final int BOUNDS_FRACTION_DIGITS = 15;

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
                        .addOption(OptionType.NUMBER, "min", "Minimum value")
                        .addOption(OptionType.NUMBER, "max", "Maximum value")
        );
    }

    @Override
    public Result run(SlashCommandInteractionEvent e) {
        switch (e.getSubcommandName()) {
            case "value":
                return valueSubcommand(e);
            case "uniform":
                return uniformSubcommand(e);
            default:
                throw new RuntimeException("Invalid subcommand " + e.getSubcommandName());
        }
    }

    private static Result valueSubcommand(SlashCommandInteractionEvent e) {
        long min = e.getOption("min", (long) Integer.MIN_VALUE, OptionMapping::getAsLong);
        long max = e.getOption("max", (long) Integer.MAX_VALUE, OptionMapping::getAsLong);
        if (min > max) {
            return new Result(Outcome.WARNING, "Minimum cannot be greater than maximum");
        }
        // nextLong upper bound is exclusive, so need to assert that max+1 bound won't overflow
        assert OptionData.MAX_POSITIVE_NUMBER < Long.MAX_VALUE;

        long roll = ThreadLocalRandom.current().nextLong(min, max + 1);
        return new Result(Outcome.SUCCESS, String.format("Rolled %d (from %d to %d)", roll, min, max));
    }

    private static Result uniformSubcommand(SlashCommandInteractionEvent e) {
        double min = e.getOption("min", 0.0, OptionMapping::getAsDouble);
        String minStr = format(min, BOUNDS_FRACTION_DIGITS);
        double max = e.getOption("max", 1.0, OptionMapping::getAsDouble);
        String maxStr = format(max, BOUNDS_FRACTION_DIGITS);

        double value = ThreadLocalRandom.current().nextDouble(min, max);
        String valueStr = format(value, ROLL_FRACTION_DIGITS);
        return new Result(Outcome.SUCCESS, String.format("Rolled `%s` (uniform random, from %s to %s)",
                valueStr, minStr, maxStr));
    }
    private static String format(double d, int maxFractionDigits) {
        return MathUtils.formatHumanReadable(d, maxFractionDigits, MIN_FORMAT_EXPONENT, MAX_FORMAT_EXPONENT);
    }

}
