package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.lang.Lang;
import com.tisawesomeness.minecord.mc.player.Player;
import com.tisawesomeness.minecord.mc.player.Render;
import com.tisawesomeness.minecord.mc.player.RenderType;
import com.tisawesomeness.minecord.mc.player.Username;
import com.tisawesomeness.minecord.util.UUIDUtils;

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

        String input = ctx.joinArgs();
        Optional<UUID> parsedUuidOpt = UUIDUtils.fromString(input);
        if (parsedUuidOpt.isPresent()) {
            UUID uuid = parsedUuidOpt.get();
            ctx.triggerCooldown();
            processLiteralUUID(uuid, ctx);
            return;
        }

        if (input.length() > Username.MAX_LENGTH) {
            ctx.warn(ctx.getLang().i18nf("mc.player.username.tooLong", Username.MAX_LENGTH));
            return;
        }
        Username username = Username.parse(input);
        if (!username.isSupportedByMojangAPI()) {
            ctx.warn(ctx.getLang().i18n("mc.player.username.unsupportedSpecialCharacters"));
            return;
        }

        ctx.triggerCooldown();
        fireUUIDRequest(ctx, username);
    }
    private static void fireUUIDRequest(CommandContext ctx, Username username) {
        CompletableFuture<Optional<UUID>> futureUUID = ctx.getMCLibrary().getPlayerProvider().getUUID(username);
        ctx.newCallbackBuilder(futureUUID)
                .onFailure(ex -> handleIOE(ex, ctx, username))
                .onSuccess(uuidOpt -> processUUID(uuidOpt, ctx, username))
                .build();
    }

    private static void handleIOE(Throwable ex, CommandContext ctx, Username username) {
        if (ex instanceof IOException) {
            log.error("IOE getting UUID from username " + username, ex);
            ctx.err(ctx.getLang().i18n("mc.external.mojang.error"));
            return;
        }
        throw new RuntimeException(ex);
    }

    private static void processLiteralUUID(UUID uuid, CommandContext ctx) {
        String title = ctx.i18nf("uuidTitle", uuid);
        constructReply(ctx, uuid, title);
    }
    private static void processUUID(Optional<UUID> uuidOpt, CommandContext ctx, Username username) {
        if (!uuidOpt.isPresent()) {
            ctx.reply(ctx.getLang().i18n("mc.player.username.doesNotExist"));
            return;
        }
        String title = ctx.i18nf("usernameTitle", username);
        constructReply(ctx, uuidOpt.get(), title);
    }
    private static void constructReply(CommandContext ctx, UUID uuid, String title) {
        Lang lang = ctx.getLang();
        String shortUuid = MarkdownUtil.bold(lang.i18n("mc.player.uuid.short")) + ": " +
                MarkdownUtil.monospace(UUIDUtils.toShortString(uuid));
        String longUuid = MarkdownUtil.bold(lang.i18n("mc.player.uuid.long")) + ": " +
                MarkdownUtil.monospace(UUIDUtils.toLongString(uuid));
        String skinType = MarkdownUtil.bold(lang.i18n("mc.player.skin.default")) + ": " +
                lang.i18n(Player.getDefaultSkinTypeFor(uuid).getTranslationKey());
        String intArray = MarkdownUtil.bold(ctx.i18n("nbt")) + ": " +
                MarkdownUtil.monospace(UUIDUtils.toIntArrayString(uuid));
        String mostLeast = MarkdownUtil.bold(ctx.i18n("legacyNbt")) + ": " +
                MarkdownUtil.monospace(UUIDUtils.toMostLeastString(uuid));
        String desc = shortUuid + "\n" + longUuid + "\n" + skinType + "\n" + intArray + "\n" + mostLeast;
        String nameMCUrl = Player.getNameMCUrlFor(uuid).toString();
        String avatarUrl = new Render(uuid, RenderType.AVATAR, true).render().toString();

        EmbedBuilder eb = new EmbedBuilder()
                .setAuthor(title, nameMCUrl, avatarUrl)
                .setDescription(desc);
        ctx.reply(eb);
    }

}
