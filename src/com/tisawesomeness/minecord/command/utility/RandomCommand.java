package com.tisawesomeness.minecord.command.utility;

import com.tisawesomeness.minecord.command.SlashCommand;
import com.tisawesomeness.minecord.util.MathUtils;
import com.tisawesomeness.minecord.util.dice.DiceCombination;
import com.tisawesomeness.minecord.util.dice.DiceError;
import com.tisawesomeness.minecord.util.dice.DiceGroup;
import com.tisawesomeness.minecord.util.type.Either;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class RandomCommand extends SlashCommand {

    private static final int MIN_FORMAT_EXPONENT = -5;
    private static final int MAX_FORMAT_EXPONENT = 10;
    private static final int ROLL_FRACTION_DIGITS = 20;
    private static final int BOUNDS_FRACTION_DIGITS = 15;

    private static final BigInteger SHOW_ROLLS_LIMIT = BigInteger.valueOf(25);
    private static final int DICE_GROUP_ROLL_EXACT_LIMIT = 50;
    private static final BigInteger ROLL_EXACT_LIMIT = BigInteger.valueOf(1000);

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

    private static final String DICE_DESCRIPTION = "One or more dice in dice notation, such as `d20`, `2d6+12`, or `d6-d20`";

    @Override
    public SlashCommandData addCommandSyntax(SlashCommandData builder) {
        return builder.addSubcommands(
                new SubcommandData("value", "Generates a random value between a minimum and maximum")
                        .addOption(OptionType.INTEGER, "min", "Minimum value, inclusive")
                        .addOption(OptionType.INTEGER, "max", "Maximum value, inclusive"),
                new SubcommandData("uniform", "Generates a random decimal between a minimum and maximum")
                        .addOption(OptionType.NUMBER, "min", "Minimum value, inclusive")
                        .addOption(OptionType.NUMBER, "max", "Maximum value, exclusive"),
                new SubcommandData("dice", "Roll multiple dice and sum the result")
                        .addOptions(new OptionData(OptionType.STRING, "dice", DICE_DESCRIPTION, true)
                                .setMinLength(1))
        );
    }

    @Override
    public Result run(SlashCommandInteractionEvent e) {
        switch (e.getSubcommandName()) {
            case "value":
                return valueSubcommand(e);
            case "uniform":
                return uniformSubcommand(e);
            case "dice":
                return diceSubcommand(e);
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
        return new Result(Outcome.SUCCESS, String.format("Rolled **%d** (from %d to %d)", roll, min, max));
    }

    private static Result uniformSubcommand(SlashCommandInteractionEvent e) {
        double min = e.getOption("min", 0.0, OptionMapping::getAsDouble);
        String minStr = format(min, BOUNDS_FRACTION_DIGITS);
        double max = e.getOption("max", 1.0, OptionMapping::getAsDouble);
        String maxStr = format(max, BOUNDS_FRACTION_DIGITS);

        double value = ThreadLocalRandom.current().nextDouble(min, max);
        String valueStr = format(value, ROLL_FRACTION_DIGITS);
        return new Result(Outcome.SUCCESS, String.format("Rolled **`%s`** (uniform random, from %s to %s)",
                valueStr, minStr, maxStr));
    }
    private static String format(double d, int maxFractionDigits) {
        return MathUtils.formatHumanReadable(d, maxFractionDigits, MIN_FORMAT_EXPONENT, MAX_FORMAT_EXPONENT);
    }

    private static Result diceSubcommand(SlashCommandInteractionEvent e) {
        String diceNotation = e.getOption("dice", OptionMapping::getAsString);
        assert diceNotation != null;

        Either<DiceCombination.Error, DiceCombination> errorOrDice = DiceCombination.parse(diceNotation);
        if (errorOrDice.isLeft()) {
            String msg = handleParseError(errorOrDice.getLeft());
            return new Result(Outcome.WARNING, msg);
        }
        DiceCombination dc = errorOrDice.getRight();

        BigInteger numDice = dc.getNumberOfDice();
        if (numDice.compareTo(SHOW_ROLLS_LIMIT) <= 0) {
            String msg = buildMessageFromRollEach(dc);
            return new Result(Outcome.SUCCESS, msg);
        } else if (numDice.compareTo(ROLL_EXACT_LIMIT) <= 0) {
            long roll = dc.roll();
            return new Result(Outcome.SUCCESS, String.format("Rolled **%d**", roll));
        } else {
            long roll = dc.rollApprox(diceGroup -> diceGroup.getNumberOfDice() > DICE_GROUP_ROLL_EXACT_LIMIT);
            return new Result(Outcome.SUCCESS, String.format("Rolled **%d**", roll));
        }
    }
    private static String handleParseError(DiceCombination.Error error) {
        switch (error.getType()) {
            case DICE_ERROR:
                return String.format("`%s` could not be parsed: %s.",
                        error.getFailedDiceString(), translateDiceError(error.getDiceError()));
            case PARSE_FAILED:
                return "Could not parse dice notation.";
            case MAX_TOO_HIGH:
                return "The maximum value the dice could roll is too high.";
            case MIN_TOO_LOW:
                return "The minimum value the dice could roll is too low.";
            default:
                throw new AssertionError("unreachable");
        }
    }
    private static String translateDiceError(DiceError error) {
        switch (error) {
            case DICE_INVALID:
                return "the number of dice is not a number";
            case DICE_ZERO:
                return "the number of dice cannot be 0";
            case FACES_INVALID:
                return "the number of faces is not a number";
            case FACES_NOT_POSITIVE:
                return "the number of faces must be positive";
            case NO_DELIMITER:
                return "missing `d` separator";
            case MAX_TOO_HIGH:
                return "the maximum value the dice could roll is too high";
            case MIN_TOO_LOW:
                return "the minimum value the dice could roll is too low";
            default:
                throw new AssertionError("unreachable");
        }
    }

    private static String buildMessageFromRollEach(DiceCombination dc) {
        List<Map<Long, Long>> rollCountsByGroup = dc.rollEach();

        if (rollCountsByGroup.size() == 1) {
            Map<Long, Long> rollCounts = rollCountsByGroup.get(0);
            if (rollCounts.size() == 1) {
                long roll = rollCounts.entrySet().iterator().next().getKey();
                return String.format("Rolled **%d**", roll);
            }
            long roll = sumRolls(rollCounts);
            String rolledValuesStr = buildRolledValuesString(rollCounts);
            return String.format("Rolled **%d**: `[%s]`", roll, rolledValuesStr);
        }

        List<DiceGroup> dice = dc.getDice();
        StringJoiner diceGroupJoiner = new StringJoiner("\n");
        long total = 0;
        for (int i = 0; i < rollCountsByGroup.size(); i++) {
            DiceGroup diceGroup = dice.get(i);
            Map<Long, Long> rollCounts = rollCountsByGroup.get(i);
            total += sumRolls(rollCounts);
            String rolledValuesStr = buildRolledValuesString(rollCounts);
            diceGroupJoiner.add(String.format("%s: `[%s]`", diceGroup, rolledValuesStr));
        }
        return String.format("Rolled **%d**\n%s", total, diceGroupJoiner);
    }
    private static long sumRolls(Map<Long, Long> rollCounts) {
        return rollCounts.entrySet().stream()
                .mapToLong(en -> en.getKey() * en.getValue())
                .reduce(Long::sum)
                .orElse(0);
    }
    private static String buildRolledValuesString(Map<Long, Long> rollCounts) {
        // returned map may have patterns, simulate random order by shuffling
        List<Long> rolledValues = MathUtils.shuffle(rollCounts.keySet());
        return rolledValues.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
    }

}
