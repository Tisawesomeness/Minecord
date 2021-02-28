package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.lang.BoolFormat;
import com.tisawesomeness.minecord.lang.Lang;
import com.tisawesomeness.minecord.mc.player.*;
import com.tisawesomeness.minecord.util.UUIDUtils;

import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;

public class RenderCommand extends AbstractPlayerCommand {

    private final @NonNull String id;
    private final RenderType type;
    public RenderCommand(RenderType type) {
        id = type.getName();
        this.type = type;
    }

    public @NonNull String getId() {
        return id;
    }

    @Override
    public Object[] getHelpArgs(String prefix, String tag) {
        return new Object[]{prefix, tag, type.getMaxScale(), type.getDefaultScale()};
    }

    public void run(String[] args, CommandContext ctx) {
        if (args.length == 0) {
            ctx.showHelp();
            return;
        }
        PlayerProvider provider = ctx.getMCLibrary().getPlayerProvider();

        Optional<UUID> parsedUuidOpt = UUIDUtils.fromString(args[0]);
        if (parsedUuidOpt.isPresent()) {
            UUID uuid = parsedUuidOpt.get();

            ctx.triggerCooldown();
            ctx.newCallbackBuilder(provider.getPlayer(uuid))
                    .onFailure(ex -> handleIOE(ex, ctx, "IOE getting player from UUID " + uuid))
                    .onSuccess(playerOpt -> processPlayer(ctx, playerOpt))
                    .build();
            return;
        }

        String input = Username.isQuoted(args[0]) ? ctx.joinArgs() : args[0];
        if (input.length() > Username.MAX_LENGTH) {
            ctx.warn(ctx.getLang().i18n("mc.player.username.tooLong"));
            return;
        }
        Username username = Username.parse(input);
        if (!username.isSupportedByMojangAPI()) {
            ctx.warn(ctx.getLang().i18n("mc.player.username.unsupportedSpecialCharacters"));
            return;
        }

        ctx.triggerCooldown();
        ctx.newCallbackBuilder(provider.getUUID(username))
                .onFailure(ex -> handleIOE(ex, ctx, "IOE getting UUID from username " + username))
                .onSuccess(uuidOpt -> processUuid(ctx, uuidOpt, username))
                .build();
    }

    private void processPlayer(CommandContext ctx, Optional<Player> playerOpt) {
        if (playerOpt.isPresent()) {
            Player player = playerOpt.get();
            parseRender(ctx, player.getUuid(), player.getUsername(), 1);
            return;
        }
        ctx.reply(ctx.getLang().i18n("mc.player.uuid.doesNotExist"));
    }
    private void processUuid(CommandContext ctx, Optional<UUID> uuidOpt, Username username) {
        if (uuidOpt.isPresent()) {
            UUID uuid = uuidOpt.get();
            parseRender(ctx, uuid, username, username.argsUsed());
            return;
        }
        ctx.reply(ctx.getLang().i18n("mc.player.username.doesNotExist"));
    }

    private void parseRender(CommandContext ctx, UUID uuid, Username username, int argsUsed) {
        int currentArg = argsUsed;
        Lang lang = ctx.getLang();
        String[] args = ctx.getArgs();

        if (currentArg + 2 < args.length) {
            ctx.warn(lang.i18nf("command.meta.upToArgs", 3));
            return;
        }

        int scale = type.getDefaultScale();
        boolean overlay = false;
        // Each argument type should only be processed once
        boolean scaleSet = false;
        boolean overlaySet = false;

        // [scale] and [overlay?] can be in any order
        while (currentArg < args.length) {
            String arg = args[currentArg++]; // Current arg is incremented for next loop no matter what route

            if (!overlaySet) {
                String overlayStr = lang.i18n("mc.player.render.overlay");
                if (lang.equalsIgnoreCase(arg, overlayStr) || lang.isTruthy(arg, BoolFormat.TRUE_OR_YES)) {
                    overlay = true;
                    overlaySet = true;
                    continue;
                } else if (lang.isFalsy(arg, BoolFormat.TRUE_OR_YES)) {
                    // overlay is already false
                    overlaySet = true;
                    continue;
                } else if (scaleSet) {
                    // If the scale was already processed, we know this argument is the overlay
                    ctx.warn(lang.i18nf("command.meta.invalidBool", arg));
                    return;
                }
            }

            OptionalInt scaleOpt = lang.parseInt(arg);
            if (scaleOpt.isPresent()) {
                int potentialScale = scaleOpt.getAsInt();
                if (potentialScale < 1) {
                    ctx.warn(lang.i18nf("mc.player.render.scaleLimit", type.getMaxScale()));
                    return;
                }
                scale = potentialScale;
                scaleSet = true;
                continue;
            } else if (overlaySet) {
                // If the overlay was already processed, we know this argument is the scale
                ctx.warn(lang.i18nf("command.meta.invalidNumber", arg));
                return;
            }

            // If reached, all attempts to process arguments failed
            ctx.warn(lang.i18nf("command.meta.invalidBoolOrNumber", arg));
            return;
        }

        Render render = new Render(uuid, type, overlay, scale);
        ctx.reply(buildRenderEmbed(ctx, username, render));
    }

    private EmbedBuilder buildRenderEmbed(CommandContext ctx, Username username, Render render) {
        EmbedBuilder eb = new EmbedBuilder()
                .setTitle(ctx.i18nf("title", username))
                .setImage(render.render().toString());
        if (render.getScale() > type.getMaxScale()) {
            eb.setDescription(ctx.getLang().i18nf("mc.player.render.scaleOverflow", type.getMaxScale()));
        }
        return eb;
    }

}
