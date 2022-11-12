package com.tisawesomeness.minecord.util;

import com.tisawesomeness.minecord.Announcement;
import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.database.Database;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.Color;
import java.util.regex.Pattern;

public class MessageUtils {

    /**
     * If the message is over the content length limit, trim it down by ending it with an ellipsis.
     * @param msg the message in a code block (ending in ```)
     * @return the trimmed message
     */
    public static String trimCodeblock(String msg) {
        if (msg.length() <= Message.MAX_CONTENT_LENGTH) {
            return msg;
        }
        return msg.substring(0, Message.MAX_CONTENT_LENGTH - 6) + "...```";
    }

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
        return eb.setFooter(announcement, Bot.ownerAvatarUrl);
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

}
