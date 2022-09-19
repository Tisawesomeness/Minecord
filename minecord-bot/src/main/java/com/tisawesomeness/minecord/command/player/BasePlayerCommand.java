package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.command.meta.CommandContext;
import com.tisawesomeness.minecord.lang.Lang;
import com.tisawesomeness.minecord.mc.external.PlayerProvider;
import com.tisawesomeness.minecord.mc.player.Player;
import com.tisawesomeness.minecord.mc.player.Username;
import com.tisawesomeness.minecord.util.UUIDs;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.util.Optional;
import java.util.UUID;

/**
 * A command that extends this class will parse a player from {@code &cmd <username|uuid>}.
 * Invalid inputs and API errors are handled automatically.
 */
@Slf4j
public abstract class BasePlayerCommand extends AbstractPlayerCommand {

    /**
     * Run when a player is found successfully.
     * @param ctx The context of the command
     * @param player The player
     */
    protected abstract void onSuccessfulPlayer(CommandContext ctx, Player player);

    public void run(String[] args, CommandContext ctx) {
        if (args.length == 0) {
            ctx.showHelp();
            return;
        }
        PlayerProvider provider = ctx.getMCLibrary().getPlayerProvider();

        String input = ctx.joinArgs();
        Optional<UUID> parsedUuidOpt = UUIDs.fromString(input);
        if (parsedUuidOpt.isPresent()) {
            UUID uuid = parsedUuidOpt.get();

            ctx.triggerCooldown();
            ctx.newCallbackBuilder(provider.getPlayer(uuid))
                    .onFailure(ex -> handleIOE(ex, ctx, "IOE getting player from UUID " + uuid))
                    .onSuccess(playerOpt ->
                            processPlayer(playerOpt, ctx, "mc.player.uuid.doesNotExist"))
                    .build();
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
        ctx.newCallbackBuilder(provider.getPlayer(username))
                .onFailure(ex -> handleIOE(ex, ctx, "IOE getting player from username " + username))
                .onSuccess(playerOpt ->
                        processPlayer(playerOpt, ctx, "mc.player.username.doesNotExist"))
                .build();
    }

    private void processPlayer(Optional<Player> playerOpt, CommandContext ctx, String i18nKey) {
        if (!playerOpt.isPresent()) {
            ctx.reply(ctx.getLang().i18n(i18nKey));
            return;
        }
        Player player = playerOpt.get();
        if (player.isPHD()) {
            Lang lang = ctx.getLang();
            ctx.reply(lang.i18nf("mc.player.phdMessage", MarkdownUtil.monospace(player.getUuid().toString())));
            return;
        }
        onSuccessfulPlayer(ctx, player);
    }

}
