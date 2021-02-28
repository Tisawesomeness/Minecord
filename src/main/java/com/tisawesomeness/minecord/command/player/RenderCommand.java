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
        id = type.getId();
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
        parseAndSendRender(ctx, type, 0);
    }

    protected static void parseAndSendRender(@NonNull CommandContext ctx,
                                             @NonNull RenderType type, int playerArgIndex) {
        PlayerProvider provider = ctx.getMCLibrary().getPlayerProvider();
        String playerArg = ctx.getArgs()[playerArgIndex];

        Optional<UUID> parsedUuidOpt = UUIDUtils.fromString(playerArg);
        if (parsedUuidOpt.isPresent()) {
            UUID uuid = parsedUuidOpt.get();

            ctx.triggerCooldown();
            ctx.newCallbackBuilder(provider.getPlayer(uuid))
                    .onFailure(ex -> handleIOE(ex, ctx, "IOE getting player from UUID " + uuid))
                    .onSuccess(playerOpt -> processPlayer(ctx, playerOpt, type, playerArgIndex))
                    .build();
            return;
        }

        String input = Username.isQuoted(playerArg) ? ctx.joinArgsSlice(playerArgIndex) : playerArg;
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
                .onSuccess(uuidOpt -> processUuid(ctx, uuidOpt, username, type, playerArgIndex))
                .build();
    }

    private static void processPlayer(CommandContext ctx, Optional<Player> playerOpt,
                                      RenderType type, int playerArgIndex) {
        if (playerOpt.isPresent()) {
            Player player = playerOpt.get();
            parseRender(ctx, player.getUuid(), player.getUsername(), playerArgIndex + 1, playerArgIndex, type);
            return;
        }
        ctx.reply(ctx.getLang().i18n("mc.player.uuid.doesNotExist"));
    }
    private static void processUuid(CommandContext ctx, Optional<UUID> uuidOpt, Username username,
                                    RenderType type, int playerArgIndex) {
        if (uuidOpt.isPresent()) {
            UUID uuid = uuidOpt.get();
            parseRender(ctx, uuid, username, username.argsUsed() + playerArgIndex, playerArgIndex, type);
            return;
        }
        ctx.reply(ctx.getLang().i18n("mc.player.username.doesNotExist"));
    }

    private static void parseRender(CommandContext ctx, UUID uuid, Username username,
                                    int argsUsed, int playerArgIndex, RenderType type) {
        int currentArg = argsUsed;
        Lang lang = ctx.getLang();
        String[] args = ctx.getArgs();

        if (currentArg + 2 < args.length) {
            ctx.warn(lang.i18nf("command.meta.upToArgs", playerArgIndex + 3));
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

    private static EmbedBuilder buildRenderEmbed(CommandContext ctx, Username username, Render render) {
        RenderType type = render.getType();
        Lang lang = ctx.getLang();
        String renderName = lang.i18n("mc.player.render." + type.getId());

        EmbedBuilder eb = new EmbedBuilder()
                .setTitle(lang.i18nf("mc.player.render.title", renderName, username))
                .setImage(render.render().toString());
        if (render.getScale() > type.getMaxScale()) {
            eb.setDescription(lang.i18nf("mc.player.render.scaleOverflow", type.getMaxScale()));
        }
        return eb;
    }

}
