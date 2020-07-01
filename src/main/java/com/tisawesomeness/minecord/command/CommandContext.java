package com.tisawesomeness.minecord.command;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.setting.SettingRegistry;
import com.tisawesomeness.minecord.setting.impl.UseMenusSetting;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.With;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nullable;

/**
 * Stores the information available to every command
 */
@RequiredArgsConstructor
public class CommandContext {
    /**
     * The arguments given to the command, split by spaces. May be length 0.
     */
    @With
    public final @NonNull String[] args;
    /**
     * The event that triggered the command.
     */
    public final @NonNull MessageReceivedEvent e;
    /**
     * The loaded config.
     */
    public final @NonNull Config config;
    /**
     * A link to the bot instance.
     */
    public final @NonNull Bot bot;
    /**
     * Whether the user executing the command is elevated.
     */
    public final boolean isElevated;
    /**
     * The current prefix. Either guild-specific or the config default for DMs.
     */
    public final @NonNull String prefix;

    public CommandContext(@NonNull String[] args, @NonNull MessageReceivedEvent e, @NonNull Bot bot,
                          @NonNull Config config, boolean isElevated, @NonNull String prefix,
                          @NonNull SettingRegistry settings) {
        this(args, e, config, bot, isElevated, prefix, settings.useMenus);
    }

    private final @NonNull UseMenusSetting useMenusSetting;
    public boolean shouldUseMenus() {
        return useMenusSetting.getEffective(this);
    }

    /**
     * Logs a message to the logging channel.
     */
    public void log(String m) {
        bot.log(m);
    }
    /**
     * Logs a message to the logging channel.
     */
    public void log(Message m) {
        bot.log(m);
    }
    /**
     * Logs a message to the logging channel.
     */
    public void log(MessageEmbed m) {
        bot.log(m);
    }

    /**
     * Formats a message with URL to look more fancy using an embed.
     * Pass {@code null} in any argument to remove that part of the message.
     * @param title The title or header of the message.
     * @param body The main body or description of the message.
     * @return The EmbedBuilder with the added info and bot branding.
     */
    public EmbedBuilder embedMessage(@Nullable String title, @Nullable String body) {
        return brand(new EmbedBuilder()
                .setTitle(title)
                .setDescription(body));
    }

    /**
     * Formats a message with URL to look more fancy using an embed.
     * Pass {@code null} in any argument to remove that part of the message.
     * @param title The title or header of the message.
     * @param url A URL that the title goes to when clicked.
     * @param body The main body or description of the message.
     * @return The EmbedBuilder with the added info and bot branding.
     */
    public EmbedBuilder embedURL(@Nullable String title, @Nullable String url, @Nullable String body) {
        return brand(new EmbedBuilder()
                .setTitle(title, url)
                .setDescription(body));
    }

    /**
     * Formats an image to look more fancy using an embed.
     * Pass {@code null} in any argument to remove that part of the message.
     * @param title The title or header.
     * @param url The URL of the image.
     * @return The EmbedBuilder with the added info and bot branding.
     */
    public EmbedBuilder embedImage(@Nullable String title, @Nullable String url) {
        return brand(new EmbedBuilder()
                .setTitle(title)
                .setImage(url));
    }

    /**
     * Adds the footer with the rolled announcement to an embed.
     * @param eb The given EmbedBuilder.
     * @return The same builder with added footer.
     */
    public EmbedBuilder addFooter(EmbedBuilder eb) {
        return eb.setFooter(bot.getAnnounceRegistry().roll());
        // TODO temporarily disabled (change to static image)
//		if (Config.getOwner().equals("0")) {
//			return eb.setFooter(announcement);
//		}
//		User owner = Bot.shardManager.retrieveUserById(Config.getOwner()).complete();
//		return eb.setFooter(announcement, owner.getAvatarUrl());
    }

    /**
     * Adds the bot color and a random announcement to an embed.
     * @param eb The given EmbedBuilder.
     * @return The same builder with added branding.
     */
    public EmbedBuilder brand(EmbedBuilder eb) {
        return addFooter(eb).setColor(Bot.color);
    }

}
