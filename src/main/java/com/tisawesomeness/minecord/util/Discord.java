package com.tisawesomeness.minecord.util;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.sharding.ShardManager;

import javax.annotation.Nullable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Discord {
    private Discord() {}

    public static final Pattern ANY_MENTION = Pattern.compile("<(@(!?|&)|#|:(.{2,32}):)\\d{17,20}>");

    private final static Pattern DISCORD_ID = Pattern.compile("\\d{17,20}");

    public static User findUser(String search, ShardManager sm) {
        Matcher ma = Pattern.compile("(<@!?)?([0-9]{9,20})>?").matcher(search);
        return ma.matches() ? sm.getUserById(ma.group(2)) : null;
    }

    public static TextChannel findChannel(String search, ShardManager sm) {
        Matcher ma = Pattern.compile("(<#)?([0-9]{9,20})>?").matcher(search);
        return ma.matches() ? sm.getTextChannelById(ma.group(2)) : null;
    }

    /**
     * Checks if the given string is in the correct ID format.
     * <br>This does not necessarily mean the ID correspond to a real Discord snowflake.
     * <br>If true, {@link Long#parseLong(String id)} will not fail.
     * @param id The string id
     * @return True if the string only contains 17-20 digits
     */
    public static boolean isDiscordId(CharSequence id) {
        return DISCORD_ID.matcher(id).matches();
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
    public static List<MessageEmbed> splitEmbeds(@NonNull MessageEmbed baseEmbed, @NonNull String fieldTitle,
                                                 List<String> partitions, @NonNull String joiner) {
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
    private static int getMaxDescriptionLength(@NonNull MessageEmbed baseEmbed) {
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
    public static MessageEmbed addFieldsUntilFullNoCopy(@NonNull EmbedBuilder eb, @NonNull String fieldTitle,
                                                        List<String> fieldValues) {
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

    /**
     * Isolates the command name and args from the prefix, but only if the string actually contains the prefix.
     * For example, {@code &profile Dinnerbone} becomes {@code profile Dinnerbone}.
     * @param content The raw content of the message
     * @param prefix The current prefix, see
     *               {@link com.tisawesomeness.minecord.setting.impl.PrefixSetting#resolve(String)
     *               PrefixSetting.resolve(String)} for valid prefix rules
     * @param options The configuration options that determine how commands can be parsed
     * @return The command name and args in a single string, or empty if the prefix is not present
     * @see #parseOptionsBuilder()
     */
    public static Optional<String> parseCommand(@NonNull String content, @NonNull String prefix,
                                                @NonNull ParseOptions options) {
        if (options.respondToMentions) {
            // mentions may either be <@id> or <@!id>
            int endIndex = content.indexOf('>');
            if (endIndex >= 0 && content.startsWith("<@")) {
                int idStartIndex = content.charAt(2) == '!' ? 3 : 2;
                if (content.substring(idStartIndex, endIndex).equals(options.selfId)) {
                    // safe substring necessary to prevent index OOB when no command name is present
                    return Optional.of(Strings.safeSubstring(content, endIndex + 2));
                }
            }
        }

        if (content.startsWith(prefix)) {
            // prevents OOB
            if (content.length() == prefix.length()) {
                return Optional.of("");
            }
            // don't include extra space in the returned command
            if (content.charAt(prefix.length()) == ' ') {
                return Optional.of(content.substring(prefix.length() + 1));
            }
            return Optional.of(content.substring(prefix.length()));
        }
        return options.prefixRequired ? Optional.empty() : Optional.of(content);
    }
    /**
     * @return A new builder for use in {@link #parseCommand(String, String, ParseOptions)}
     */
    public static ParseBuilder parseOptionsBuilder() {
        return new ParseBuilder();
    }

    /**
     * Contains all the options that determine how a command prefix should be parsed.
     */
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ParseOptions {
        private final boolean respondToMentions;
        private final @Nullable String selfId; // null only if respondToMentions false
        private final boolean prefixRequired;
    }
    /**
     * Builds a {@link ParseOptions}.
     */
    public static class ParseBuilder {
        private boolean respondToMentions;
        private String selfId;
        private boolean prefixRequired = true;

        /**
         * Changes whether a mention counts as a prefix
         * @param respondToMentions Whether the bot should respond to mentions
         * @param selfId The ID of the bot user
         * @return This builder
         * @throws NullPointerException If {@code respondToMentions} is true and {@code selfId} is null
         */
        public ParseBuilder respondToMentions(boolean respondToMentions, @Nullable String selfId) {
            if (respondToMentions) {
                if (selfId == null) {
                    throw new NullPointerException("Self ID cannot be null if the bot is responding to mentions");
                }
                this.selfId = selfId;
            }
            this.respondToMentions = respondToMentions;
            return this;
        }
        /**
         * Changes whether a prefix is required
         * @param prefixRequired Whether a prefix is required
         * @return This builder
         */
        public ParseBuilder prefixRequired(boolean prefixRequired) {
            this.prefixRequired = prefixRequired;
            return this;
        }
        /**
         * @return The built parse options object
         */
        public ParseOptions build() {
            return new ParseOptions(respondToMentions, selfId, prefixRequired);
        }
    }

}
