package com.tisawesomeness.minecord.command.utility;

import com.tisawesomeness.minecord.command.SlashCommand;
import com.tisawesomeness.minecord.command.player.UuidCommand;
import com.tisawesomeness.minecord.util.ColorUtils;
import com.tisawesomeness.minecord.util.MathUtils;
import com.tisawesomeness.minecord.util.MessageUtils;
import com.tisawesomeness.minecord.util.UuidUtils;
import com.tisawesomeness.minecord.util.dice.DiceCombination;
import com.tisawesomeness.minecord.util.dice.DiceError;
import com.tisawesomeness.minecord.util.dice.DiceGroup;
import com.tisawesomeness.minecord.util.type.Either;
import com.tisawesomeness.minecord.util.type.HumanDecimalFormat;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.awt.*;
import java.math.BigInteger;
import java.util.List;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RandomCommand extends SlashCommand {

    private static final HumanDecimalFormat ROLL_FORMAT = HumanDecimalFormat.builder()
            .minimumExponentForExactValues(-5)
            .maximumExponentForExactValues(10)
            .minimumFractionDigits(1)
            .maximumFractionDigits(20)
            .build();
    private static final HumanDecimalFormat BOUNDS_FORMAT = HumanDecimalFormat.builder()
            .minimumExponentForExactValues(-5)
            .maximumExponentForExactValues(10)
            .minimumFractionDigits(1)
            .maximumFractionDigits(10)
            .build();

    private static final Pattern COMMA_PATTERN = Pattern.compile(",");

    private static final BigInteger SHOW_ROLLS_LIMIT = BigInteger.valueOf(25);
    private static final int DICE_GROUP_ROLL_EXACT_LIMIT = 50;
    private static final BigInteger ROLL_EXACT_LIMIT = BigInteger.valueOf(1000);

    @Override
    public CommandInfo getInfo() {
        return new CommandInfo(
                "random",
                "Generate random numbers.",
                "<type> [<arguments...>]",
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
                new SubcommandData("uuid", "Generates a random UUID"),
                new SubcommandData("choose", "Chooses randomly from a list")
                        .addOptions(new OptionData(OptionType.STRING, "choices", "List of choices, separated by comma `,`", true)
                                .setMinLength(1)),
                new SubcommandData("dice", "Roll multiple dice and sum the result")
                        .addOptions(new OptionData(OptionType.STRING, "dice", DICE_DESCRIPTION, true)
                                .setMinLength(1)),
                new SubcommandData("color", "Generates a random color")
                        .addOptions(new OptionData(OptionType.INTEGER, "type", "desc", true)
                                .addChoice("minecraft", ColorType.MINECRAFT.id)
                                .addChoice("hex", ColorType.HEX.id))
        );
    }

    @Override
    public Result run(SlashCommandInteractionEvent e) {
        switch (e.getSubcommandName()) {
            case "value":
                return valueSubcommand(e);
            case "uniform":
                return uniformSubcommand(e);
            case "uuid":
                return uuidSubcommand();
            case "choose":
                return chooseSubcommand(e);
            case "dice":
                return diceSubcommand(e);
            case "color":
                return colorSubcommand(e);
            default:
                throw new RuntimeException("Invalid subcommand " + e.getSubcommandName());
        }
    }

    private static Result valueSubcommand(SlashCommandInteractionEvent e) {
        long min = e.getOption("min", (long) Integer.MIN_VALUE, OptionMapping::getAsLong);
        long max = e.getOption("max", (long) Integer.MAX_VALUE, OptionMapping::getAsLong);
        if (min > max) {
            return new Result(Outcome.WARNING, String.format("Minimum (%d) cannot be greater than maximum (%d)", min, max));
        }
        // nextLong upper bound is exclusive, so need to assert that max+1 bound won't overflow
        assert OptionData.MAX_POSITIVE_NUMBER < Long.MAX_VALUE;

        long roll = ThreadLocalRandom.current().nextLong(min, max + 1);
        return new Result(Outcome.SUCCESS, String.format("Rolled **%d** (from %d to %d)", roll, min, max));
    }

    private static Result uniformSubcommand(SlashCommandInteractionEvent e) {
        double min = e.getOption("min", 0.0, OptionMapping::getAsDouble);
        String minStr = BOUNDS_FORMAT.format(min);
        double max = e.getOption("max", 1.0, OptionMapping::getAsDouble);
        String maxStr = BOUNDS_FORMAT.format(max);
        if (min >= max) {
            return new Result(Outcome.WARNING, String.format("Minimum (%s) cannot be greater than or equal to maximum (%s)", minStr, maxStr));
        }

        double value = ThreadLocalRandom.current().nextDouble(min, max);
        String valueStr = ROLL_FORMAT.format(value);
        return new Result(Outcome.SUCCESS, String.format("Rolled **`%s`** (uniform random, from %s to %s)",
                valueStr, minStr, maxStr));
    }

    private static Result uuidSubcommand() {
        return new Result(Outcome.SUCCESS, UuidCommand.constructDescription(UuidUtils.randomUuidInsecure()));
    }

    private static Result chooseSubcommand(SlashCommandInteractionEvent e) {
        String choices = e.getOption("choices", OptionMapping::getAsString);
        assert choices != null;

        String trimmed = trimLeadingCommas(choices); // split() ignores trailing commas, ignore leading to be consistent
        if (trimmed.isEmpty()) {
            return new Result(Outcome.WARNING, "Must specify at least one choice.");
        }
        String choice = MathUtils.choose(COMMA_PATTERN.split(trimmed));
        if (choice.isEmpty()) {
            choice = "(empty)";
        }
        return new Result(Outcome.SUCCESS, "Chose: " + choice);
    }
    private static String trimLeadingCommas(String s) {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c != ',') {
                return s.substring(i);
            }
        }
        return "";
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
        if (numDice.equals(BigInteger.ZERO)) {
            return new Result(Outcome.WARNING, "No dice specified (example: `d6`)");
        } else if (numDice.compareTo(SHOW_ROLLS_LIMIT) <= 0) {
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

        if (rollCountsByGroup.size() == 1 && dc.getConstant() == 0) {
            Map<Long, Long> rollCounts = rollCountsByGroup.get(0);
            if (rollCounts.size() == 1) {
                long roll = rollCounts.entrySet().iterator().next().getKey();
                return String.format("Rolled **%d**", roll);
            }
            long roll = sumRolls(rollCounts, dc.getConstant());
            String rolledValuesStr = buildRolledValuesString(rollCounts);
            return String.format("Rolled **%d**: `[%s]`", roll, rolledValuesStr);
        }

        List<DiceGroup> dice = dc.getDice();
        StringJoiner diceGroupJoiner = new StringJoiner("\n");
        long total = 0;
        for (int i = 0; i < rollCountsByGroup.size(); i++) {
            DiceGroup diceGroup = dice.get(i);
            Map<Long, Long> rollCounts = rollCountsByGroup.get(i);
            total += sumRolls(rollCounts, dc.getConstant());
            String rolledValuesStr = buildRolledValuesString(rollCounts);
            diceGroupJoiner.add(String.format("%s: `[%s]`", diceGroup, rolledValuesStr));
        }
        if (dc.getConstant() != 0) {
            diceGroupJoiner.add(String.format("`%+d`", dc.getConstant()));
        }
        return String.format("Rolled **%d**\n%s", total, diceGroupJoiner);
    }
    private static long sumRolls(Map<Long, Long> rollCounts, long constant) {
        return rollCounts.entrySet().stream()
                .mapToLong(en -> en.getKey() * en.getValue())
                .reduce(Long::sum)
                .orElse(0) + constant;
    }
    private static String buildRolledValuesString(Map<Long, Long> rollCounts) {
        // returned map may have patterns, simulate random order by shuffling
        List<Long> rolledValues = MathUtils.shuffle(rollCounts.keySet());
        return rolledValues.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
    }

    private static Result colorSubcommand(SlashCommandInteractionEvent e) {
        int colorTypeId = e.getOption("type", OptionMapping::getAsInt);
        Optional<ColorType> colorTypeOpt = ColorType.of(colorTypeId);
        if (!colorTypeOpt.isPresent()) {
            return new Result(Outcome.ERROR, "Invalid argument in /random color");
        }
        Color randomColor = colorTypeOpt.get().colorGenerator.get();
        EmbedBuilder eb = ColorCommand.buildEmbed(randomColor);
        return new Result(Outcome.SUCCESS, MessageUtils.addFooter(eb).build());
    }

    @AllArgsConstructor
    private enum ColorType {
        MINECRAFT(0, ColorUtils::randomColor),
        HEX(1, ColorUtils::veryRandomColor);

        public final int id;
        public final Supplier<Color> colorGenerator;

        public static Optional<ColorType> of(int id) {
            return Arrays.stream(values())
                    .filter(ct -> ct.id == id)
                    .findFirst();
        }
    }

}
