package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.command.meta.CommandContext;
import com.tisawesomeness.minecord.config.config.Config;
import com.tisawesomeness.minecord.lang.BoolFormat;
import com.tisawesomeness.minecord.lang.Lang;
import com.tisawesomeness.minecord.mc.external.PlayerProvider;
import com.tisawesomeness.minecord.mc.player.Player;
import com.tisawesomeness.minecord.mc.player.Render;
import com.tisawesomeness.minecord.mc.player.RenderType;
import com.tisawesomeness.minecord.mc.player.Username;
import com.tisawesomeness.minecord.util.Colors;
import com.tisawesomeness.minecord.util.UUIDs;
import com.tisawesomeness.minecord.util.type.Either;

import lombok.NonNull;
import lombok.Value;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.awt.Color;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;

public class RenderCommand extends AbstractPlayerCommand {

    /** Whether render overlay is enabled by default */
    public static final boolean DEFAULT_OVERLAY = true;

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
    public Object[] getHelpArgs(String prefix, String tag, Config config) {
        return new Object[]{prefix, tag, type.getMaxScale(), type.getDefaultScale()};
    }

    public void run(String[] args, CommandContext ctx) {
        if (args.length == 0) {
            ctx.showHelp();
            return;
        }
        parseAndSendRender(ctx, type, 0);
    }

    /**
     * Parses the username, scale, and overlay from args, then sends the render.
     * @param ctx The context of the executing command
     * @param type The type of render to send
     * @param playerArgIndex The index of the username/uuid argument (example: 1 for {@code &render body jeb_ 5})
     */
    protected static void parseAndSendRender(@NonNull CommandContext ctx, RenderType type, int playerArgIndex) {
        // If username is quoted, the location of the scale and overlay args changes
        // and an unquoted string could also be a UUID
        String playerArg = ctx.getArgs()[playerArgIndex];
        if (Username.isQuoted(playerArg)) {
            handleQuoted(ctx, type, playerArgIndex);
        } else {
            handleUnquoted(ctx, type, playerArgIndex);
        }
    }

    private static void handleQuoted(@NonNull CommandContext ctx, RenderType type, int playerArgIndex) {
        String argsWithUsernameStart = ctx.joinArgsSlice(playerArgIndex);
        Either<String, Username> errorOrUsername = validateUsername(argsWithUsernameStart, ctx.getLang());
        if (!errorOrUsername.isRight()) {
            ctx.warn(errorOrUsername.getLeft());
            return;
        }
        Username username = errorOrUsername.getRight();

        // render args are processed before the username to avoid
        // unnecessary Mojang API requests in case the render args are invalid
        int argsUsed = username.argsUsed() + playerArgIndex;
        Either<String, ImpersonalRender> errorOrRender = parseRenderFromArgs(ctx, type, argsUsed, playerArgIndex);
        if (!errorOrRender.isRight()) {
            ctx.invalidArgs(errorOrRender.getLeft());
            return;
        }
        ImpersonalRender irender = errorOrRender.getRight();

        PlayerProvider provider = ctx.getMCLibrary().getPlayerProvider();
        ctx.triggerCooldown();
        ctx.newCallbackBuilder(provider.getUUID(username))
                .onFailure(ex -> handleIOE(ex, ctx, "IOE getting UUID from username " + username))
                .onSuccess(uuidOpt -> processUuid(ctx, uuidOpt, username, irender))
                .build();
    }
    private static void handleUnquoted(@NonNull CommandContext ctx, RenderType type, int playerArgIndex) {
        // render args are processed before the username or UUID to avoid
        // unnecessary Mojang API requests in case the render args are invalid
        int argsUsed = playerArgIndex + 1;
        Either<String, ImpersonalRender> errorOrRender = parseRenderFromArgs(ctx, type, argsUsed, playerArgIndex);
        if (!errorOrRender.isRight()) {
            ctx.invalidArgs(errorOrRender.getLeft());
            return;
        }
        ImpersonalRender irender = errorOrRender.getRight();

        String usernameArg = ctx.getArgs()[playerArgIndex];
        Optional<UUID> parsedUuidOpt = UUIDs.fromString(usernameArg);
        if (parsedUuidOpt.isPresent()) {
            UUID uuid = parsedUuidOpt.get();

            ctx.triggerCooldown();
            PlayerProvider provider = ctx.getMCLibrary().getPlayerProvider();
            ctx.newCallbackBuilder(provider.getPlayer(uuid))
                    .onFailure(ex -> handleIOE(ex, ctx, "IOE getting player from UUID " + uuid))
                    .onSuccess(playerOpt -> processPlayer(ctx, playerOpt, irender))
                    .build();
            return;
        }

        Either<String, Username> errorOrUsername = validateUsername(usernameArg, ctx.getLang());
        if (!errorOrUsername.isRight()) {
            ctx.warn(errorOrUsername.getLeft());
            return;
        }
        Username username = errorOrUsername.getRight();

        ctx.triggerCooldown();
        PlayerProvider provider = ctx.getMCLibrary().getPlayerProvider();
        ctx.newCallbackBuilder(provider.getUUID(username))
                .onFailure(ex -> handleIOE(ex, ctx, "IOE getting UUID from username " + username))
                .onSuccess(uuidOpt -> processUuid(ctx, uuidOpt, username, irender))
                .build();
    }

    private static Either<String, Username> validateUsername(String input, Lang lang) {
        if (input.length() > Username.MAX_LENGTH) {
            return Either.left(lang.i18nf("mc.player.username.tooLong", Username.MAX_LENGTH));
        }
        Username username = Username.parse(input);
        if (!username.isSupportedByMojangAPI()) {
            return Either.left(lang.i18n("mc.player.username.unsupportedSpecialCharacters"));
        }
        return Either.right(username);
    }

    private static Either<String, ImpersonalRender> parseRenderFromArgs(CommandContext ctx, RenderType type,
                                                                        int argsUsed, int playerArgIndex) {
        int currentArg = argsUsed;
        Lang lang = ctx.getLang();
        String[] args = ctx.getArgs();

        if (currentArg + 2 < args.length) {
            return Either.left(lang.i18nf("command.meta.upToArgs", ctx.formatCommandName(), playerArgIndex + 3));
        }

        int scale = type.getDefaultScale();
        boolean overlay = DEFAULT_OVERLAY;
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
                    overlay = false;
                    overlaySet = true;
                    continue;
                } else if (scaleSet) {
                    // If the scale was already processed, we know this argument is the overlay
                    return Either.left(lang.i18nf("command.meta.invalidBool", arg));
                }
            }

            OptionalInt scaleOpt = lang.parseInt(arg);
            if (scaleOpt.isPresent()) {
                int potentialScale = scaleOpt.getAsInt();
                if (potentialScale < 1) {
                    return Either.left(lang.i18nf("mc.player.render.scaleLimit", type.getMaxScale()));
                }
                scale = potentialScale;
                scaleSet = true;
                continue;
            } else if (overlaySet) {
                // If the overlay was already processed, we know this argument is the scale
                return Either.left(lang.i18nf("command.meta.invalidNumber", arg));
            }

            // If reached, all attempts to process arguments failed
            return Either.left(lang.i18nf("command.meta.invalidBoolOrNumber", arg));
        }
        return Either.right(new ImpersonalRender(type, overlay, scale));
    }

    private static void processUuid(CommandContext ctx, Optional<UUID> uuidOpt,
                                    Username username, ImpersonalRender irender) {
        if (uuidOpt.isPresent()) {
            UUID uuid = uuidOpt.get();
            Render render = irender.completeWith(uuid);
            sendRenderEmbed(ctx, username, render);
            return;
        }
        ctx.reply(ctx.getLang().i18n("mc.player.username.doesNotExist"));
    }
    private static void processPlayer(CommandContext ctx, Optional<Player> playerOpt, ImpersonalRender irender) {
        Lang lang = ctx.getLang();
        if (playerOpt.isPresent()) {
            Player player = playerOpt.get();
            if (player.isPHD()) {
                ctx.reply(lang.i18nf("mc.player.phdMessage", MarkdownUtil.monospace(player.getUuid().toString())));
                return;
            }
            Render render = irender.completeWith(player.getUuid());
            sendRenderEmbed(ctx, player.getUsername(), render);
            return;
        }
        ctx.reply(lang.i18n("mc.player.uuid.doesNotExist"));
    }

    private static void sendRenderEmbed(CommandContext ctx, Username username, Render render) {
        RenderType type = render.getType();
        Lang lang = ctx.getLang();
        String renderName = lang.i18n("mc.player.render." + type.getId());

        Color color = Player.isRainbow(username) ? Colors.randomColor() : ctx.getColor();
        EmbedBuilder eb = ctx.addFooter(new EmbedBuilder())
                .setTitle(lang.i18nf("mc.player.render.title", renderName, username))
                .setImage(render.render().toString())
                .setColor(color);
        if (render.getProvidedScale() > type.getMaxScale()) {
            eb.setDescription(lang.i18nf("mc.player.render.scaleOverflow", type.getMaxScale()));
        }
        ctx.replyRaw(eb);
    }

    @Value
    private static class ImpersonalRender {
        RenderType type;
        boolean overlay;
        int scale;

        public Render completeWith(UUID uuid) {
            return new Render(uuid, type, overlay, scale);
        }
    }

}
