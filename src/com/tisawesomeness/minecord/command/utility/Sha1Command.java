package com.tisawesomeness.minecord.command.utility;

import com.tisawesomeness.minecord.command.OptionTypes;
import com.tisawesomeness.minecord.command.SlashCommand;
import com.tisawesomeness.minecord.util.MathUtils;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class Sha1Command extends SlashCommand {

    public CommandInfo getInfo() {
        return new CommandInfo(
                "sha1",
                "Computes the sha1 hash of some text.",
                "<text>",
                0,
                false,
                false
        );
    }

    @Override
    public SlashCommandData addCommandSyntax(SlashCommandData builder) {
        return builder.addOption(OptionType.STRING, "text", "The text to hash", true);
    }

    @Override
    public String[] getLegacyAliases() {
        return new String[]{"sha", "hash"};
    }

    @Override
    public String getHelp() {
        return "`{&}sha1 <text>` - Computes the sha1 hash of some text.\n" +
                "Useful for comparing a server against Mojang's blocked server list.\n" +
                "\n" +
                "Examples:\n" +
                "- `{&}sha1 any string here`\n" +
                "- `{&}sha1 mc.hypixel.net`\n";
    }

    public Result run(SlashCommandInteractionEvent e) {
        String text = getOption(e, "text", OptionTypes.STRING);
        if (text == null) {
            return Result.SLASH_COMMAND_FAIL;
        }
        return new Result(Outcome.SUCCESS, MathUtils.sha1(text));
    }

}
