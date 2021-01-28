package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.mc.player.Player;
import com.tisawesomeness.minecord.network.APIClient;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

@Slf4j
public class CapeCommand extends BasePlayerCommand {

    public @NonNull String getId() {
        return "cape";
    }

    public void onSuccessfulPlayer(CommandContext ctx, Player player) {
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
            sendCape(ctx, player, optifineCapeUrl, "optifineCape");
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
        String avatarUrl = player.getAvatarUrl().toString();
        String title = ctx.i18nf(translationKey, player.getUsername());
        EmbedBuilder eb = new EmbedBuilder()
                .setAuthor(title, nameMcUrl, avatarUrl)
                .setImage(capeUrl.toString())
                .setColor(Bot.color);
        ctx.replyRaw(eb);
    }

}
