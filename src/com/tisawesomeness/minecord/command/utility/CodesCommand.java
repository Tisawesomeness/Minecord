package com.tisawesomeness.minecord.command.utility;

import com.tisawesomeness.minecord.command.SlashCommand;
import com.tisawesomeness.minecord.util.ColorUtils;
import com.tisawesomeness.minecord.util.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class CodesCommand extends SlashCommand {

    public CommandInfo getInfo() {
        return new CommandInfo(
                "codes",
                "Lists the available chat codes.",
                null,
                1000,
                false,
                false
        );
    }

    @Override
    public String[] getLegacyAliases() {
        return new String[]{"code", "chat"};
    }

    private static final String img = "https://minecraft.wiki/images/Minecraft_Formatting.gif?2311f";

    public Result run(SlashCommandInteractionEvent e) {

        String desc = "Symbol copy-paste: `\u00A7`, `\\u00A7`\nUse `/color` to get info on a color.";
        EmbedBuilder eb = MessageUtils.addFooter(new EmbedBuilder())
                .setTitle("Minecraft Chat Codes")
                .setColor(ColorUtils.randomColor())
                .setDescription(desc)
                .setImage(img);
        return new Result(Outcome.SUCCESS, MessageUtils.addFooter(eb).build());

    }

}
