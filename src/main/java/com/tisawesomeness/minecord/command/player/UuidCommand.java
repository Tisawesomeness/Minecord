package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.Lang;
import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.mc.player.Player;
import com.tisawesomeness.minecord.mc.player.Username;
import com.tisawesomeness.minecord.util.UUIDUtils;
import com.tisawesomeness.minecord.util.concurrent.FutureCallback;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class UuidCommand extends AbstractPlayerCommand {

    public @NonNull String getId() {
        return "uuid";
    }

    public void run(String[] args, CommandContext ctx) {
        if (args.length == 0) {
            ctx.showHelp();
            return;
        }

        String inputName = ctx.joinArgs();
        if (inputName.length() > Username.MAX_LENGTH) {
            ctx.warn(ctx.i18n("tooLong"));
            return;
        }
        Username username = new Username(inputName);
        if (!username.isSupportedByMojangAPI()) {
            ctx.warn(ctx.i18n("unsupportedSpecialCharacters"));
            return;
        }

        ctx.triggerCooldown();
        fireUUIDRequest(ctx, username);
    }
    private static void fireUUIDRequest(CommandContext ctx, Username username) {
        CompletableFuture<Optional<UUID>> futureUUID = ctx.getMCLibrary().getPlayerProvider().getUUID(username);
        FutureCallback.builder(futureUUID)
                .onFailure(ex -> handleIOE(ex, ctx, username))
                .onSuccess(uuidOpt -> processUUID(uuidOpt, ctx, username))
                .build();

    }

    private static void handleIOE(Throwable ex, CommandContext ctx, Username username) {
        if (ex instanceof IOException) {
            log.error("IOE getting UUID from username " + username, ex);
            ctx.err(ctx.i18n("mojangError"));
            return;
        }
        throw new RuntimeException(ex);
    }

    private static void processUUID(Optional<UUID> uuidOpt, CommandContext ctx, Username username) {
        if (uuidOpt.isEmpty()) {
            ctx.reply(ctx.i18n("usernameDoesNotExist"));
            return;
        }
        constructReply(ctx, username, uuidOpt.get());
    }
    private static void constructReply(CommandContext ctx, Username username, UUID uuid) {
        Lang lang = ctx.getLang();
        String title = ctx.i18nf("title", username);
        String shortUuid = MarkdownUtil.bold(lang.i18n("mc.player.uuid.short")) + ": " +
                MarkdownUtil.monospace(UUIDUtils.toShortString(uuid));
        String longUuid = MarkdownUtil.bold(lang.i18n("mc.player.uuid.long")) + ": " +
                MarkdownUtil.monospace(UUIDUtils.toLongString(uuid));
        String skinType = MarkdownUtil.bold(ctx.i18n("defaultSkinModel")) + ": " +
                lang.i18n(Player.getDefaultSkinTypeFor(uuid).getTranslationKey());
        String desc = shortUuid + "\n" + longUuid + "\n" + skinType;
        String nameMCUrl = Player.getNameMCUrlFor(uuid).toString();
        String avatarUrl = Player.getAvatarUrlFor(uuid).toString();

        EmbedBuilder eb = new EmbedBuilder()
                .setAuthor(title, nameMCUrl, avatarUrl)
                .setDescription(desc);
        ctx.reply(eb);
    }

}
