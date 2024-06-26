package com.tisawesomeness.minecord.command.utility;

import com.tisawesomeness.minecord.command.SlashCommand;
import com.tisawesomeness.minecord.util.ColorUtils;
import com.tisawesomeness.minecord.util.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.awt.*;

public class ColorCommand extends SlashCommand {

    public CommandInfo getInfo() {
        return new CommandInfo(
                "color",
                "Look up a color or Minecraft color code.",
                "<color>",
                0,
                false,
                false
        );
    }

    @Override
    public SlashCommandData addCommandSyntax(SlashCommandData builder) {
        return builder.addOption(OptionType.STRING, "color", "A color", true);
    }

    @Override
    public String[] getLegacyAliases() {
        return new String[]{"colour", "colorcode", "colourcode"};
    }

    @Override
    public String getHelp() {
        return "`{&}color <color>` - Look up a color.\n" +
                "Shows extra info if the color is one of the 16 Minecraft color codes.\n" +
                "\n" +
                "`<color>` can be:\n" +
                "- A Minecraft color name: `red`, `dark blue`\n" +
                "- A color code: `&b`, `\u00A7b`, `b`\n" +
                "- A hex code: `#55ffff`, `0x55ffff`\n" +
                "- RGB format: `85 85 255`, `rgb(85,85,255)`\n" +
                "- Other formats: `hsv(120,100,50)`, `hsl(120 100 25)`, `cmyk(100%,0%,100%,50%)`\n" +
                "- An RGB int: `5635925`, `i8`\n";
    }

    public Result run(SlashCommandInteractionEvent e) {
        Color c = ColorUtils.parseColor(e.getOption("color").getAsString(), "en_US");
        if (c == null) {
            return new Result(Outcome.WARNING, ":warning: Not a valid color!");
        }
        EmbedBuilder eb = buildEmbed(c);
        return new Result(Outcome.SUCCESS, MessageUtils.addFooter(eb).build());
    }

    public static EmbedBuilder buildEmbed(Color c) {
        String formats = ColorUtils.getRGB(c) + "\n" +
                ColorUtils.getHSV(c) + "\n" +
                ColorUtils.getHSL(c) + "\n" +
                ColorUtils.getCMYK(c);
        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("Color Info")
                .setColor(c)
                .addField("Other formats", formats, true)
                .addField("Hex Code", ColorUtils.getHexCode(c), true)
                .addField("Integer", String.valueOf(ColorUtils.getInt(c)), true);
        // Test for Minecraft color
        int colorID = ColorUtils.getMCIndex(c);
        if (colorID >= 0) {
            eb.addField("Name", ColorUtils.getName(colorID), true)
                    .addField("Chat Code", ColorUtils.getColorCode(colorID), true)
                    .addField("Background Color", ColorUtils.getBackgroundHex(colorID), true);
        }
        return eb;
    }

}
