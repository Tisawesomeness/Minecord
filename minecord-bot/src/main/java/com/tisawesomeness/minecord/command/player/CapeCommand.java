package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.command.meta.CommandContext;
import com.tisawesomeness.minecord.mc.player.Player;
import com.tisawesomeness.minecord.mc.player.RenderType;
import com.tisawesomeness.minecord.network.APIClient;
import com.tisawesomeness.minecord.util.Colors;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;

@Slf4j
public class CapeCommand extends BasePlayerCommand {

    public @NonNull String getId() {
        return "cape";
    }

    protected void onSuccessfulPlayer(CommandContext ctx, Player player) {
        APIClient client = ctx.getMCLibrary().getClient();

        boolean hasMojangCape = false;
        Optional<URL> capeUrlOpt = player.getProfile().getCapeUrl();
        if (capeUrlOpt.isPresent()) {
            URL capeUrl = capeUrlOpt.get();
            sendCape(ctx, player, capeUrl, "minecraftCape");
            hasMojangCape = true;
        }

        boolean hasOptifineCape = false;
        URL optifineCapeUrl = player.getOptifineCapeUrl();
        try {
            hasOptifineCape = client.exists(optifineCapeUrl);
            if (hasOptifineCape) {
                sendCape(ctx, player, optifineCapeUrl, "optifineCape");
            }
        } catch (IOException ex) {
            log.error("IOE getting optifine cape for " + player, ex);
            ctx.err(ctx.i18n("optifineError"));
        }

        if (!hasMojangCape && !hasOptifineCape) {
            ctx.reply(ctx.i18nf("noCape", player.getUsername()));
        }
    }

    private static void sendCape(CommandContext ctx, Player player, URL capeUrl, String translationKey) {
        String nameMcUrl = player.getNameMCUrl().toString();
        String avatarUrl = player.createRender(RenderType.AVATAR, true).render().toString();
        String title = ctx.i18nf(translationKey, player.getUsername());
        Color color = player.isRainbow() ? Colors.randomColor() : ctx.getColor();
        EmbedBuilder eb = new EmbedBuilder()
                .setAuthor(title, nameMcUrl, avatarUrl)
                .setColor(color)
                .setImage(capeUrl.toString());
        ctx.replyRaw(eb);
    }

}
