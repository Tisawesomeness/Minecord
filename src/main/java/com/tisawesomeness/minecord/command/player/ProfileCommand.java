package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.command.meta.CommandContext;
import com.tisawesomeness.minecord.lang.Lang;
import com.tisawesomeness.minecord.mc.external.PlayerProvider;
import com.tisawesomeness.minecord.mc.player.AccountStatus;
import com.tisawesomeness.minecord.mc.player.Player;
import com.tisawesomeness.minecord.mc.player.RenderType;
import com.tisawesomeness.minecord.util.Colors;
import com.tisawesomeness.minecord.util.Discord;
import com.tisawesomeness.minecord.util.Strings;
import com.tisawesomeness.minecord.util.UUIDs;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.awt.Color;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class ProfileCommand extends BasePlayerCommand {

    public @NonNull String getId() {
        return "profile";
    }

    public void onSuccessfulPlayer(CommandContext ctx, Player player) {
        PlayerProvider provider = ctx.getMCLibrary().getPlayerProvider();
        provider.getAccountStatus(player.getUuid())
                .exceptionally(ex -> Optional.empty())
                .thenAccept(statusOpt -> onSuccessfulStatus(ctx, player, statusOpt));
    }

    private static void onSuccessfulStatus(CommandContext ctx, Player player, Optional<AccountStatus> statusOpt) {
        Lang lang = ctx.getLang();

        String title = ctx.i18nf("title", player.getUsername());
        String nameMCUrl = player.getNameMCUrl().toString();
        String avatarUrl = player.createRender(RenderType.AVATAR, true).render().toString();
        String bodyUrl = player.createRender(RenderType.BODY, true).render().toString();

        String desc = constructDescription(ctx, player);
        Color color = player.isRainbow() ? Colors.randomColor() : ctx.getColor();

        EmbedBuilder eb = new EmbedBuilder()
                .setColor(color)
                .setAuthor(title, nameMCUrl, avatarUrl);
        MessageEmbed baseEmbed = eb.build();
        eb.setThumbnail(bodyUrl).setDescription(desc);

        if (!player.isPHD()) {
            String skinInfo = constructSkinInfo(ctx, player);
            String capeInfo = player.getProfile().getCapeUrl()
                    .map(url -> boldMaskedLink(ctx.i18n("capeLink"), url))
                    .orElseGet(() -> ctx.i18n("noCape"));
            String accountInfo = constructAccountInfo(ctx, player, statusOpt);
            eb.addField(lang.i18n("mc.player.skin.skin"), skinInfo, true)
                    .addField(lang.i18n("mc.player.cape.cape"), capeInfo, true)
                    .addField(ctx.i18n("account"), accountInfo, true);
        }

        String nameHistoryTitle = lang.i18n("mc.player.history.nameHistory");
        List<String> parts = buildHistoryPartitions(ctx, player);

        MessageEmbed mainEmbed = Discord.addFieldsUntilFullNoCopy(eb, nameHistoryTitle, parts);
        ctx.reply(mainEmbed);
        List<MessageEmbed> additionalEmbeds = Discord.splitEmbeds(baseEmbed, nameHistoryTitle, parts, "\n");
        for (MessageEmbed emb : additionalEmbeds) {
            ctx.reply(emb);
        }
    }

    private static @NonNull String constructDescription(CommandContext ctx, Player player) {
        Lang lang = ctx.getLang();
        UUID uuid = player.getUuid();

        String usernameLength = MarkdownUtil.bold(ctx.i18n("lettersInName")) + ": " +
                MarkdownUtil.monospace(String.valueOf(player.getUsername().length()));
        String shortUuid = MarkdownUtil.bold(lang.i18n("mc.player.uuid.shortUuid")) + ": " +
                MarkdownUtil.monospace(UUIDs.toShortString(uuid));
        String longUuid = MarkdownUtil.bold(lang.i18n("mc.player.uuid.longUuid")) + ": " +
                MarkdownUtil.monospace(UUIDs.toLongString(uuid));
        String defaultModel = MarkdownUtil.bold(lang.i18n("mc.player.skin.default")) + ": " +
                lang.i18n(player.getDefaultSkinType().getTranslationKey());

        String descriptionStart = usernameLength + "\n" + shortUuid + "\n" + longUuid + "\n";
        if (player.isPHD()) {
            String phdMessage = MarkdownUtil.bold(ctx.i18n("phd"));
            return descriptionStart + defaultModel + "\n" + phdMessage;
        } else {
            String skinType = MarkdownUtil.bold(lang.i18n("mc.player.skin.model")) + ": " +
                    lang.i18n(player.getSkinType().getTranslationKey());
            return descriptionStart + skinType + "\n" + defaultModel;
        }
    }

    private static @NonNull String constructSkinInfo(CommandContext ctx, Player player) {
        Lang lang = ctx.getLang();
        String skinLink = boldMaskedLink(ctx.i18n("skinLink"), player.getSkinUrl());
        String custom = MarkdownUtil.bold(lang.i18n("mc.player.skin.custom")) + ": " +
                lang.displayBool(player.hasCustomSkin());
        String skinHistoryLink = boldMaskedLink(ctx.i18n("skinHistoryLink"), player.getMCSkinHistoryUrl());
        return skinLink + "\n" + custom + "\n" + skinHistoryLink;
    }
    private static @NonNull String constructAccountInfo(CommandContext ctx, Player player,
                Optional<AccountStatus> statusOpt) {
        Lang lang = ctx.getLang();
        String accountType = statusOpt
                .map(lang::localize)
                .map(MarkdownUtil::bold)
                .orElseGet(() -> constructLegacyStr(ctx, player, lang));
        String demo = MarkdownUtil.bold(ctx.i18n("demo")) + ": " +
                lang.displayBool(player.getProfile().isDemo());
        String invalid = MarkdownUtil.bold(ctx.i18n("invalid")) + ": " +
                lang.displayBool(!player.getUsername().isValid());
        return accountType + "\n" + demo + "\n" + invalid;
    }
    private static @NonNull String constructLegacyStr(CommandContext ctx, Player player, Lang lang) {
        return MarkdownUtil.bold(ctx.i18n("legacy")) + ": " +
                lang.displayBool(player.getProfile().isLegacy());
    }

    private static String boldMaskedLink(String text, URL url) {
        return MarkdownUtil.bold(MarkdownUtil.maskedLink(text, url.toString()));
    }

    private static List<String> buildHistoryPartitions(CommandContext ctx, Player player) {
        List<String> nameHistoryLines = buildHistoryLines(ctx, player.getNameHistory());
        List<String> partitions = Strings.partitionLinesByLength(nameHistoryLines, MessageEmbed.VALUE_MAX_LENGTH);
        return new LinkedList<>(partitions);
    }

}