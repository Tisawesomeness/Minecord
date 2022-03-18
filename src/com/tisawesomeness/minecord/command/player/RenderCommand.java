package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.mc.external.PlayerProvider;
import com.tisawesomeness.minecord.mc.player.Player;
import com.tisawesomeness.minecord.mc.player.Render;
import com.tisawesomeness.minecord.mc.player.RenderType;
import com.tisawesomeness.minecord.mc.player.Username;
import com.tisawesomeness.minecord.util.*;
import com.tisawesomeness.minecord.util.type.Either;

import lombok.Value;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.awt.Color;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;

public class RenderCommand extends AbstractPlayerCommand {

    /** Whether render overlay is enabled by default */
    public static final boolean DEFAULT_OVERLAY = true;

    private final String id;
    private final RenderType type;
    public RenderCommand(RenderType type) {
        id = type.getId();
        this.type = type;
    }

    public CommandInfo getInfo() {
        return new CommandInfo(
                id,
                String.format("Shows an image of the player's %s.", type.getId()),
                "<player> [<scale>] [<overlay?>]",
                null,
                2000,
                false,
                false,
                true
        );
    }

    public String getHelp() {
        return "`{&}" + id + " <player> [<scale>] [<overlay?>]` - Shows an image of the player's " + type.getId() + ".\n" +
                "- `<player>` can be a username or a UUID.\n" +
                "- `[<scale>]` changes the image size, can be from 1 to " + type.getMaxScale() + ", defaults to " + type.getDefaultScale() + ".\n" +
                "- `[<overlay?>]` is whether to include the second skin layer, defaults to true.\n" +
                "Use `{&}help usernameInput|uuidInput|phd` for more help.\n" +
                "Note that Crafatar caches images for 20-60 minutes.\n" +
                "\n" +
                "- `{&}" + id + " avatar Tis_awesomeness`\n" +
                "- `{&}" + id + " head LadyAgnes true`\n" +
                "- `{&}" + id + " avatar f6489b797a9f49e2980e265a05dbc3af 256`\n" +
                "- `{&}" + id + " head 069a79f4-44e9-4726-a5be-fca90e38aaf5 10 overlay`\n";
    }

    public Result run(String[] args, MessageReceivedEvent e) {
        if (args.length == 0) {
            return new Result(Outcome.WARNING, ":warning: You must specify a player.");
        }
        return parseAndSendRender(e, type,  id, args,0);
    }

    /**
     * Parses the username, scale, and overlay from args, then sends the render.
     * @param e The received message event
     * @param type The type of render to send
     * @param commandId the id of the command
     * @param args The command arguments
     * @param playerArgIndex The index of the username/uuid argument (example: 1 for {@code &render body jeb_ 5})
     */
    protected static Result parseAndSendRender(MessageReceivedEvent e, RenderType type, String commandId, String[] args, int playerArgIndex) {
        // If username is quoted, the location of the scale and overlay args changes
        // and an unquoted string could also be a UUID
        String playerArg = args[playerArgIndex];
        if (Username.isQuoted(playerArg)) {
            return handleQuoted(e, type, commandId, args, playerArgIndex);
        } else {
            return handleUnquoted(e, type, commandId, args, playerArgIndex);
        }
    }

    private static Result handleQuoted(MessageReceivedEvent e, RenderType type, String commandId, String[] args, int playerArgIndex) {
        String argsWithUsernameStart = ArrayUtils.joinSlice(args, playerArgIndex, " ");
        Either<String, Username> errorOrUsername = validateUsername(argsWithUsernameStart);
        if (!errorOrUsername.isRight()) {
            return new Result(Outcome.WARNING, errorOrUsername.getLeft());
        }
        Username username = errorOrUsername.getRight();

        // render args are processed before the username to avoid
        // unnecessary Mojang API requests in case the render args are invalid
        int argsUsed = username.argsUsed() + playerArgIndex;
        Either<String, ImpersonalRender> errorOrRender = parseRenderFromArgs(type, commandId, args, argsUsed, playerArgIndex);
        if (!errorOrRender.isRight()) {
            return new Result(Outcome.WARNING, errorOrRender.getLeft());
        }
        ImpersonalRender irender = errorOrRender.getRight();

        PlayerProvider provider = Bot.mcLibrary.getPlayerProvider();
        newCallbackBuilder(provider.getUUID(username), e)
                .onFailure(ex -> handleIOE(ex, e, "IOE getting UUID from username " + username))
                .onSuccess(uuidOpt -> processUuid(e, uuidOpt, username, irender))
                .build();
        return new Result(Outcome.SUCCESS);
    }
    private static Result handleUnquoted(MessageReceivedEvent e, RenderType type, String commandId, String[] args, int playerArgIndex) {
        // render args are processed before the username or UUID to avoid
        // unnecessary Mojang API requests in case the render args are invalid
        int argsUsed = playerArgIndex + 1;
        Either<String, ImpersonalRender> errorOrRender = parseRenderFromArgs(type, commandId, args, argsUsed, playerArgIndex);
        if (!errorOrRender.isRight()) {
            return new Result(Outcome.WARNING, errorOrRender.getLeft());
        }
        ImpersonalRender irender = errorOrRender.getRight();

        String usernameArg = args[playerArgIndex];
        Optional<UUID> parsedUuidOpt = UuidUtils.fromString(usernameArg);
        if (parsedUuidOpt.isPresent()) {
            UUID uuid = parsedUuidOpt.get();

            PlayerProvider provider = Bot.mcLibrary.getPlayerProvider();
            newCallbackBuilder(provider.getPlayer(uuid), e)
                    .onFailure(ex -> handleIOE(ex, e, "IOE getting player from UUID " + uuid))
                    .onSuccess(playerOpt -> processPlayer(e, playerOpt, irender))
                    .build();
            return new Result(Outcome.SUCCESS);
        }

        Either<String, Username> errorOrUsername = validateUsername(usernameArg);
        if (!errorOrUsername.isRight()) {
            return new Result(Outcome.WARNING, errorOrUsername.getLeft());
        }
        Username username = errorOrUsername.getRight();

        PlayerProvider provider = Bot.mcLibrary.getPlayerProvider();
        newCallbackBuilder(provider.getUUID(username), e)
                .onFailure(ex -> handleIOE(ex, e, "IOE getting UUID from username " + username))
                .onSuccess(uuidOpt -> processUuid(e, uuidOpt, username, irender))
                .build();
        return new Result(Outcome.SUCCESS);
    }

    private static Either<String, Username> validateUsername(String input) {
        if (input.length() > Username.MAX_LENGTH) {
            String msg = String.format("Usernames must be %d characters or less.", Username.MAX_LENGTH);
            return Either.left(msg);
        }
        Username username = Username.parse(input);
        if (!username.isSupportedByMojangAPI()) {
            return Either.left("Unfortunately, the Mojang API does not support special characters " +
                    "other than spaces and `_!@$-.?`");
        }
        return Either.right(username);
    }

    private static Either<String, ImpersonalRender> parseRenderFromArgs(RenderType type, String commandId,
                                                                        String[] args, int argsUsed, int playerArgIndex) {
        int currentArg = argsUsed;

        if (currentArg + 2 < args.length) {
            String msg = String.format("&%s takes up to %d arguments.", commandId, playerArgIndex + 3);
            return Either.left(msg);
        }

        int scale = type.getDefaultScale();
        boolean overlay = DEFAULT_OVERLAY;
        // Each argument type should only be processed once
        boolean scaleSet = false;
        boolean overlaySet = false;

        // [<scale>] and [<overlay?>] can be in any order
        while (currentArg < args.length) {
            String arg = args[currentArg++]; // Current arg is incremented for next loop no matter what route

            if (!overlaySet) {
                if (arg.equalsIgnoreCase("Overlay") || StringUtils.isTruthy(arg)) {
                    overlay = true;
                    overlaySet = true;
                    continue;
                } else if (StringUtils.isFalsy(arg)) {
                    overlay = false;
                    overlaySet = true;
                    continue;
                } else if (scaleSet) {
                    // If the scale was already processed, we know this argument is the overlay
                    return Either.left(arg + " is not a valid true/false value.");
                }
            }

            OptionalInt scaleOpt = MathUtils.safeParseInt(arg);
            if (scaleOpt.isPresent()) {
                int potentialScale = Integer.parseInt(arg);
                if (potentialScale < 1) {
                    String msg = String.format("The scale must be from 1 to %d.", type.getMaxScale());
                    return Either.left(msg);
                }
                scale = potentialScale;
                scaleSet = true;
                continue;
            } else if (overlaySet) {
                // If the overlay was already processed, we know this argument is the scale
                return Either.left(arg + " is not a valid number.");
            }

            // If reached, all attempts to process arguments failed
            return Either.left(arg + " is not a valid true/false value or number.");
        }
        return Either.right(new ImpersonalRender(type, overlay, scale));
    }

    private static void processUuid(MessageReceivedEvent e, Optional<UUID> uuidOpt,
                                    Username username, ImpersonalRender irender) {
        if (uuidOpt.isPresent()) {
            UUID uuid = uuidOpt.get();
            Render render = irender.completeWith(uuid);
            sendRenderEmbed(e, username, render);
            return;
        }
        e.getChannel().sendMessage("That username does not currently exist.").queue();
    }
    private static void processPlayer(MessageReceivedEvent e, Optional<Player> playerOpt, ImpersonalRender irender) {
        if (playerOpt.isPresent()) {
            Player player = playerOpt.get();
            if (player.isPHD()) {
                String msg = String.format("The account with UUID `%s` is pseudo hard-deleted (PHD), so no skin/cape is available.",
                        MarkdownUtil.monospace(player.getUuid().toString()));
                e.getChannel().sendMessage(msg).queue();
                return;
            }
            Render render = irender.completeWith(player.getUuid());
            sendRenderEmbed(e, player.getUsername(), render);
            return;
        }
        e.getChannel().sendMessage("That UUID does not currently exist.").queue();
    }

    private static void sendRenderEmbed(MessageReceivedEvent e, Username username, Render render) {
        RenderType type = render.getType();

        Color color = Player.isRainbow(username) ? ColorUtils.randomColor() : Bot.color;
        String title = type + " for " + username;
        EmbedBuilder eb = MessageUtils.addFooter(new EmbedBuilder())
                .setTitle(title)
                .setImage(render.render().toString())
                .setColor(color);
        if (render.getProvidedScale() > type.getMaxScale()) {
            String msg = String.format("The scale was too high, so it was set to the max, %d.", type.getMaxScale());
            eb.setDescription(msg);
        }
        e.getChannel().sendMessageEmbeds(eb.build()).queue();
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
