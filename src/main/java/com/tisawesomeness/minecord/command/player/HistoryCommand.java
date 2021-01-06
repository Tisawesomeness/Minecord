package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.mc.player.NameChange;
import com.tisawesomeness.minecord.mc.player.Player;
import com.tisawesomeness.minecord.mc.player.PlayerProvider;
import com.tisawesomeness.minecord.mc.player.Username;
import com.tisawesomeness.minecord.util.*;
import com.tisawesomeness.minecord.util.concurrent.FutureCallback;
import com.tisawesomeness.minecord.util.type.IntegralDuration;

import com.google.common.base.Splitter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.*;

@Slf4j
public class HistoryCommand extends AbstractPlayerCommand {

    public @NonNull String getId() {
        return "history";
    }

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
            FutureCallback.builder(provider.getPlayer(uuid))
                    .onFailure(ex -> handleIOE(ex, ctx, "IOE getting player from UUID " + uuid))
                    .onSuccess(playerOpt -> processPlayer(playerOpt, ctx, "mc.player.uuid.doesNotExist"))
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
        FutureCallback.builder(provider.getPlayer(username))
                .onFailure(ex -> handleIOE(ex, ctx, "IOE getting player from username " + username))
                .onSuccess(playerOpt -> processPlayer(playerOpt, ctx, "mc.player.username.doesNotExist"))
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

    private static void processPlayer(Optional<Player> playerOpt, CommandContext ctx, String i18nKey) {
        if (playerOpt.isEmpty()) {
            ctx.reply(ctx.getLang().i18n(i18nKey));
            return;
        }
        Player player = playerOpt.get();
        sendNameHistory(ctx, player);
    }
    private static void sendNameHistory(CommandContext ctx, Player player) {
        List<String> historyLines = buildHistoryLines(ctx, player.getNameHistory());
        MessageEmbed baseEmbed = constructBaseEmbed(ctx, player);
        int remainingLength = MessageEmbed.EMBED_MAX_LENGTH_BOT - baseEmbed.getLength();
        List<String> historyPartitions = StringUtils.partitionLinesByLength(historyLines, remainingLength);
        for (String partition : historyPartitions) {
            replyWithPartition(ctx, baseEmbed, partition);
        }
    }

    private static List<String> buildHistoryLines(CommandContext ctx, List<NameChange> history) {
        Instant now = Instant.now();
        List<String> historyLines = new ArrayList<>();
        for (int i = 0; i < history.size(); i++) {
            NameChange nc = history.get(i);
            int num = history.size() - i;
            String dateAgo = getDateAgo(ctx, now, nc);
            historyLines.add(String.format("**%d.** `%s` | %s", num, nc.getUsername(), dateAgo));
        }
        return historyLines;
    }
    private static String getDateAgo(CommandContext ctx, Temporal now, NameChange nc) {
        Optional<Instant> timeOpt = nc.getTime();
        if (timeOpt.isPresent()) {
            Instant time = timeOpt.get();
            String date = TimeUtils.formatDateTime(time, ctx.getLocale());
            Duration duration = Duration.between(time, now);
            IntegralDuration truncated = IntegralDuration.fromDuration(duration, ChronoUnit.SECONDS, ChronoUnit.DAYS);
            String ago = TimeUtils.localizeIntegralDuration(truncated, ctx.getLang());
            return String.format("%s (%s)", date, ago);
        }
        return MarkdownUtil.bold(ctx.getLang().i18n("mc.player.history.original"));
    }

    private static @NonNull MessageEmbed constructBaseEmbed(CommandContext ctx, Player player) {
        String title = ctx.i18nf("title", player.getUsername());
        String nameMCUrl = player.getNameMCUrl().toString();
        String avatarUrl = player.getAvatarUrl().toString();
        return ctx.brand(new EmbedBuilder())
                .setAuthor(title, nameMCUrl, avatarUrl)
                .build();
    }

    private static void replyWithPartition(CommandContext ctx, MessageEmbed baseEmbed, CharSequence history) {
        EmbedBuilder eb = new EmbedBuilder(baseEmbed); // copying required since EmbedBuilder is mutable
        if (history.length() > MessageEmbed.TEXT_MAX_LENGTH) {
            addFields(ctx, history, eb);
        } else {
            eb.setDescription(history);
        }
        ctx.replyRaw(eb); // remember, base embed is already branded
    }
    private static void addFields(CommandContext ctx, CharSequence history, EmbedBuilder eb) {
        String title = ctx.getLang().i18n("mc.player.history.nameHistory");
        int remainingLength = MessageEmbed.VALUE_MAX_LENGTH - title.length();
        List<String> lines = Splitter.on('\n').splitToList(history);
        List<String> partitions = StringUtils.partitionLinesByLength(lines, remainingLength);
        for (String fieldValue : partitions) {
            eb.addField(title, fieldValue, false);
        }
    }

}
