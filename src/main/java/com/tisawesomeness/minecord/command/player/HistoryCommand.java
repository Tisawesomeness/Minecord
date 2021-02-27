package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.mc.player.*;
import com.tisawesomeness.minecord.util.*;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.Color;
import java.util.*;

@Slf4j
public class HistoryCommand extends BasePlayerCommand {

    public @NonNull String getId() {
        return "history";
    }

    public void onSuccessfulPlayer(CommandContext ctx, Player player) {
        List<String> historyLines = buildHistoryLines(ctx, TimeUtils.Format.DATETIME, player.getNameHistory());
        List<String> historyPartitions = StringUtils.partitionLinesByLength(
                historyLines, MessageEmbed.VALUE_MAX_LENGTH);

        MessageEmbed baseEmbed = constructBaseEmbed(ctx, player);
        String fieldTitle = ctx.getLang().i18n("mc.player.history.nameHistory");

        List<MessageEmbed> embeds = DiscordUtils.splitEmbeds(baseEmbed, fieldTitle, historyPartitions, "\n");
        for (MessageEmbed emb : embeds) {
            ctx.reply(emb);
        }
    }

    private static @NonNull MessageEmbed constructBaseEmbed(CommandContext ctx, Player player) {
        String title = ctx.i18nf("title", player.getUsername());
        String nameMCUrl = player.getNameMCUrl().toString();
        String avatarUrl = player.createRender(RenderType.AVATAR, true).render().toString();
        Color color = player.isRainbow() ? ColorUtils.randomColor() : ctx.getColor();
        return ctx.addFooter(new EmbedBuilder())
                .setAuthor(title, nameMCUrl, avatarUrl)
                .setColor(color)
                .build();
    }

}
