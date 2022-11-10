package com.tisawesomeness.minecord.util;

import com.tisawesomeness.minecord.Announcement;
import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.database.Database;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.Color;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class MessageUtils {

    /**
     * Formats a message to look more fancy using an embed. Pass null in any argument (except color) to remove that aspect of the message.
     * @param title The title or header of the message.
     * @param url A URL that the title goes to when clicked. Only works if title is not null.
     * @param body The main body of the message.
     * @param color The color of the embed. Discord markdown formatting and newline are supported.
     * @return A MessageEmbed representing the message. You can add additional info (e.g. fields) by passing this variable into a new EmbedBuilder.
     */
    public static MessageEmbed embedMessage(String title, String url, String body, Color color) {
        EmbedBuilder eb = new EmbedBuilder();
        if (title != null) eb.setTitle(title, url);
        eb.setDescription(body);
        eb.setColor(color);
        eb = addFooter(eb);
        return eb.build();
    }

    public static EmbedBuilder addFooter(EmbedBuilder eb) {
        String announcement = Announcement.rollAnnouncement();
        if (Config.getOwner().equals("0")) {
            return eb.setFooter(announcement);
        }
        User owner = Bot.shardManager.retrieveUserById(Config.getOwner()).complete();
        return eb.setFooter(announcement, owner.getAvatarUrl());
    }

    /**
     * Logs a message to the logging channel.
     */
    public static void log(String m) {
        if (!Config.getLogChannel().equals("0")) {
            TextChannel tc = Bot.shardManager.getTextChannelById(Config.getLogChannel());
            if (tc == null) {
                System.err.println("Logging channel not found!");
            } else {
                tc.sendMessage(m).queue();
            }
        }
    }
    /**
     * Logs a message to the logging channel.
     */
    public static void log(MessageEmbed m) {
        if (!Config.getLogChannel().equals("0")) {
            EmbedBuilder eb = new EmbedBuilder(m);
            eb.setTimestamp(OffsetDateTime.now());
            TextChannel tc = Bot.shardManager.getTextChannelById(Config.getLogChannel());
            if (tc == null) {
                System.err.println("Logging channel not found!");
            } else {
                tc.sendMessageEmbeds(eb.build()).queue();
            }
        }
    }
    /**
     * Logs a message to the join log channel.
     */
    public static void logJoin(MessageEmbed m) {
        if (!Config.getJoinLogChannel().equals("0")) {
            EmbedBuilder eb = new EmbedBuilder(m);
            eb.setTimestamp(OffsetDateTime.now());
            TextChannel tc = Bot.shardManager.getTextChannelById(Config.getJoinLogChannel());
            if (tc == null) {
                System.err.println("Join log channel not found!");
            } else {
                tc.sendMessageEmbeds(eb.build()).queue();
            }
        }
    }

    /**
     * Gets the command-useful content of a message, keeping the name and arguments and purging the prefix and mention.
     */
    public static String[] getContent(Message m, String prefix, SelfUser su) {
        String content = m.getContentRaw();
        if (m.getContentRaw().startsWith(prefix)) {
            return content.replaceFirst(Pattern.quote(prefix), "").split(" ");
        } else if (content.replace("@!", "@").startsWith(su.getAsMention())) {
            String[] args = content.split(" ");
            return ArrayUtils.remove(args, 0);
        } else {
            return null;
        }
    }

    /**
     * Gets the prefix the bot should use in a text or private channel
     * @param e The event corresponding to a command
     * @return The configured prefix if e is for a text channel, or the default otherwise
     */
    public static String getPrefix(MessageReceivedEvent e) {
        return e.isFromGuild() ? Database.getPrefix(e.getGuild().getIdLong()) : Config.getPrefix();
    }

    /**
     * Splits a list of partitions into one or more embeds. If the total length of the partitions (or remaining
     * partitions if some have been already used) can fit into an embed description, those partitions are joined
     * with the joiner and placed into the description. Otherwise, fields are added, one partition per field, until
     * the max embed length is reached.
     * @param baseEmbed The base embed to add description/fields to, which will be copied if multiple embeds are needed,
     *                  the description will be removed
     * @param fieldTitle The title of each field if fields are used
     * @param partitions A list of partitions, inseparable strings that will be placed into the embed
     * @param joiner The joiner used to join partitions that can fit into an embed
     * @return A list of message embeds, empty if the input partitions is empty
     */
    public static List<MessageEmbed> splitEmbeds(MessageEmbed baseEmbed, String fieldTitle, List<String> partitions,
                                                 String joiner) {
        if (partitions.isEmpty()) {
            return Collections.emptyList();
        }
        int maxDescriptionLength = getMaxDescriptionLength(baseEmbed);
        boolean anyOverMaxLength = partitions.stream()
                .mapToInt(String::length)
                .anyMatch(x -> x > maxDescriptionLength);
        if (anyOverMaxLength) {
            throw new IllegalArgumentException("An input partition was over the max length " + maxDescriptionLength);
        }

        LinkedList<String> parts = new LinkedList<>(partitions);
        List<MessageEmbed> embs = new ArrayList<>();
        while (!parts.isEmpty()) {
            EmbedBuilder eb = new EmbedBuilder(baseEmbed);
            eb.setDescription(null);

            int totalLength = parts.stream()
                    .mapToInt(String::length)
                    .sum() + (parts.size() - 1) * joiner.length();
            if (totalLength <= maxDescriptionLength) {
                eb.setDescription(String.join(joiner, parts));
                embs.add(eb.build());
                return Collections.unmodifiableList(embs);
            }

            addFieldsUntilFullNoCopy(eb, fieldTitle, parts);
            embs.add(eb.build());
        }
        return Collections.unmodifiableList(embs);
    }
    private static int getMaxDescriptionLength(MessageEmbed baseEmbed) {
        String description = baseEmbed.getDescription();
        int descriptionLength = description == null ? 0 : description.length();
        int remainingEmbedLength = MessageEmbed.EMBED_MAX_LENGTH_BOT + descriptionLength - baseEmbed.getLength();
        return Math.min(MessageEmbed.TEXT_MAX_LENGTH, remainingEmbedLength);
    }

    /**
     * Add fields to an embed builder until it is full.
     * @param eb The embed builder to add onto, <b>will be modified</b>
     * @param fieldTitle The field title
     * @param fieldValues A possibly-empty list of field values/descriptions, <b>must be mutable, used fields will be
     *                    removed from the list</b>
     * @return The embed with fields added
     */
    public static MessageEmbed addFieldsUntilFullNoCopy(EmbedBuilder eb, String fieldTitle, List<String> fieldValues) {
        while (!fieldValues.isEmpty()) {
            int lengthIfLineAdded = eb.length() + fieldTitle.length() + fieldValues.get(0).length();
            if (lengthIfLineAdded > MessageEmbed.EMBED_MAX_LENGTH_BOT) {
                return eb.build();
            }
            String nextFieldValue = fieldValues.remove(0);
            eb.addField(fieldTitle, nextFieldValue, false);
        }
        return eb.build();
    }

}
