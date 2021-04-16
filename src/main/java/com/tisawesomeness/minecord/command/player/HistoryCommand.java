package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.command.meta.CommandContext;
import com.tisawesomeness.minecord.mc.player.Player;
import com.tisawesomeness.minecord.mc.player.RenderType;
import com.tisawesomeness.minecord.util.Colors;
import com.tisawesomeness.minecord.util.Discord;
import com.tisawesomeness.minecord.util.Strings;
import com.tisawesomeness.minecord.util.Time;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.Color;
import java.util.List;

@Slf4j
public class HistoryCommand extends BasePlayerCommand {

    public @NonNull String getId() {
        return "history";
    }

    public void onSuccessfulPlayer(CommandContext ctx, Player player) {
        List<String> historyLines = buildHistoryLines(ctx, Time.Format.DATETIME, player.getNameHistory());
        List<String> historyPartitions = Strings.partitionLinesByLength(
                historyLines, MessageEmbed.VALUE_MAX_LENGTH);

        MessageEmbed baseEmbed = constructBaseEmbed(ctx, player);
        String fieldTitle = ctx.getLang().i18n("mc.player.history.nameHistory");

        List<MessageEmbed> embeds = Discord.splitEmbeds(baseEmbed, fieldTitle, historyPartitions, "\n");
        for (MessageEmbed emb : embeds) {
            ctx.reply(emb);
        }
    }

    private static @NonNull MessageEmbed constructBaseEmbed(CommandContext ctx, Player player) {
        String title = ctx.i18nf("title", player.getUsername());
        String nameMCUrl = player.getNameMCUrl().toString();
        String avatarUrl = player.createRender(RenderType.AVATAR, true).render().toString();
        Color color = player.isRainbow() ? Colors.randomColor() : ctx.getColor();
        return ctx.addFooter(new EmbedBuilder())
                .setAuthor(title, nameMCUrl, avatarUrl)
                .setColor(color)
                .build();
    }

}
