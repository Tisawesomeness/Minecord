package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.mc.player.NameChange;
import com.tisawesomeness.minecord.mc.player.Player;
import com.tisawesomeness.minecord.mc.player.PlayerProvider;
import com.tisawesomeness.minecord.mc.player.Username;
import com.tisawesomeness.minecord.util.TimeUtils;
import com.tisawesomeness.minecord.util.UUIDUtils;
import com.tisawesomeness.minecord.util.concurrent.FutureCallback;
import com.tisawesomeness.minecord.util.discord.LocalizedMarkdownBuilder;
import com.tisawesomeness.minecord.util.type.IntegralDuration;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
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
        Optional<UUID> parsedUuidOpt = UUIDUtils.fromString(input);
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
            ctx.warn(ctx.getLang().i18n("mc.player.username.tooLong"));
            return;
        }
        Username username = new Username(input);
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

    private static void handleIOE(Throwable ex, CommandContext ctx, String errorMessage) {
        if (ex instanceof IOException) {
            log.error(errorMessage, ex);
            ctx.err(ctx.getLang().i18n("mc.external.mojang.error"));
            return;
        }
        throw new RuntimeException(ex);
    }
    private void processPlayer(Optional<Player> playerOpt, CommandContext ctx, String i18nKey) {
        if (playerOpt.isEmpty()) {
            ctx.reply(ctx.getLang().i18n(i18nKey));
            return;
        }
        onSuccessfulPlayer(ctx, playerOpt.get());
    }

    /**
     * Builds a list of lines from a player's name history
     * @param ctx The context of the command
     * @param format The date format
     * @param history The player's name history
     * @return A mutable list of strings, one line per name change
     */
    public static List<String> buildHistoryLines(CommandContext ctx, TimeUtils.Format format,
                                                 List<NameChange> history) {
        Instant now = Instant.now();
        List<String> historyLines = new ArrayList<>();
        for (int i = 0; i < history.size(); i++) {
            NameChange nc = history.get(i);
            int num = history.size() - i;
            String dateAgo = getDateAgo(ctx, now, format, nc);
            historyLines.add(String.format("**%d.** `%s` | %s", num, nc.getUsername(), dateAgo));
        }
        return historyLines;
    }
    private static String getDateAgo(CommandContext ctx, Temporal now, TimeUtils.Format format, NameChange nc) {
        Optional<Instant> timeOpt = nc.getTime();
        if (timeOpt.isEmpty()) {
            return MarkdownUtil.bold(ctx.getLang().i18n("mc.player.history.original"));
        }
        Instant time = timeOpt.get();

        Duration duration = Duration.between(time, now);
        IntegralDuration truncated = IntegralDuration.fromDuration(duration, ChronoUnit.SECONDS, ChronoUnit.DAYS);
        Optional<LocalizedMarkdownBuilder> builderOpt = TimeUtils.localizeIntegralDurationBuilder(
                truncated, ctx.getLang());
        String ago = builderOpt
                .map(localizedMarkdownBuilder -> localizedMarkdownBuilder
                        .bold(0, NumberFormat.Field.INTEGER)
                        .build())
                .orElseGet(() -> TimeUtils.localizedNoDuration(ctx.getLang()));

        String date = TimeUtils.format(time, ctx.getLocale(), format);
        return String.format("%s (%s)", date, ago);
    }

}
