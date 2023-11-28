package com.tisawesomeness.minecord.command.utility;

import com.tisawesomeness.minecord.command.SlashCommand;
import com.tisawesomeness.minecord.mc.pos.BlockPos;
import com.tisawesomeness.minecord.mc.pos.SectionPos;
import com.tisawesomeness.minecord.mc.pos.Vec3;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Optional;

public class CoordsCommand extends SlashCommand {

    @Override
    public CommandInfo getInfo() {
        return new CommandInfo(
                "coords",
                "Convert Overworld <-> Nether coordinates and compute chunk positions.",
                "<coordinate> [<dimension>]",
                0,
                false,
                false
        );
    }

    @Override
    public SlashCommandData addCommandSyntax(SlashCommandData builder) {
        return builder.addOptions(
                new OptionData(OptionType.STRING, "coordinate", "A coordinate (example: `57, -12, 105`)", true),
                new OptionData(OptionType.INTEGER, "dimension", "Current dimension")
                        .addChoice("Overworld", Dimension.OVERWORLD.ordinal()).addChoice("Nether", Dimension.NETHER.ordinal())
        );
    }

    @Override
    public Result run(SlashCommandInteractionEvent e) {
        String coordinateStr = e.getOption("coordinate", OptionMapping::getAsString);
        int dimensionInt = e.getOption("dimension", Dimension.OVERWORLD.ordinal(), OptionMapping::getAsInt);

        Optional<Vec3> coordinateOpt = Vec3.parse(coordinateStr);
        if (!coordinateOpt.isPresent()) {
            return new Result(Outcome.WARNING, "Could not parse coordinate.");
        }
        BlockPos coordinate = new BlockPos(coordinateOpt.get().round());
        Dimension dimension = dimensionInt == 0 ? Dimension.OVERWORLD : Dimension.NETHER;

        if (!coordinate.isInBounds()) {
            return new Result(Outcome.WARNING, "Coordinate is outside the world border at 29,999,984 blocks.");
        }

        return new Result(Outcome.SUCCESS, buildMessage(coordinate, dimension));
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
        return String.format("Position: `%s`\n", blockPos) +
                String.format("Position within Section: `%s`\n", blockPos.getPosWithinSection()) +
                String.format("Section (Chunk): `%s`\n", section) +
                String.format("Section within Region: `%s`\n", section.getPosWithinRegion()) +
                String.format("Region file: `%s`", section.getRegionFileName());
    }

    private enum Dimension {
        OVERWORLD,
        NETHER
    }

}
