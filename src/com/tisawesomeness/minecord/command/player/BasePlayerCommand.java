package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.mc.external.PlayerProvider;
import com.tisawesomeness.minecord.mc.player.NameChange;
import com.tisawesomeness.minecord.mc.player.Player;
import com.tisawesomeness.minecord.mc.player.Username;
import com.tisawesomeness.minecord.util.UuidUtils;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.TimeFormat;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * A command that extends this class will parse a player from {@code &cmd <username|uuid>}.
 * Invalid inputs and API errors are handled automatically.
 */
public abstract class BasePlayerCommand extends AbstractPlayerCommand {

    /**
     * @return whether pseudo hard-deleted players should be automatically rejected with an error message
     * before the command is run
     */
    protected abstract boolean shouldRejectPHD();

    /**
     * Run when a player is found successfully.
     * @param e The received event
     * @param player The player
     */
    protected abstract void onSuccessfulPlayer(MessageReceivedEvent e, Player player);

    public Result run(String[] args, MessageReceivedEvent e) {
        if (args.length == 0) {
            return new Result(Outcome.WARNING, ":warning: You must specify a player.");
        }
        PlayerProvider provider = Bot.mcLibrary.getPlayerProvider();

        String input = String.join(" ", args);
        Optional<UUID> parsedUuidOpt = UuidUtils.fromString(input);
        if (parsedUuidOpt.isPresent()) {
            UUID uuid = parsedUuidOpt.get();

            String msg = "That UUID does not currently exist.";
            newCallbackBuilder(provider.getPlayer(uuid), e)
                    .onFailure(ex -> handleIOE(ex, e, "IOE getting player from UUID " + uuid))
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
        newCallbackBuilder(provider.getPlayer(username), e)
                .onFailure(ex -> handleIOE(ex, e, "IOE getting player from username " + username))
                .onSuccess(playerOpt -> processPlayer(playerOpt, e, msg))
                .build();
        return new Result(Outcome.SUCCESS);
    }

    private void processPlayer(Optional<Player> playerOpt, MessageReceivedEvent e, String notFoundMessage) {
        if (!playerOpt.isPresent()) {
            e.getChannel().sendMessage(notFoundMessage).queue();
            return;
        }
        Player player = playerOpt.get();
        if (shouldRejectPHD() && player.isPHD()) {
            String msg = String.format("The account with UUID `%s` is pseudo hard-deleted (PHD), so no skin/cape is available.",
                    player.getUuid());
            e.getChannel().sendMessage(msg).queue();
            return;
        }
        onSuccessfulPlayer(e, player);
    }

    /**
     * Builds a list of lines from a player's name history
     * @param history The player's name history
     * @return A mutable list of strings, one line per name change
     */
    public static List<String> buildHistoryLines(List<NameChange> history) {
        return buildHistoryLines(history, history.size());
    }
    /**
     * Builds a list of lines from a player's name history
     * @param history The player's name history
     * @param limit Number of name changes to process
     * @return A mutable list of strings, one line per name change
     * @throws IndexOutOfBoundsException if limit is greater than the number of name changes
     */
    public static List<String> buildHistoryLines(List<NameChange> history, int limit) {
        if (limit > history.size()) {
            throw new IndexOutOfBoundsException("limit must be less than history.size()");
        }
        List<String> historyLines = new ArrayList<>();
        for (int i = 0; i < limit; i++) {
            NameChange nc = history.get(i);
            int num = history.size() - i;
            historyLines.add(buildHistoryLine(nc, num));
        }
        return historyLines;
    }

    /**
     * Builds a line from a player's name history
     * @param nc The name change
     * @param num The name change number, starting with the original name as 1 and increasing
     * @return A string with a formatted name change
     */
    public static String buildHistoryLine(NameChange nc, int num) {
        String dateAgo = getDateAgo(nc);
        return String.format("**%d.** `%s` | %s", num, nc.getUsername(), dateAgo);
    }

    private static String getDateAgo(NameChange nc) {
        Optional<Instant> timeOpt = nc.getTime();
        if (!timeOpt.isPresent()) {
            return "**Original**";
        }
        Instant time = timeOpt.get();
        return TimeFormat.RELATIVE.format(time);
    }

}
