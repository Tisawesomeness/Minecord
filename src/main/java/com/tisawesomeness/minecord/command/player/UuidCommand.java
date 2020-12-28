package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.Lang;
import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.command.Result;
import com.tisawesomeness.minecord.mc.player.Player;
import com.tisawesomeness.minecord.mc.player.Username;
import com.tisawesomeness.minecord.util.UUIDUtils;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class UuidCommand extends AbstractPlayerCommand {

    public @NonNull String getId() {
        return "uuid";
    }

    public Result run(String[] args, CommandContext ctx) {
        if (args.length == 0) {
            return ctx.showHelp();
        }

        String inputName = ctx.joinArgs();
        if (inputName.length() > Username.MAX_LENGTH) {
            return ctx.warn(ctx.i18n("tooLong"));
        }
        Optional<Username> usernameOpt = Username.from(inputName);
        if (usernameOpt.isEmpty()) {
            return ctx.warn(ctx.i18n("unsupportedSpecialCharacters"));
        }
        Username username = usernameOpt.get();

        ctx.triggerCooldown();
        Optional<UUID> uuidOpt;
        try {
            uuidOpt = ctx.getMCLibrary().getPlayerProvider().getUUID(username);
        } catch (IOException ex) {
            log.error("IOE getting UUID from username " + username, ex);
            return ctx.err(ctx.i18n("mojangError"));
        }
        if (uuidOpt.isEmpty()) {
            return ctx.reply(ctx.i18n("usernameDoesNotExist"));
        }
        UUID uuid = uuidOpt.get();

        return constructReply(ctx, username, uuid);
    }

    private static Result constructReply(CommandContext ctx, Username username, UUID uuid) {
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
        return ctx.reply(eb);
    }

}
