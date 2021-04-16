package com.tisawesomeness.minecord.command.utility;

import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.util.Colors;

import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.Color;

public class ColorCommand extends AbstractUtilityCommand {

    public @NonNull String getId() {
        return "color";
    }

    public void run(String[] args, CommandContext ctx) {
        if (args.length == 0) {
            ctx.showHelp();
            return;
        }
        ctx.triggerCooldown();
        Color c = Colors.parseColor(ctx.joinArgs(), "en_US");
        if (c == null) {
            ctx.invalidArgs("Not a valid color!");
            return;
        }
        ctx.replyRaw(ctx.addFooter(buildColorInfo(c)));
    }

    protected static EmbedBuilder buildColorInfo(Color c) {
        String formats = Colors.getRGB(c) + "\n" +
                Colors.getHSV(c) + "\n" +
                Colors.getHSL(c) + "\n" +
                Colors.getCMYK(c);
        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("Color Info")
                .setColor(c)
                .addField("Other formats", formats, true)
                .addField("Hex Code", Colors.getHexCode(c), true)
                .addField("Integer", String.valueOf(Colors.getInt(c)), true);
        // Test for Minecraft color
        int colorID = Colors.getMCIndex(c);
        if (colorID >= 0) {
            eb.addField("Name", Colors.getName(colorID), true)
                    .addField("Chat Code", Colors.getColorCode(colorID), true)
                    .addField("Background Color", Colors.getBackgroundHex(colorID), true);
        }
        return eb;
    }

}