package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.mc.external.PlayerProvider;
import com.tisawesomeness.minecord.mc.player.Player;
import com.tisawesomeness.minecord.mc.player.Render;
import com.tisawesomeness.minecord.mc.player.RenderType;
import com.tisawesomeness.minecord.mc.player.Username;
import com.tisawesomeness.minecord.util.ArrayUtils;
import com.tisawesomeness.minecord.util.UuidUtils;
import com.tisawesomeness.minecord.util.type.Either;

import lombok.Value;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.util.Optional;
import java.util.UUID;

/**
 * A command that extends this class can parse a player from {@code &cmd <username|uuid>} and create a Crafatar render.
 * Invalid inputs and API errors are handled automatically.
 */
public abstract class BaseRenderCommand extends AbstractPlayerCommand {

    /**
     * Starts parsing the player and render args, then calls
     * {@link #onSuccessfulRender(MessageReceivedEvent, Username, Render)} when successful.
     * @param e the event
     * @param type the type of render to create
     * @param args the command arguments
     * @param playerArgIndex the index of the player argument, 0 for {@code &cmd <player>}
     * @return the command result
     */
    protected Result parseAndSendRender(MessageReceivedEvent e, RenderType type, String[] args, int playerArgIndex) {
        // If username is quoted, the location of the scale and overlay args changes
        // and an unquoted string could also be a UUID
        String playerArg = args[playerArgIndex];
        if (Username.isQuoted(playerArg)) {
            return handleQuoted(e, type, args, playerArgIndex);
        } else {
            return handleUnquoted(e, type, args, playerArgIndex);
        }
    }

    private Result handleQuoted(MessageReceivedEvent e, RenderType type, String[] args, int playerArgIndex) {
        String argsWithUsernameStart = ArrayUtils.joinSlice(args, playerArgIndex, " ");
        Either<String, Username> errorOrUsername = validateUsername(argsWithUsernameStart);
        if (!errorOrUsername.isRight()) {
            return new Result(Command.Outcome.WARNING, errorOrUsername.getLeft());
        }
        Username username = errorOrUsername.getRight();

        // render args are processed before the username to avoid
        // unnecessary Mojang API requests in case the render args are invalid
        int argsUsed = username.argsUsed() + playerArgIndex;
        Either<String, ImpersonalRender> errorOrRender = parseRender(type, args, argsUsed, playerArgIndex);
        if (!errorOrRender.isRight()) {
            return new Result(Command.Outcome.WARNING, errorOrRender.getLeft());
        }
        ImpersonalRender irender = errorOrRender.getRight();

        PlayerProvider provider = Bot.mcLibrary.getPlayerProvider();
        newCallbackBuilder(provider.getUUID(username), e)
                .onFailure(ex -> handleIOE(ex, e, "IOE getting UUID from username " + username))
                .onSuccess(uuidOpt -> processUuid(e, uuidOpt, username, irender))
                .build();
        return new Result(Command.Outcome.SUCCESS);
    }
    private Result handleUnquoted(MessageReceivedEvent e, RenderType type, String[] args, int playerArgIndex) {
        // render args are processed before the username or UUID to avoid
        // unnecessary Mojang API requests in case the render args are invalid
        int argsUsed = playerArgIndex + 1;
        Either<String, ImpersonalRender> errorOrRender = parseRender(type, args, argsUsed, playerArgIndex);
        if (!errorOrRender.isRight()) {
            return new Result(Command.Outcome.WARNING, errorOrRender.getLeft());
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
            return new Result(Command.Outcome.SUCCESS);
        }

        Either<String, Username> errorOrUsername = validateUsername(usernameArg);
        if (!errorOrUsername.isRight()) {
            return new Result(Command.Outcome.WARNING, errorOrUsername.getLeft());
        }
        Username username = errorOrUsername.getRight();

        PlayerProvider provider = Bot.mcLibrary.getPlayerProvider();
        newCallbackBuilder(provider.getUUID(username), e)
                .onFailure(ex -> handleIOE(ex, e, "IOE getting UUID from username " + username))
                .onSuccess(uuidOpt -> processUuid(e, uuidOpt, username, irender))
                .build();
        return new Result(Command.Outcome.SUCCESS);
    }

    /**
     * Parses the render arguments.
     * @param type the type of render to create
     * @param args the command arguments
     * @param argsUsed the number of arguments used up to this point,
     *                 and the index of the next argument to be processed
     * @param playerArgIndex the index of the player argument, 0 for {@code &cmd <player>}
     * @return a render without a player attached, or a string error message
     */
    protected abstract Either<String, ImpersonalRender> parseRender(RenderType type, String[] args,
                                                                    int argsUsed, int playerArgIndex);

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

    private void processUuid(MessageReceivedEvent e, Optional<UUID> uuidOpt,
                                    Username username, ImpersonalRender irender) {
        if (uuidOpt.isPresent()) {
            UUID uuid = uuidOpt.get();
            Render render = irender.completeWith(uuid);
            onSuccessfulRender(e, username, render);
            return;
        }
        e.getChannel().sendMessage("That username does not currently exist.").queue();
    }
    private void processPlayer(MessageReceivedEvent e, Optional<Player> playerOpt, ImpersonalRender irender) {
        if (playerOpt.isPresent()) {
            Player player = playerOpt.get();
            if (player.isPHD()) {
                String msg = String.format("The account with UUID `%s` is pseudo hard-deleted (PHD), so no skin/cape is available.",
                        MarkdownUtil.monospace(player.getUuid().toString()));
                e.getChannel().sendMessage(msg).queue();
                return;
            }
            Render render = irender.completeWith(player.getUuid());
            onSuccessfulRender(e, player.getUsername(), render);
            return;
        }
        e.getChannel().sendMessage("That UUID does not currently exist.").queue();
    }

    /**
     * Called when a render is successfully created.
     * @param e the message event
     * @param username the username of the player
     * @param render the render
     */
    protected abstract void onSuccessfulRender(MessageReceivedEvent e, Username username, Render render);

    /**
     * A {@link Render} without a UUID.
     */
    @Value
    protected static class ImpersonalRender {
        RenderType type;
        boolean overlay;
        int scale;

        public Render completeWith(UUID uuid) {
            return new Render(uuid, type, overlay, scale);
        }
    }

}
