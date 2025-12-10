package com.tisawesomeness.minecord.command.utility;

import com.tisawesomeness.minecord.command.OptionTypes;
import com.tisawesomeness.minecord.command.SlashCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class SeedCommand extends SlashCommand {

    public CommandInfo getInfo() {
        return new CommandInfo(
                "seed",
                "Converts some text to a seed number.",
                "<text>",
                0,
                false,
                false
        );
    }

    @Override
    public SlashCommandData addCommandSyntax(SlashCommandData builder) {
        return builder.addOption(OptionType.STRING, "text", "The text to convert to a seed number", true);
    }

    @Override
    public String getHelp() {
        return "`{&}seed <text>` - Converts some text to a seed number.\n" +
                "Spaces at the start and end are removed, and numbers are treated as strings.\n" +
                "\n" +
                "Examples:\n" +
                "- `{&}seed Glacier`\n" +
                "- `{&}seed zsjpxah` - numeric seed 0\n" +
                "- `{&}seed 0` - treated as a string\n";
    }

    @Override
    public Result run(SlashCommandInteractionEvent e) throws Exception {
        String seedArg = getOption(e, "text", OptionTypes.STRING);
        if (seedArg == null) {
            return Result.SLASH_COMMAND_FAIL;
        }
        long seed = seedArg.hashCode();
        return new Result(Outcome.SUCCESS, String.format("Seed: `%d`", seed));
    }

}
