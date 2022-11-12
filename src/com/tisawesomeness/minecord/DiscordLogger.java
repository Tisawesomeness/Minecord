package com.tisawesomeness.minecord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class DiscordLogger {

    private final String logChannelId;
    private final String joinLogChannelId;

    public DiscordLogger() {
        if (Config.getLogChannel().equals("0")) {
            logChannelId = null;
            System.out.println("Config missing log channel, some messages will be missed!");
        } else {
            logChannelId = Config.getLogChannel();
        }
        if (Config.getJoinLogChannel().equals("0")) {
            joinLogChannelId = null;
            System.out.println("Config missing join log channel");
        } else {
            joinLogChannelId = Config.getLogChannel();
        }
    }

    public void log(String str) {
        log(MessageCreateData.fromContent(str));
    }
    public void log(MessageCreateData message) {
        logInternal(message, logChannelId, "Log");
    }
    public void joinLog(MessageCreateData message) {
        logInternal(message, joinLogChannelId, "Join log");
    }

    private void logInternal(MessageCreateData message, String channelId, String channelName) {
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
