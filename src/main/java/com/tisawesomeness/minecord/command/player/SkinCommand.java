package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.lang.Lang;
import com.tisawesomeness.minecord.mc.player.Player;
import com.tisawesomeness.minecord.mc.player.RenderType;
import com.tisawesomeness.minecord.util.ColorUtils;

import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.awt.Color;

public class SkinCommand extends BasePlayerCommand {

    public @NonNull String getId() {
        return "skin";
    }

    public void onSuccessfulPlayer(CommandContext ctx, Player player) {
        String title = ctx.i18nf("title", player.getUsername());
        String skinHistoryUrl = player.getMCSkinHistoryUrl().toString();
        String avatarUrl = player.createRender(RenderType.AVATAR, true).render().toString();
        String description = constructDescription(ctx, player);

        Color color = player.isRainbow() ? ColorUtils.randomColor() : ctx.getColor();
        EmbedBuilder eb = ctx.addFooter(new EmbedBuilder())
                .setAuthor(title, skinHistoryUrl, avatarUrl)
                .setColor(color)
                .setDescription(description)
                .setImage(player.getSkinUrl().toString());
        ctx.replyRaw(eb);
    }
    private static @NonNull String constructDescription(CommandContext ctx, Player player) {
        Lang lang = ctx.getLang();
        String custom = MarkdownUtil.bold(lang.i18n("mc.player.skin.custom")) + ": " +
                lang.displayBool(player.hasCustomSkin());
        String skinType = MarkdownUtil.bold(lang.i18n("mc.player.skin.model")) + ": " +
                lang.i18n(player.getSkinType().getTranslationKey());
        String defaultModel = MarkdownUtil.bold(lang.i18n("mc.player.skin.default")) + ": " +
                lang.i18n(player.getDefaultSkinType().getTranslationKey());
        return custom + "\n" + skinType + "\n" + defaultModel;
    }

}
