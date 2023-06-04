package com.tisawesomeness.minecord.command.utility;

import com.tisawesomeness.minecord.command.SlashCommand;
import com.tisawesomeness.minecord.util.ColorUtils;
import com.tisawesomeness.minecord.util.MessageUtils;
import com.tisawesomeness.minecord.util.RequestUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.IOException;

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

    private static final String img = "https://minecraft.gamepedia.com/media/minecraft.gamepedia.com/7/7e/Minecraft_Formatting.gif";
    private static byte[] imgBytes;

    public Result run(SlashCommandInteractionEvent e) {
        e.deferReply().queue();

        if (imgBytes == null) {
            try {
                imgBytes = RequestUtils.download(img);
            } catch (IOException ex) {
                ex.printStackTrace();
                return new Result(Outcome.ERROR, "There was an error downloading the chat codes image.");
            }
        }

        String desc = "Symbol copy-paste: `\u00A7`, `\\u00A7`\nUse `/color` to get info on a color.";
        EmbedBuilder eb = MessageUtils.addFooter(new EmbedBuilder())
                .setTitle("Minecraft Chat Codes")
                .setColor(ColorUtils.randomColor())
                .setDescription(desc)
                .setImage("attachment://codes.gif");
        e.getHook().sendMessageEmbeds(eb.build()).addFiles(FileUpload.fromData(imgBytes, "codes.gif")).queue();
        return new Result(Outcome.SUCCESS);

    }

}
