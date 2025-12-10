package com.tisawesomeness.minecord.command.discord;

import com.tisawesomeness.minecord.command.OptionTypes;
import com.tisawesomeness.minecord.command.SlashCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.utils.TimeFormat;
import net.dv8tion.jda.api.utils.TimeUtil;

import java.time.OffsetDateTime;

public class IdCommand extends SlashCommand {

    public CommandInfo getInfo() {
        return new CommandInfo(
                "id",
                "Gets the creation time of a Discord ID.",
                "<id>",
                0,
                false,
                false
        );
    }

    @Override
    public SlashCommandData addCommandSyntax(SlashCommandData builder) {
        return builder.addOption(OptionType.INTEGER, "id", "The Discord ID", true);
    }

    @Override
    public String[] getLegacyAliases() {
        return new String[]{"snowflake"};
    }

    @Override
    public String getHelp() {
        return "`{&}id <id>` - Gets the creation time of a Discord ID.\n" +
                "This command does not check if an ID exists.\n" +
                "To get Discord IDs, turn on User Settings > Advanced > Developer Mode, then right click and select \"Copy ID\"\n" +
                "The `{&}user`/`{&}role`/`{&}guild` commands also show IDs.\n" +
                "\n" +
                "Examples:\n" +
                "- `{&}id 211261249386708992`\n" +
                "- `{&}id 292279711034245130`\n";
    }

    @Override
    public Result run(SlashCommandInteractionEvent e) throws Exception {
        Long id = getOption(e, "id", OptionTypes.LONG);
        if (id == null) {
            return Result.SLASH_COMMAND_FAIL;
        }
        OffsetDateTime time = TimeUtil.getTimeCreated(id);
        return new Result(Outcome.SUCCESS, "Created " + TimeFormat.RELATIVE.format(time));
    }

}
