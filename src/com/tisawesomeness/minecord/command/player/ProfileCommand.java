package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.mc.external.PlayerProvider;
import com.tisawesomeness.minecord.mc.player.AccountStatus;
import com.tisawesomeness.minecord.mc.player.Player;
import com.tisawesomeness.minecord.mc.player.ProfileAction;
import com.tisawesomeness.minecord.mc.player.RenderType;
import com.tisawesomeness.minecord.util.ColorUtils;
import com.tisawesomeness.minecord.util.UuidUtils;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.awt.*;
import java.net.URL;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ProfileCommand extends BasePlayerCommand {

    public CommandInfo getInfo() {
        return new CommandInfo(
                "profile",
                "Shows info for a Minecraft account.",
                "<player>",
                1000,
                false,
                false
        );
    }

    @Override
    public String[] getLegacyAliases() {
        return new String[]{"p", "player"};
    }

    @Override
    public String getHelp() {
        return "`{&}profile <player>` - Shows info for a Minecraft account.\n" +
                "- `<player>` can be a username or UUID.\n" +
                "Use `{&}help usernameInput|uuidInput|phd` for more help.\n" +
                "\n" +
                "Examples:\n" +
                "- `{&}profile Tis_awesomeness`\n" +
                "- `{&}profile LadyAgnes`\n" +
                "- `{&}profile f6489b797a9f49e2980e265a05dbc3af`\n" +
                "- `{&}profile 069a79f4-44e9-4726-a5be-fca90e38aaf5`\n";
    }

    @Override
    protected void onSuccessfulPlayer(SlashCommandInteractionEvent e, Player player) {
        PlayerProvider provider = Bot.mcLibrary.getPlayerProvider();
        if (provider.isStatusAPIEnabled()) {
            CompletableFuture<Optional<AccountStatus>> future = provider.getAccountStatus(player.getUuid())
                    .exceptionally(ex -> {
                        handleIOE(ex, e, "IOE getting account status for " + player.getUuid());
                        return Optional.empty();
                    });
            newCallbackBuilder(future, e)
                    .onSuccess(accountStatusOpt -> onSuccessfulStatus(e, player, accountStatusOpt))
                    .build();
        } else {
            onSuccessfulStatus(e, player, Optional.empty());
        }
    }

    private void onSuccessfulStatus(SlashCommandInteractionEvent e, Player player, Optional<AccountStatus> statusOpt) {
        String title = "Profile for " + player.getUsername();
        String nameMCUrl = player.getNameMCUrl().toString();
        String avatarUrl = player.createRender(RenderType.AVATAR, true).render().toString();
        String bodyUrl = player.createRender(RenderType.BODY, true).render().toString();

        String desc = constructDescription(player);
        Color color = player.isRainbow() ? ColorUtils.randomColor() : Bot.color;

        EmbedBuilder eb = new EmbedBuilder()
                .setColor(color)
                .setAuthor(title, nameMCUrl, avatarUrl)
                .setThumbnail(bodyUrl)
                .setDescription(desc);

        if (!player.isPHD()) {
            String skinInfo = constructSkinInfo(player);
            String capeInfo = player.getProfile().getCapeUrl()
                    .map(url -> boldMaskedLink("View Cape", url))
                    .orElse("No Cape");
            String accountInfo = constructAccountInfo(player, statusOpt);
            eb.addField("Skin", skinInfo, true)
                    .addField("Cape", capeInfo, true)
                    .addField("Account", accountInfo, true);
        }

        String nameHistory = "Mojang has removed the name history API, [read more here](https://help.minecraft.net/hc/en-us/articles/8969841895693). We are working on a different way to retrieve name history, stay tuned.";
        eb.addField("Name History", nameHistory, true);
        uploadOrEmbedImages(e, eb.build());
    }

    private static @NonNull String constructDescription(Player player) {
        UUID uuid = player.getUuid();

        String usernameLength = String.format("**Letters in Username**: `%d`", player.getUsername().length());
        String shortUuid = String.format("**Short UUID**: `%s`", UuidUtils.toShortString(uuid));
        String longUuid = String.format("**Long UUID**: `%s`", UuidUtils.toLongString(uuid));
        String defaultModel = "**Default Skin Model**: " + player.getDefaultSkinModel().getDescription();
        String newDefaultModel = "**1.19.3+ Default Skin**: " + player.getNewDefaultSkin();

        String descriptionStart = usernameLength + "\n" + shortUuid + "\n" + longUuid + "\n";
        String defaultModels = defaultModel + "\n" + newDefaultModel;
        if (player.isPHD()) {
            return "__This player is pseudo hard-deleted (PHD)!__\n\n" + descriptionStart + defaultModels;
        }
        String skinModel = "**Skin Model**: " + player.getSkinModel().getDescription();
        String description = descriptionStart + skinModel + "\n" + defaultModels;
        if (player.getProfile().getProfileActions().contains(ProfileAction.FORCED_NAME_CHANGE)) {
            return "__Cannot play multiplayer due to banned name.__\n\n" + description;
        }
        return description;
    }

    private static @NonNull String constructSkinInfo(Player player) {
        String skinLink = boldMaskedLink("View Skin", player.getSkinUrl());
        String custom = "**Custom**: " + displayBool(player.hasCustomSkin());
        String skinHistoryLink = boldMaskedLink("Skin History", player.getMCSkinHistoryUrl());
        String skinInfo = skinLink + "\n" + custom + "\n" + skinHistoryLink;
        if (player.getProfile().getProfileActions().contains(ProfileAction.USING_BANNED_SKIN)) {
            String banNotice = player.hasCustomSkin() ? "__Banned Skin__" : "__Banned Skin__ (reset)";
            return banNotice + "\n" + skinInfo;
        }
        return skinInfo;
    }
    private static @NonNull String constructAccountInfo(Player player, Optional<AccountStatus> statusOpt) {
        String accountType = statusOpt
                .map(AccountStatus::getName)
                .map(MarkdownUtil::bold)
                .orElseGet(() -> constructLegacyStr(player));
        String demo = "**Demo**: " + displayBool(player.getProfile().isDemo());
        String invalid = "**Invalid**: " + displayBool(!player.getUsername().isValid());
        return accountType + "\n" + demo + "\n" + invalid;
    }
    private static @NonNull String constructLegacyStr(Player player) {
        return "**Legacy**: " + displayBool(player.getProfile().isLegacy());
    }

    private static String boldMaskedLink(String text, URL url) {
        return MarkdownUtil.bold(MarkdownUtil.maskedLink(text, url.toString()));
    }
    private static String displayBool(boolean bool) {
        return bool ? "True" : "False";
    }

}