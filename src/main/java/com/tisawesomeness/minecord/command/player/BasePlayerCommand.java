package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.command.meta.CommandContext;
import com.tisawesomeness.minecord.mc.external.PlayerProvider;
import com.tisawesomeness.minecord.mc.player.NameChange;
import com.tisawesomeness.minecord.mc.player.Player;
import com.tisawesomeness.minecord.mc.player.Username;
import com.tisawesomeness.minecord.util.UUIDs;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.utils.MarkdownUtil;
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
@Slf4j
public abstract class BasePlayerCommand extends AbstractPlayerCommand {

    /**
     * Run when a player is found successfully.
     * @param ctx The context of the command
     * @param player The player
     */
    public abstract void onSuccessfulPlayer(CommandContext ctx, Player player);

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
        onSuccessfulPlayer(ctx, playerOpt.get());
    }

    /**
     * Builds a list of lines from a player's name history
     * @param ctx The context of the command
     * @param history The player's name history
     * @return A mutable list of strings, one line per name change
     */
    public static List<String> buildHistoryLines(CommandContext ctx, List<NameChange> history) {
        return buildHistoryLines(ctx, history, history.size());
    }
    /**
     * Builds a list of lines from a player's name history
     * @param ctx The context of the command
     * @param history The player's name history
     * @param limit Number of name changes to process
     * @return A mutable list of strings, one line per name change
     * @throws IndexOutOfBoundsException if limit is greater than the number of name changes
     */
    public static List<String> buildHistoryLines(CommandContext ctx, List<NameChange> history, int limit) {
        if (limit > history.size()) {
            throw new IndexOutOfBoundsException("limit must be less than history.size()");
        }
        List<String> historyLines = new ArrayList<>();
        for (int i = 0; i < limit; i++) {
            NameChange nc = history.get(i);
            int num = history.size() - i;
            historyLines.add(buildHistoryLine(ctx, nc, num));
        }
        return historyLines;
    }

    /**
     * Builds a line from a player's name history
     * @param ctx The context of the command
     * @param nc The name change
     * @param num The name change number, starting with the original name as 1 and increasing
     * @return A string with a formatted name change
     */
    public static String buildHistoryLine(CommandContext ctx, NameChange nc, int num) {
        String dateAgo = getDateAgo(ctx, nc);
        return String.format("**%d.** `%s` | %s", num, nc.getUsername(), dateAgo);
    }

    private static String getDateAgo(CommandContext ctx, NameChange nc) {
        Optional<Instant> timeOpt = nc.getTime();
        if (!timeOpt.isPresent()) {
            return MarkdownUtil.bold(ctx.getLang().i18n("mc.player.history.original"));
        }
        Instant time = timeOpt.get();
        return TimeFormat.RELATIVE.format(time);
    }

}
