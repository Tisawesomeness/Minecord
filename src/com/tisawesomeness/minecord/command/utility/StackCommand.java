package com.tisawesomeness.minecord.command.utility;

import com.tisawesomeness.minecord.command.SlashCommand;
import com.tisawesomeness.minecord.mc.item.Container;
import com.tisawesomeness.minecord.mc.item.ItemCount;
import com.tisawesomeness.minecord.util.type.HumanDecimalFormat;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.*;

public class StackCommand extends SlashCommand {

    private static final HumanDecimalFormat FORMAT = HumanDecimalFormat.builder()
            .maximumFractionDigits(6)
            .minimumExponentForExactValues(-5)
            .includeApproximationSymbol(true)
            .includeGroupingCommas(true)
            .build();
    private static final EnumSet<Container> ALL_CONTAINERS = EnumSet.allOf(Container.class);
    private static final EnumSet<Container> NO_SHULKERS = EnumSet.complementOf(EnumSet.of(
            Container.CHEST_SHULKER, Container.DOUBLE_CHEST_SHULKER));

    @Override
    public CommandInfo getInfo() {
        return new CommandInfo(
                "stack",
                "Convert item counts to stacks, chests, shulkers, and back.",
                "<arguments...>",
                0,
                false,
                false
        );
    }

    @Override
    public SlashCommandData addCommandSyntax(SlashCommandData builder) {
        return builder.addOptions(
                new OptionData(OptionType.INTEGER, "items", "Number of items").setMinValue(0),
                new OptionData(OptionType.INTEGER, "stacks", "Number of stacks of items").setMinValue(0),
                new OptionData(OptionType.INTEGER, "chests", "Number of chests full of items").setMinValue(0),
                new OptionData(OptionType.INTEGER, "double-chests", "Number of double chests full of items").setMinValue(0),
                new OptionData(OptionType.INTEGER, "chest-shulkers", "Number of chests full of shulkers").setMinValue(0),
                new OptionData(OptionType.INTEGER, "double-chest-shulkers", "Number of double chests full of shulkers").setMinValue(0),
                new OptionData(OptionType.INTEGER, "stack-size", "Stack size of the item").setRequiredRange(1, ItemCount.MAX_STACK_SIZE)
        );
    }

    @Override
    public Result run(SlashCommandInteractionEvent e) {
        long items = e.getOption("items", 0L, OptionMapping::getAsLong);
        long stacks = e.getOption("stacks", 0L, OptionMapping::getAsLong);
        long chests = e.getOption("chests", 0L, OptionMapping::getAsLong);
        long doubleChests = e.getOption("double-chests", 0L, OptionMapping::getAsLong);
        long chestShulkers = e.getOption("chest-shulkers", 0L, OptionMapping::getAsLong);
        long doubleChestShulkers = e.getOption("double-chest-shulkers", 0L, OptionMapping::getAsLong);
        int stackSize = e.getOption("stack-size", 64, OptionMapping::getAsInt);

        ItemCount itemCount = new ItemCount(items, stackSize)
                .addStacks(stacks)
                .addContainers(Container.CHEST, chests)
                .addContainers(Container.DOUBLE_CHEST, doubleChests)
                .addContainers(Container.CHEST_SHULKER, chestShulkers)
                .addContainers(Container.DOUBLE_CHEST_SHULKER, doubleChestShulkers);
        if (itemCount.getCount() == 0) {
            return new Result(Outcome.WARNING, "Must add at least one item");
        }

        List<Long> inputDistribution = Arrays.asList(doubleChestShulkers, chestShulkers, doubleChests, chests, stacks, items);
        String inputDistributionStr = itemDistributionToPlusString(inputDistribution, ALL_CONTAINERS);
        String output = String.format("%s is equal to:\n", inputDistributionStr) +
                buildDistributionLine(itemCount) +
                buildDistributionLineNoShulkers(itemCount) +
                "\n" +
                "Or:\n" +
                itemLine(itemCount) +
                itemCountExactLine(itemCount, Container.STACK) +
                itemCountExactLine(itemCount, Container.CHEST) +
                itemCountExactLine(itemCount, Container.DOUBLE_CHEST) +
                itemCountExactLine(itemCount, Container.CHEST_SHULKER) +
                itemCountExactLine(itemCount, Container.DOUBLE_CHEST_SHULKER);

        return new Result(Outcome.SUCCESS, output);
    }

    private static String buildDistributionLine(ItemCount itemCount) {
        List<Long> distribution = itemCount.distribute(ALL_CONTAINERS);
        String distributionStr = itemDistributionToPlusString(distribution, ALL_CONTAINERS);
        return String.format("- %s\n", distributionStr);
    }
    private static String buildDistributionLineNoShulkers(ItemCount itemCount) {
        // Don't include line if distribution wouldn't change by removing shulkers
        if (itemCount.getExact(Container.CHEST_SHULKER) < 1) {
            return "";
        }
        List<Long> distribution = itemCount.distribute(NO_SHULKERS);
        String distributionStr = itemDistributionToPlusString(distribution, NO_SHULKERS);
        return String.format("- %s\n", distributionStr);
    }
    private static String itemDistributionToPlusString(List<Long> distribution, EnumSet<Container> containers) {
        List<Container> containersList = new ArrayList<>(containers); // Keeps natural order

        // Does not include items since items is less than smallest container (STACK)
        StringJoiner distributionJoiner = new StringJoiner(" + ");
        for (int i = 0; i < distribution.size() - 1; i++) {
            long value = distribution.get(i);
            if (value != 0) {
                Container container = Container.values()[containersList.size() - i - 1];
                String formatted = String.format("**%d** %s", value, container.getDescription(value));
                distributionJoiner.add(formatted);
            }
        }

        // Account for items (last element of distribution list)
        long leftoverItems = distribution.get(distribution.size() - 1);
        if (leftoverItems != 0) {
            String formatted = String.format("**%d** %s", leftoverItems, pluralItems(leftoverItems));
            distributionJoiner.add(formatted);
        }
        return distributionJoiner.toString();
    }

    private static String pluralItems(double count) {
        return count == 1.0 ? "item" : "items";
    }

    private static String itemLine(ItemCount itemCount) {
        long count = itemCount.getCount();
        return String.format("- **%d** %s\n", count, pluralItems(count));
    }
    private static String itemCountExactLine(ItemCount itemCount, Container container) {
        double value = itemCount.getExact(container);
        return String.format("- **%s** %s\n", FORMAT.format(value), container.getDescription((int) value));
    }

}
