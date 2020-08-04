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

    public Result run(CommandContext ctx) {
        if (ctx.args.length == 0) {
            return ctx.showHelp();
        }
        Color c = ColorUtils.parseColor(ctx.joinArgs(), "en_US");
        if (c == null) {
            return new Result(Outcome.WARNING, ":warning: Not a valid color!");
        }
        EmbedBuilder eb = buildColorInfo(c);
        return new Result(Outcome.SUCCESS, ctx.addFooter(eb).build());
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