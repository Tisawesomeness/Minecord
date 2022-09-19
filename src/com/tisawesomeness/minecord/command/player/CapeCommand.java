package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.mc.player.Player;
import com.tisawesomeness.minecord.mc.player.RenderType;
import com.tisawesomeness.minecord.util.ColorUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;

public class CapeCommand extends BasePlayerCommand {

    public CommandInfo getInfo() {
        return new CommandInfo(
                "cape",
                "Shows a player's capes.",
                "<player>",
                2000,
                false,
                false
        );
    }

    @Override
    public String getHelp() {
        return "`{&}cape <player>` - Shows an image of the player's Minecraft and Optifine capes.\n" +
                "- `<player>` can be a username or UUID.\n" +
                "Use `{&}help usernameInput|uuidInput|phd` for more help.\n" +
                "\n" +
                "Examples:\n" +
                "- `{&}cape Tis_awesomeness`\n" +
                "- `{&}cape LadyAgnes`\n" +
                "- `{&}cape f6489b797a9f49e2980e265a05dbc3af`\n" +
                "- `{&}cape 069a79f4-44e9-4726-a5be-fca90e38aaf5`\n";
    }

    protected void onSuccessfulPlayer(SlashCommandInteractionEvent e, Player player) {
        boolean hasMojangCape = false;
        Optional<URL> capeUrlOpt = player.getProfile().getCapeUrl();
        if (capeUrlOpt.isPresent()) {
            URL capeUrl = capeUrlOpt.get();
            sendCape(e, player, capeUrl, "Minecraft");
            hasMojangCape = true;
        }

        URL optifineCapeUrl = player.getOptifineCapeUrl();
        boolean hasOptifineCape = false;
        try {
            hasOptifineCape = Bot.mcLibrary.getClient().exists(optifineCapeUrl);
        } catch (IOException ex) {
            System.err.println("IOE getting optifine cape for " + player);
            ex.printStackTrace();
            e.getHook().sendMessage("There was an error requesting the Optifine cape.").setEphemeral(true).queue();
        }
        if (hasOptifineCape) {
            sendCape(e, player, optifineCapeUrl, "Optifine");
        }

        if (!hasMojangCape && !hasOptifineCape) {
            e.getHook().sendMessage(player.getUsername() + " does not have a cape.").queue();
        }
    }

    private static void sendCape(SlashCommandInteractionEvent e, Player player, URL capeUrl, String capeType) {
        String nameMcUrl = player.getNameMCUrl().toString();
        String avatarUrl = player.createRender(RenderType.AVATAR, true).render().toString();
        String title = capeType + " Cape for " + player.getUsername();
        Color color = player.isRainbow() ? ColorUtils.randomColor() : Bot.color;
        EmbedBuilder eb = new EmbedBuilder()
                .setAuthor(title, nameMcUrl, avatarUrl)
                .setColor(color)
                .setImage(capeUrl.toString());
        e.getHook().sendMessageEmbeds(eb.build()).queue();
    }

}
