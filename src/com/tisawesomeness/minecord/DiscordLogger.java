package com.tisawesomeness.minecord;

import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.external.JDAWebhookClient;
import club.minnced.discord.webhook.send.*;
import dev.failsafe.RateLimiter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import okhttp3.OkHttpClient;

import javax.annotation.Nullable;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class DiscordLogger {

    private final @Nullable String debugLogChannelId;
    private final RateLimiter<Object> debugRateLimiter;
    private final @Nullable String joinLogChannelId;
    private final @Nullable JDAWebhookClient logWebhookClient;
    private final @Nullable JDAWebhookClient statusWebhookClient;

    public DiscordLogger(OkHttpClient httpClient) {

        if (Config.getLogChannel().equals("0")) {
            debugLogChannelId = null;
            System.out.println("Config missing log channel, some messages will be missed!");
        } else {
            debugLogChannelId = Config.getLogChannel();
        }
        debugRateLimiter = RateLimiter.burstyBuilder(5, Duration.ofMinutes(1)).build();

        if (Config.getJoinLogChannel().equals("0")) {
            joinLogChannelId = null;
            System.out.println("Config missing join log channel");
        } else {
            joinLogChannelId = Config.getJoinLogChannel();
        }

        if (Config.getLogWebhook().isEmpty()) {
            logWebhookClient = null;
            System.out.println("Config missing webhook URL, some messages will be missed!");
        } else {
            logWebhookClient = buildWebhookClient(httpClient, Config.getLogWebhook());
        }

        if (Config.getStatusWebhook().isEmpty()) {
            statusWebhookClient = null;
            System.out.println("Config missing status webhook URL, some messages will be missed!");
        } else {
            statusWebhookClient = buildWebhookClient(httpClient, Config.getStatusWebhook());
        }

    }

    private static JDAWebhookClient buildWebhookClient(OkHttpClient httpClient, String url) {
        return new WebhookClientBuilder(url)
                .setHttpClient(httpClient)
                .setAllowedMentions(AllowedMentions.none())
                .setDaemon(true)
                .buildJDA();
    }

    public void log(String str) {
        if (logWebhookClient != null) {
            logWebhookClient.send(str);
        }
    }
    public void log(MessageCreateData message) {
        if (logWebhookClient != null) {
            logWebhookClient.send(toWebhookMessage(message));
        }
    }
    public void statusLog(String str) {
        if (statusWebhookClient != null) {
            statusWebhookClient.send(str);
        }
    }

    private static WebhookMessage toWebhookMessage(MessageCreateData message) {
        List<WebhookEmbed> embeds = message.getEmbeds().stream()
                .map(WebhookEmbedBuilder::fromJDA)
                .map(WebhookEmbedBuilder::build)
                .collect(Collectors.toList());
        WebhookMessageBuilder builder = new WebhookMessageBuilder()
                .setContent(message.getContent())
                .setTTS(message.isTTS())
                .addEmbeds(embeds);
        message.getFiles().forEach(f -> builder.addFile(f.getName(), f.getData()));
        return builder.build();
    }

    public void debugLog(String str) {
        debugLog(MessageCreateData.fromContent(str));
    }
    public void debugLog(MessageCreateData message) {
        if (debugRateLimiter.tryAcquirePermit()) {
            channelLog(message, debugLogChannelId, "Log");
        } else {
            System.err.println("Debug log rate limit exceeded, Discord message dropped");
        }
    }
    public void joinLog(String str) {
        joinLog(MessageCreateData.fromContent(str));
    }
    public void joinLog(MessageCreateData message) {
        channelLog(message, joinLogChannelId, "Join log");
    }

    private void channelLog(MessageCreateData message, String channelId, String channelName) {
        if (channelId != null) {
            TextChannel tc = Bot.shardManager.getTextChannelById(channelId);
            if (tc == null) {
                System.err.println(channelName + " channel not found!");
            } else {
                List<MessageEmbed> embeds = message.getEmbeds().stream()
                        .map(DiscordLogger::addTimestamp)
                        .collect(Collectors.toList());
                MessageCreateData updatedMessage = MessageCreateBuilder.from(message).setEmbeds(embeds).build();
                tc.sendMessage(updatedMessage).queue();
            }
        }
    }
    private static MessageEmbed addTimestamp(MessageEmbed embed) {
        if (embed.getTimestamp() == null) {
            EmbedBuilder eb = new EmbedBuilder(embed);
            eb.setTimestamp(OffsetDateTime.now());
            return eb.build();
        }
        return embed;
    }

}
