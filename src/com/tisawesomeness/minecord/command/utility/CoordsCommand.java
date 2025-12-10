package com.tisawesomeness.minecord.command.utility;

import com.tisawesomeness.minecord.command.OptionTypes;
import com.tisawesomeness.minecord.command.SlashCommand;
import com.tisawesomeness.minecord.mc.pos.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import javax.annotation.Nullable;

public class CoordsCommand extends SlashCommand {

    @Override
    public CommandInfo getInfo() {
        return new CommandInfo(
                "coords",
                "Convert Overworld <-> Nether coordinates and compute chunk positions.",
                "<coordinate> [<dimension>] [<type>]",
                0,
                false,
                false
        );
    }

    @Override
    public SlashCommandData addCommandSyntax(SlashCommandData builder) {
        return builder.addOptions(
                new OptionData(OptionType.STRING, "coordinate", "A coordinate (example: `57, -12, 105` or `57, 105`)", true),
                new OptionData(OptionType.INTEGER, "dimension", "Current dimension")
                        .addChoice("Overworld", Dimension.OVERWORLD.ordinal())
                        .addChoice("Nether", Dimension.NETHER.ordinal()),
                new OptionData(OptionType.INTEGER, "type", "Type of coordinate")
                        .addChoice("Position", Type.POSITION.ordinal())
                        .addChoice("Chunk/Section", Type.SECTION.ordinal())
                        .addChoice("Region", Type.REGION.ordinal())
        );
    }

    @Override
    public Result run(SlashCommandInteractionEvent e) {
        int typeInt = getOption(e, "type", Type.POSITION.ordinal(), OptionTypes.INTEGER);
        Type type = Type.values()[typeInt];

        String coordinateStr = getOption(e, "coordinate", OptionTypes.STRING);
        if (coordinateStr == null) {
            return Result.SLASH_COMMAND_FAIL;
        }
        Vec vec = Vec.parse(coordinateStr);
        if (vec == null) {
            return new Result(Outcome.WARNING, "Could not parse coordinate.");
        }
        BlockPos coordinate = computeBlockPos(vec, type);
        if (coordinate == null) {
            return new Result(Outcome.WARNING, "Region coordinates must be in `x, z` format.");
        }
        if (!coordinate.isInBounds()) {
            return new Result(Outcome.WARNING, "Coordinate is outside the world border at 29,999,984 blocks.");
        }

        int dimensionInt = getOption(e, "dimension", Dimension.OVERWORLD.ordinal(), OptionTypes.INTEGER);
        Dimension dimension = Dimension.values()[dimensionInt];

        return new Result(Outcome.SUCCESS, buildMessage(coordinate, dimension));
    }

    private static @Nullable BlockPos computeBlockPos(Vec vec, Type type) {
        switch (type) {
            case POSITION: {
                if (vec instanceof Vec3) {
                    return new BlockPos(((Vec3) vec).round());
                } else {
                    return new BlockPos(((Vec2) vec).round().withY(0));
                }
            }
            case SECTION: {
                if (vec instanceof Vec3) {
                    return new SectionPos(((Vec3) vec).round()).getBlockPos();
                } else {
                    return new SectionPos(((Vec2) vec).round().withY(0)).getBlockPos();
                }
            }
            case REGION: {
                if (vec instanceof Vec3) {
                    return null;
                } else {
                    return new RegionPos(((Vec2) vec).round()).getSectionPos().getBlockPos();
                }
            }
            default: throw new AssertionError("unreachable");
        }
    }

    private static String buildMessage(BlockPos coordinate, Dimension dimension) {
        if (dimension == Dimension.OVERWORLD) {
            String overworld = buildCoordinateCalculationString(coordinate);
            String nether = buildCoordinateCalculationString(coordinate.overworldToNether());
            return "**Overworld Coordinates**:\n" +
                    overworld + "\n" +
                    "\n" +
                    "**Nether Coordinates**:\n" +
                    nether;
        } else {
            String nether = buildCoordinateCalculationString(coordinate);
            String overworld = buildCoordinateCalculationString(coordinate.netherToOverworld());
            return "**Nether Coordinates**:\n" +
                    nether + "\n" +
                    "\n" +
                    "**Overworld Coordinates**:\n" +
                    overworld;
        }
    }
    private static String buildCoordinateCalculationString(BlockPos blockPos) {
        SectionPos section = blockPos.getSection();
        RegionPos region = section.getRegionPos();
        return String.format("Position: `%s`\n", blockPos) +
                String.format("Position within Section: `%s`\n", blockPos.getPosWithinSection()) +
                String.format("Section (Chunk): `%s`\n", section) +
                String.format("Section within Region: `%s`\n", section.getPosWithinRegion()) +
                String.format("Region file: `%s`", region.getFileName());
    }

    private enum Dimension {
        OVERWORLD,
        NETHER
    }

    private enum Type {
        POSITION,
        SECTION,
        REGION
    }

}
