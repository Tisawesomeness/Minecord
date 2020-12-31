package com.tisawesomeness.minecord.command.utility;

import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.util.ColorUtils;

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
        Color c = ColorUtils.parseColor(ctx.joinArgs(), "en_US");
        if (c == null) {
            ctx.invalidArgs("Not a valid color!");
            return;
        }
        ctx.replyRaw(ctx.addFooter(buildColorInfo(c)));
    }

    public static EmbedBuilder buildColorInfo(Color c) {
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