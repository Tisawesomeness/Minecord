package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.mc.external.PlayerProvider;
import com.tisawesomeness.minecord.mc.player.Player;
import com.tisawesomeness.minecord.mc.player.Render;
import com.tisawesomeness.minecord.mc.player.RenderType;
import com.tisawesomeness.minecord.mc.player.Username;
import com.tisawesomeness.minecord.util.UuidUtils;
import com.tisawesomeness.minecord.util.type.Either;

import lombok.Value;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.util.Optional;
import java.util.UUID;

/**
 * A command that extends this class can parse a player from {@code &cmd <username|uuid>} and create a Crafatar render.
 * Invalid inputs and API errors are handled automatically.
 */
public abstract class BaseRenderCommand extends AbstractPlayerCommand {

    @Override
    public SlashCommandData addCommandSyntax(SlashCommandData builder) {
        return builder.addOptions(new OptionData(OptionType.STRING, "player", "The player", true)
                .setMaxLength(Player.MAX_PLAYER_ARGUMENT_LENGTH));
    }

    /**
     * Starts parsing the player and render args, then calls
     * {@link #onSuccessfulRender(SlashCommandInteractionEvent, Username, Render)} when successful.
     * @param e the event
     * @param type the type of render to create
     * @return the command result
     */
    protected Result parseAndSendRender(SlashCommandInteractionEvent e, RenderType type) {
        // If username is quoted, the location of the scale and overlay args changes
        // and an unquoted string could also be a UUID
        String playerArg = e.getOption("player").getAsString();
        ImpersonalRender irender = parseRender(type, e);

        Optional<UUID> parsedUuidOpt = UuidUtils.fromString(playerArg);
        if (parsedUuidOpt.isPresent()) {
            UUID uuid = parsedUuidOpt.get();

            PlayerProvider provider = Bot.mcLibrary.getPlayerProvider();
            e.deferReply().queue();
            newCallbackBuilder(provider.getPlayer(uuid), e)
                    .onFailure(ex -> handleMojangIOE(ex, e, "IOE getting player from UUID " + uuid))
                    .onSuccess(playerOpt -> processPlayer(e, playerOpt, irender))
                    .build();
            return new Result(Outcome.SUCCESS);
        }

        Either<String, Username> errorOrUsername = validateUsername(playerArg);
        if (!errorOrUsername.isRight()) {
            return new Result(Outcome.WARNING, errorOrUsername.getLeft());
        }
        Username username = errorOrUsername.getRight();

        PlayerProvider provider = Bot.mcLibrary.getPlayerProvider();
        e.deferReply().queue();
        newCallbackBuilder(provider.getUUID(username), e)
                .onFailure(ex -> handleMojangIOE(ex, e, "IOE getting UUID from username " + username))
                .onSuccess(uuidOpt -> processUuid(e, uuidOpt, username, irender))
                .build();
        return new Result(Outcome.SUCCESS);
    }

    /**
     * Parses the render arguments.
     * @param type the type of render to create
     * @param e the event
     * @return a render without a player attached, or a string error message
     */
    protected abstract ImpersonalRender parseRender(RenderType type, SlashCommandInteractionEvent e);

    private static Either<String, Username> validateUsername(String input) {
        if (input.isEmpty()) {
            return Either.left(":warning: You must specify a player.");
        }
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

    private void processUuid(SlashCommandInteractionEvent e, Optional<UUID> uuidOpt,
                             Username username, ImpersonalRender irender) {
        if (uuidOpt.isPresent()) {
            UUID uuid = uuidOpt.get();
            Render render = irender.completeWith(uuid);
            onSuccessfulRender(e, username, render);
            return;
        }
        e.getHook().sendMessage("That username does not currently exist.").queue();
    }
    private void processPlayer(SlashCommandInteractionEvent e, Optional<Player> playerOpt, ImpersonalRender irender) {
        if (playerOpt.isPresent()) {
            Player player = playerOpt.get();
            if (player.isPHD()) {
                String msg = String.format("The account with UUID `%s` is pseudo hard-deleted (PHD), so no skin/cape is available.",
                        MarkdownUtil.monospace(player.getUuid().toString()));
                e.getHook().sendMessage(msg).queue();
                return;
            }
            Render render = irender.completeWith(player.getUuid());
            onSuccessfulRender(e, player.getUsername(), render);
            return;
        }
        e.getHook().sendMessage("That UUID does not currently exist.").queue();
    }

    /**
     * Called when a render is successfully created.
     * @param e the message event
     * @param username the username of the player
     * @param render the render
     */
    protected abstract void onSuccessfulRender(SlashCommandInteractionEvent e, Username username, Render render);

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
