package com.tisawesomeness.minecord.command.utility;

import java.awt.Color;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.util.ColorUtils;
import com.tisawesomeness.minecord.util.MessageUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ColorCommand extends Command {

    public CommandInfo getInfo() {
		return new CommandInfo(
			"color",
			"Look up a color.",
			"<color>",
			new String[]{"colour", "colorcode", "colourcode"},
			1000,
			false,
			false,
			true
		);
    }

    public String getHelp() {
        return "`{&}color <color>` - Look up a color.\n" +
            "`{&}color random` - Get a random Minecraft color.\n" +
            "`{&}color very random` - Get any random color.\n" +
            "Shows extra info if the color is one of the 16 Minecraft color codes.\n" +
            "\n" +
            "`<color>` can be:\n" +
            "- A color name: `red`, `dark blue`\n" +
            "- A color code: `&b`, `\u00A7b`, `b`\n" +
            "- A hex code: `#55ffff`, `0x55ffff`\n" +
            "- An RGB int: `5635925`, `i8`\n";
    }

    public Result run(String[] args, MessageReceivedEvent e) throws Exception {

        if (args.length == 0) {
            return new Result(Outcome.WARNING, ":warning: You must specify a color.");
        }

        String query = String.join(" ", args);
        Color c;
        if (query.equalsIgnoreCase("rand") || query.equalsIgnoreCase("random")) {
            c = ColorUtils.randomColor();
        } else if (query.equalsIgnoreCase("very rand") || query.equalsIgnoreCase("very random")) {
            c = ColorUtils.veryRandomColor();
        } else {
            // Parse &2 as 2
            char start = query.charAt(0);
            if (start == '&' || start == '\u00A7') {
                query = query.substring(1);
            }
            c = ColorUtils.getColor(query, "en_US");
            if (c == null) {
                try {
                    // Since "3" is interpreted as a color code, "i3" is the integer 3
                    if (start == 'i') {
                        c = new Color(Integer.parseInt(query.substring(1)));
                    } else {
                        c = Color.decode(query);
                    }
                } catch (NumberFormatException ex) {
                    return new Result(Outcome.WARNING, ":warning: Not a valid color!");
                }
            }
        }

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

        return new Result(Outcome.SUCCESS, MessageUtils.addFooter(eb).build());
    }

}