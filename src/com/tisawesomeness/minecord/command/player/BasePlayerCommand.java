package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.OptionTypes;
import com.tisawesomeness.minecord.mc.external.PlayerProvider;
import com.tisawesomeness.minecord.mc.player.Player;
import com.tisawesomeness.minecord.mc.player.Username;
import com.tisawesomeness.minecord.util.UuidUtils;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Optional;
import java.util.UUID;

/**
 * A command that extends this class will parse a player from {@code &cmd <username|uuid>}.
 * Invalid inputs and API errors are handled automatically.
 */
public abstract class BasePlayerCommand extends AbstractPlayerCommand {

    /**
     * Run when a player is found successfully.
     * @param e The received event
     * @param player The player
     */
    protected abstract void onSuccessfulPlayer(SlashCommandInteractionEvent e, Player player);

    @Override
    public SlashCommandData addCommandSyntax(SlashCommandData builder) {
        return builder.addOptions(new OptionData(OptionType.STRING, "player", "The player", true));
    }

    public Result run(SlashCommandInteractionEvent e) {
        PlayerProvider provider = Bot.mcLibrary.getPlayerProvider();

        String player = getOption(e, "player", OptionTypes.STRING);
        if (player == null) {
            return Result.SLASH_COMMAND_FAIL;
        }
        String input = String.join(" ", player);
        Optional<UUID> parsedUuidOpt = UuidUtils.fromString(input);
        if (parsedUuidOpt.isPresent()) {
            UUID uuid = parsedUuidOpt.get();

            String msg = "That UUID does not currently exist.";
            e.deferReply().queue();
            newCallbackBuilder(provider.getPlayer(uuid), e)
                    .onFailure(ex -> handleMojangIOE(ex, e, "IOE getting player from UUID " + uuid))
                    .onSuccess(playerOpt -> processPlayer(playerOpt, e, msg))
                    .build();
            return new Result(Outcome.SUCCESS);
        }

        if (input.length() > Username.MAX_LENGTH) {
            String msg = String.format(":warning: Usernames must be %d characters or less.", Username.MAX_LENGTH);
            return new Result(Outcome.WARNING, msg);
        }
        Username username = Username.parse(input);
        if (!username.isSupportedByMojangAPI()) {
            return new Result(Outcome.WARNING, ":warning: Unfortunately, the Mojang API does not support " +
                    "special characters other than spaces and `_!@$-.?`");
        }

        String msg = "That username does not currently exist.";
        e.deferReply().queue();
        newCallbackBuilder(provider.getPlayer(username), e)
                .onFailure(ex -> handleMojangIOE(ex, e, "IOE getting player from username " + username))
                .onSuccess(playerOpt -> processPlayer(playerOpt, e, msg))
                .build();
        return new Result(Outcome.SUCCESS);
    }

    private void processPlayer(Optional<Player> playerOpt, SlashCommandInteractionEvent e, String notFoundMessage) {
        if (!playerOpt.isPresent()) {
            e.getHook().sendMessage(notFoundMessage).queue();
            return;
        }
        Player player = playerOpt.get();
        if (player.isPHD()) {
            String msg = String.format("The account with UUID `%s` is pseudo hard-deleted (PHD), so no information is available.",
                    player.getUuid());
            e.getHook().sendMessage(msg).queue();
            return;
        }
        onSuccessfulPlayer(e, player);
    }

}
