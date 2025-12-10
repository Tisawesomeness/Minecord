package com.tisawesomeness.minecord.command.utility;

import com.tisawesomeness.minecord.command.OptionTypes;
import com.tisawesomeness.minecord.command.SlashCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class ShadowCommand extends SlashCommand {

    private static final long SUM = -7379792620528906219L;

    public CommandInfo getInfo() {
        return new CommandInfo(
                "shadow",
                "Gets the shadow of a seed.",
                "<seed>",
                0,
                false,
                false
        );
    }

    @Override
    public SlashCommandData addCommandSyntax(SlashCommandData builder) {
        return builder.addOption(OptionType.STRING, "seed", "The seed to get the shadow of", true);
    }

    @Override
    public String getHelp() {
        return "`{&}shadow <text>` - Generates a seed's \"shadow\", where the biome maps are the same but everything else is different.\n" +
                "Spaces at the start and end are removed, and numbers are treated as raw numbers (same as MC 1.18.2).\n" +
                "\n" +
                "Examples:\n" +
                "- `{&}shadow Glacier`\n" +
                "- `{&}shadow zsjpxah` - converted to numeric 0\n" +
                "- `{&}shadow 0` - numeric seed 0\n";
    }

    public Result run(SlashCommandInteractionEvent e) {
        String seed = getOption(e, "seed", OptionTypes.STRING);
        if (seed == null) {
            return Result.SLASH_COMMAND_FAIL;
        }
        long shadow = shadow(stringToSeed(seed));
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
