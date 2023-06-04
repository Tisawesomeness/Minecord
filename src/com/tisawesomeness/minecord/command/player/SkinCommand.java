package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.mc.player.Player;
import com.tisawesomeness.minecord.mc.player.RenderType;
import com.tisawesomeness.minecord.util.ColorUtils;
import com.tisawesomeness.minecord.util.MessageUtils;
import com.tisawesomeness.minecord.util.RequestUtils;

import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;

import java.awt.Color;
import java.io.IOException;

public class SkinCommand extends BasePlayerCommand {

    public CommandInfo getInfo() {
        return new CommandInfo(
                "skin",
                "Shows an image of a player's skin.",
                "<player>",
                1000,
                false,
                false
        );
    }

    public String getHelp() {
        return "`{&}skin <player>` - Shows an image of the player's skin.\n" +
                "- `<player>` can be a username or UUID.\n" +
                "Use `{&}help usernameInput|uuidInput|phd` for more help.\n" +
                "\n" +
                "Examples:\n" +
                "- `{&}skin Tis_awesomeness`\n" +
                "- `{&}skin LadyAgnes`\n" +
                "- `{&}skin f6489b797a9f49e2980e265a05dbc3af`\n" +
                "- `{&}skin 069a79f4-44e9-4726-a5be-fca90e38aaf5`\n";
    }

    protected void onSuccessfulPlayer(SlashCommandInteractionEvent e, Player player) {
        String title = "Skin for " + player.getUsername();
        String skinHistoryUrl = player.getMCSkinHistoryUrl().toString();
        String avatarUrl = player.createRender(RenderType.AVATAR, true).render().toString();
        String skinUrl = player.getSkinUrl().toString();
        String description = constructDescription(player);

        Color color = player.isRainbow() ? ColorUtils.randomColor() : Bot.color;

        EmbedBuilder eb = MessageUtils.addFooter(new EmbedBuilder())
                .setAuthor(title, skinHistoryUrl, avatarUrl)
                .setColor(color)
                .setDescription(description);
        try {
            System.out.println(skinUrl);
            byte[] data = RequestUtils.download(skinUrl);
            e.getHook().sendMessageEmbeds(eb.setImage("attachment://skin.png").build())
                    .addFiles(FileUpload.fromData(data, "skin.png")).queue();
        } catch (IOException ex) {
            ex.printStackTrace();
            e.getHook().sendMessageEmbeds(eb.setImage(skinUrl).build()).queue();
        }
    }
    private static @NonNull String constructDescription(Player player) {
        String custom = "**Custom**: " + (player.hasCustomSkin() ? "True" : "False");
        String skinModel = "**Skin Model**: " + player.getSkinModel().getDescription();
        String defaultModel = "**Default Skin Model**: " + player.getDefaultSkinModel().getDescription();
        String newDefaultModel = "**1.19.3+ Default Skin**: " + player.getNewDefaultSkin();
        return custom + "\n" + skinModel + "\n" + defaultModel + "\n" + newDefaultModel;
    }

}
